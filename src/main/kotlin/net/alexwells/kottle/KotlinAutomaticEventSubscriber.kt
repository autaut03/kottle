package net.alexwells.kottle

import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.Logging
import net.minecraftforge.fml.ModContainer
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation
import net.minecraftforge.forgespi.language.ModFileScanData
import org.apache.logging.log4j.LogManager
import org.objectweb.asm.Type

/**
 * Similar to AutomaticEventSubscriber, but with support for kotlin objects.
 */
@Suppress("DEPRECATION")
object KotlinAutomaticEventSubscriber {
    private val FORGE_SUBSCRIBER = Type.getType(Mod.EventBusSubscriber::class.java)

    private val logger = LogManager.getLogger()

    fun inject(mod: ModContainer, scanData: ModFileScanData?, loader: ClassLoader) = if (scanData == null) run { } else scanData.annotations
            .also { logger.debug(Logging.LOADING, "Attempting to inject @EventBusSubscriber classes into the eventbus for ${mod.modId}") }
            .filter { it.annotationType == FORGE_SUBSCRIBER && shouldBeRegistered(mod.modId, it) }
            .forEach { ad ->
                val busTarget = (ad.annotationData["bus"] as? ModAnnotation.EnumHolder)
                        ?.let { Mod.EventBusSubscriber.Bus.valueOf(it.value) }
                        ?: Mod.EventBusSubscriber.Bus.FORGE

                try {
                    logger.debug(Logging.LOADING, "Auto-subscribing ${ad.classType.className} to $busTarget")
                    val clazz = Class.forName(ad.classType.className, true, loader)
                    (if (busTarget == Mod.EventBusSubscriber.Bus.MOD) FMLKotlinModLoadingContext.get().modEventBus else MinecraftForge.EVENT_BUS)
                            .register(clazz.kotlin.objectInstance ?: clazz)
                } catch (e: ClassNotFoundException) {
                    logger.fatal(Logging.LOADING, "Failed to load mod class ${ad.classType} for @EventBusSubscriber annotation", e)
                    throw e
                }
            }

    private fun shouldBeRegistered(modId: String, ad: ModFileScanData.AnnotationData): Boolean {
        @Suppress("UNCHECKED_CAST")
        val sides = (ad.annotationData["value"] as? List<ModAnnotation.EnumHolder>)
                ?.map { Dist.valueOf(it.value) }
                ?: listOf(Dist.CLIENT, Dist.DEDICATED_SERVER)

        val annotationModId = ad.annotationData.getOrDefault("modid", modId) as String

        return modId == annotationModId && FMLEnvironment.dist in sides
    }
}
