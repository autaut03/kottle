package net.alexwells.kottle

import net.minecraftforge.fml.ModLoadingContext

val modLoadingContext: Context get() = ModLoadingContext.get().extension()

class Context(private val container: FMLKotlinModContainer) {
    val modEventBus get() = container.eventBus
}