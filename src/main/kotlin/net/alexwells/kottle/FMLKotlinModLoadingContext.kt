package net.alexwells.kottle

import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.ModLoadingContext

/**
 * Similar to FMLModLoadingContext.
 */
object FMLKotlinModLoadingContext {
    fun get(): Context {
        return ModLoadingContext.get().extension()
    }

    class Context(private val container: FMLKotlinModContainer) {
        val modEventBus: IEventBus
            get() = container.eventBus
    }
}