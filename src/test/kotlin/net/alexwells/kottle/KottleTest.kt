package net.alexwells.kottle

import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Mod("kottle_test")
object KottleTest {
    val logger: Logger = LogManager.getLogger()

    init {
        // You either need to specify generic type explicitly and use a consumer
        modLoadingContext.modEventBus.addListener<FMLCommonSetupEvent> { setup(it) }
        // use a consumer with parameter types specified
        modLoadingContext.modEventBus.addListener<FMLCommonSetupEvent> { setup2(it) }
        // or just register whole object and mark needed method with SubscribeEvent annotations.
        modLoadingContext.modEventBus.register(this)

        modLoadingContext.modEventBus.register(ObjectStub)

        modLoadingContext.modEventBus.addListener<RegistryEvent.Register<Item>> { AnotherObjectStub.registerItems(it) }
    }

    // You can also use EventBusSubscriber as usual
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    object EventSubscriber {
        @SubscribeEvent
        fun setupNonStatic(event: FMLCommonSetupEvent) {
            logger.info("HELLO from setupNonStatic")
        }
    }

    fun setup(event: FMLCommonSetupEvent) {
        logger.info("HELLO from setup")
    }

    fun setup2(event: FMLCommonSetupEvent) {
        logger.info("HELLO from setup2")
    }

    @SubscribeEvent
    fun setup3(event: FMLCommonSetupEvent) {
        logger.info("HELLO from setup3")
    }
}
