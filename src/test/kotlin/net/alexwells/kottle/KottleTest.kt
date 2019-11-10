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
        FMLKotlinModLoadingContext.get().modEventBus.addListener<FMLCommonSetupEvent> { setup(it) }
        // use a consumer with parameter types specified
        FMLKotlinModLoadingContext.get().modEventBus.addListener { event: FMLCommonSetupEvent -> setup2(event) }
        // or just register whole object and mark needed method with SubscribeEvent annotations.
        FMLKotlinModLoadingContext.get().modEventBus.register(this)

        FMLKotlinModLoadingContext.get().modEventBus.register(ObjectStub)

        FMLKotlinModLoadingContext.get().modEventBus.addListener { event: RegistryEvent.Register<Item> -> AnotherObjectStub.registerItems(event) }
    }

    // You can also use EventBusSubscriber as usual
    @KotlinEventBusSubscriber(bus = KotlinEventBusSubscriber.Bus.MOD)
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
