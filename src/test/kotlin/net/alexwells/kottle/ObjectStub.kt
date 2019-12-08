package net.alexwells.kottle

import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object ObjectStub {
    private val logger: Logger = LogManager.getLogger()

    @SubscribeEvent
    fun registerItems(event: RegistryEvent.Register<Item>) {
        logger.info("HELLO from registerItems")
    }
}
