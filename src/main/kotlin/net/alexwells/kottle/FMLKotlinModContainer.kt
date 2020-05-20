package net.alexwells.kottle

import java.util.*
import java.util.function.Consumer
import java.util.function.Supplier
import net.minecraftforge.eventbus.EventBusErrorMessage
import net.minecraftforge.eventbus.api.BusBuilder
import net.minecraftforge.eventbus.api.Event
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.eventbus.api.IEventListener
import net.minecraftforge.fml.LifecycleEventProvider
import net.minecraftforge.fml.Logging.LOADING
import net.minecraftforge.fml.ModContainer
import net.minecraftforge.fml.ModLoadingException
import net.minecraftforge.fml.ModLoadingStage
import net.minecraftforge.forgespi.language.IModInfo
import net.minecraftforge.forgespi.language.ModFileScanData
import org.apache.logging.log4j.LogManager

/**
 * Similar to FMLModContainer, with few changes:
 * - we do NOT instantiate mod class until CONSTRUCT phase, due to the fact kotlin object's
 *   init {} block becomes static {} after compilation, which leads to unexpected NPEs
 *   if trying to get currently loaded mod container for the event bus.
 * - during CONSTRUCT phase, instead of always doing .newInstance() on a class, we first
 *   check if that's kotlin object instance and return it instead if it is
 * - instead of using default AutomaticEventSubscriber, we're using KotlinAutomaticEventSubscriber
 */
class FMLKotlinModContainer(
    info: IModInfo,
    private val className: String,
    private val modClassLoader: ClassLoader,
    private val scanResults: ModFileScanData
) : ModContainer(info) {
    private val logger = LogManager.getLogger()

    private lateinit var mod: Any
    val eventBus: IEventBus = BusBuilder.builder().setExceptionHandler(::onEventFailed).setTrackPhases(false).build()

    init {
        logger.debug(LOADING, "Creating FMLModContainer instance for $className with classLoader $modClassLoader & ${javaClass.classLoader}")
        triggerMap[ModLoadingStage.CONSTRUCT] = dummy().andThen(::constructMod).andThen(::afterEvent)
        triggerMap[ModLoadingStage.CREATE_REGISTRIES] = dummy().andThen(::fireEvent).andThen(::afterEvent)
        triggerMap[ModLoadingStage.LOAD_REGISTRIES] = dummy().andThen(::fireEvent).andThen(::afterEvent)
        triggerMap[ModLoadingStage.COMMON_SETUP] = dummy().andThen(::fireEvent).andThen(::afterEvent)
        triggerMap[ModLoadingStage.SIDED_SETUP] = dummy().andThen(::fireEvent).andThen(::afterEvent)
        triggerMap[ModLoadingStage.ENQUEUE_IMC] = dummy().andThen(::fireEvent).andThen(::afterEvent)
        triggerMap[ModLoadingStage.PROCESS_IMC] = dummy().andThen(::fireEvent).andThen(::afterEvent)
        triggerMap[ModLoadingStage.COMPLETE] = dummy().andThen(::fireEvent).andThen(::afterEvent)
        triggerMap[ModLoadingStage.GATHERDATA] = dummy().andThen(::fireEvent).andThen(::afterEvent)
        configHandler = Optional.of(Consumer { event -> eventBus.post(event) })
        val contextExtension = Context(this)
        this.contextExtension = Supplier { contextExtension }
    }

    private fun constructMod(event: LifecycleEventProvider.LifecycleEvent) {
        // Here we'll load the class
        val modClass = try {
            Class.forName(className, false, modClassLoader).also {
                logger.debug(LOADING, "Loaded modclass ${it.name} with ${it.classLoader}")
            }
        } catch (e: Throwable) {
            logger.error(LOADING, "Failed to load class $className", e)
            throw ModLoadingException(modInfo, ModLoadingStage.CONSTRUCT, "fml.modloading.failedtoloadmodclass", e)
        }
        // Then we check whether it's a kotlin object and return it, or if not we create a new instance of kotlin class.
        try {
            logger.debug(LOADING, "Loading mod instance $modId of type $className")
            this.mod = modClass.kotlin.objectInstance ?: modClass.getConstructor().newInstance()
            logger.debug(LOADING, "Loaded mod instance $modId of type $className")
        } catch (e: Throwable) {
            if (e is IllegalStateException) {
                logger.error(LOADING, "This seems like a compatibility error between Kottle and the mod. " +
                        "Please make sure identical versions of shadowed libraries are used in the mod and Kottle of this version."
                )
            }

            logger.error(LOADING, "Failed to create mod instance. ModID: $modId, class $className", e)

            throw ModLoadingException(modInfo, event.fromStage(), "fml.modloading.failedtoloadmod", e, modClass)
        }

        logger.debug(LOADING, "Injecting Automatic event subscribers for $modId")
        injectSubscribers(this, scanResults, modClass.classLoader)
        logger.debug(LOADING, "Completed Automatic event subscribers for $modId")
    }

    private fun dummy() = Consumer<LifecycleEventProvider.LifecycleEvent> {}

    private fun fireEvent(lifecycleEvent: LifecycleEventProvider.LifecycleEvent) {
        val event = lifecycleEvent.getOrBuildEvent(this)
        logger.debug(LOADING, "Firing event for modid $modId : $event")
        try {
            eventBus.post(event)
            logger.debug(LOADING, "Fired event for modid $modId : $event")
        } catch (e: Throwable) {
            logger.error(LOADING, "Caught exception during event $event dispatch for modid $modId", e)
            throw ModLoadingException(modInfo, lifecycleEvent.fromStage(), "fml.modloading.errorduringevent", e)
        }
    }

    private fun afterEvent(lifecycleEvent: LifecycleEventProvider.LifecycleEvent) {
        if (currentState == ModLoadingStage.ERROR) {
            logger.error(LOADING, "An error occurred while dispatching event ${lifecycleEvent.fromStage()} to $modId")
        }
    }

    private fun onEventFailed(iEventBus: IEventBus, event: Event, iEventListeners: Array<IEventListener>, i: Int, throwable: Throwable) =
            logger.error(EventBusErrorMessage(event, i, iEventListeners, throwable))

    override fun matches(mod: Any) = mod === this.mod
    override fun getMod() = mod

    override fun acceptEvent(e: Event) {
        eventBus.post(e)
    }
}
