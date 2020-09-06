package net.alexwells.kottle

import net.minecraftforge.eventbus.EventBusErrorMessage
import net.minecraftforge.eventbus.api.BusBuilder
import net.minecraftforge.eventbus.api.Event
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.eventbus.api.IEventListener
import net.minecraftforge.fml.Logging.LOADING
import net.minecraftforge.fml.ModContainer
import net.minecraftforge.fml.ModLoadingException
import net.minecraftforge.fml.ModLoadingStage
import net.minecraftforge.fml.event.lifecycle.IModBusEvent
import net.minecraftforge.forgespi.language.IModInfo
import net.minecraftforge.forgespi.language.ModFileScanData
import org.apache.logging.log4j.Logger
import java.util.*
import java.util.function.Consumer
import java.util.function.Supplier

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
    private val scanResults: ModFileScanData,
    private val logger: Logger,
    private val eventSubscriber: KotlinAutomaticEventSubscriber,
) : ModContainer(info) {
    private lateinit var mod: Any
    val eventBus: IEventBus = BusBuilder.builder()
        .setExceptionHandler(::onEventFailed)
        .setTrackPhases(false)
        .markerType(IModBusEvent::class.java)
        .build()

    init {
        logger.debug(LOADING, "Creating FMLKotlinModContainer instance for $className with classLoader $modClassLoader & ${javaClass.classLoader}")

        activityMap[ModLoadingStage.CONSTRUCT] = Runnable { constructMod() }
        configHandler = Optional.of(Consumer { event -> eventBus.post(event) })
        val contextExtension = FMLKotlinModLoadingContext.Context(this)
        this.contextExtension = Supplier { contextExtension }
    }

    private fun constructMod() {
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
                logger.error(
                    LOADING,
                    "This seems like a compatibility error between Kottle and the mod. " +
                        "Please make sure identical versions of shadowed libraries are used in the mod and Kottle of this version."
                )
            }

            logger.error(LOADING, "Failed to create mod instance. ModID: $modId, class $className", e)

            throw ModLoadingException(modInfo, ModLoadingStage.CONSTRUCT, "fml.modloading.failedtoloadmod", e, modClass)
        }

        try {
            logger.debug(LOADING, "Injecting Automatic event subscribers for $modId")
            eventSubscriber.inject(this, scanResults, modClass.classLoader)
            logger.debug(LOADING, "Completed Automatic event subscribers for $modId")
        } catch (e: Throwable) {
            logger.error(LOADING, "Failed to register automatic subscribers for $modId, class ${modClass.name}", e)

            throw ModLoadingException(modInfo, ModLoadingStage.CONSTRUCT, "fml.modloading.failedtoloadmod", e, modClass)
        }
    }

    private fun onEventFailed(iEventBus: IEventBus, event: Event, iEventListeners: Array<IEventListener>, i: Int, throwable: Throwable) =
        logger.error(EventBusErrorMessage(event, i, iEventListeners, throwable))

    override fun matches(mod: Any) = mod === this.mod
    override fun getMod() = mod

    override fun <T> acceptEvent(event: T) where T : Event?, T : IModBusEvent? {
        try {
            logger.debug(LOADING, "Firing event for modid $modId : $event")
            eventBus.post(event)
            logger.debug(LOADING, "Fired event for modid $modId : $event")
        } catch (t: Throwable) {
            logger.error(LOADING, "Caught exception during event $event dispatch for modid $modId", t)

            throw ModLoadingException(modInfo, modLoadingStage, "fml.modloading.errorduringevent", t)
        }
    }
}
