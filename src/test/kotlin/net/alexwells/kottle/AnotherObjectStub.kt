package net.alexwells.kottle

import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object AnotherObjectStub {
    private val logger: Logger = LogManager.getLogger()

    fun registerItems(event: RegistryEvent.Register<Item>) {
        logger.info("HELLO from registerItems")
    }
}