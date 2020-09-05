package net.alexwells.kottle

import net.minecraftforge.fml.ModLoadingContext

/**
 * Similar to FMLModLoadingContext.
 */
object FMLKotlinModLoadingContext {
    fun get(): Context = ModLoadingContext.get().extension()

    class Context(private val container: FMLKotlinModContainer) {
        val modEventBus get() = container.eventBus
    }
}
