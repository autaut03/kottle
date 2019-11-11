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
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList

/**
 * Similar to AutomaticEventSubscriber, but with support for kotlin objects.
 */
@Suppress("DEPRECATION")
object KotlinAutomaticEventSubscriber {
    private val AUTO_SUBSCRIBER = Type.getType(KotlinEventBusSubscriber::class.java)
    private val EVENT_BUS_SUBSCRIBER = Type.getType(Mod.EventBusSubscriber::class.java)

    private val logger = LogManager.getLogger()

    @Suppress("UNCHECKED_CAST")
    fun inject(mod: ModContainer, scanData: ModFileScanData, classLoader: ClassLoader) {
        logger.debug(Logging.LOADING, "Attempting to inject @EventBusSubscriber kotlin objects in to the event bus for {}", mod.modId)
        val data: ArrayList<ModFileScanData.AnnotationData> = scanData.annotations.stream()
                .filter { annotationData ->
                    EVENT_BUS_SUBSCRIBER == annotationData.annotationType ||
                            AUTO_SUBSCRIBER == annotationData.annotationType
                }
                .collect(Collectors.toList()) as ArrayList<ModFileScanData.AnnotationData>
        data.forEach { annotationData ->
            val sidesValue: List<ModAnnotation.EnumHolder> = annotationData.annotationData.getOrDefault("value", listOf(ModAnnotation.EnumHolder(null, "CLIENT"), ModAnnotation.EnumHolder(null, "DEDICATED_SERVER"))) as List<ModAnnotation.EnumHolder>
            val sides: EnumSet<Dist> = sidesValue.stream().map { eh -> Dist.valueOf(eh.value) }
                    .collect(Collectors.toCollection { EnumSet.noneOf(Dist::class.java) })
            val modid = annotationData.annotationData.getOrDefault("modid", mod.modId)
            val busTargetHolder: ModAnnotation.EnumHolder = annotationData.annotationData.getOrDefault("bus", ModAnnotation.EnumHolder(null, "FORGE")) as ModAnnotation.EnumHolder
            val busTarget = Mod.EventBusSubscriber.Bus.valueOf(busTargetHolder.value)
            val ktObject = Class.forName(annotationData.classType.className, true, classLoader).kotlin.objectInstance
            if (ktObject !== null && mod.modId == modid && sides.contains(FMLEnvironment.dist)) {
                try {
                    logger.debug(Logging.LOADING, "Auto-subscribing kotlin object {} to {}", annotationData.classType.className, busTarget)
                    if (busTarget === Mod.EventBusSubscriber.Bus.MOD) {
                        // Gets the correct mod loading context
                        FMLKotlinModLoadingContext.get().modEventBus.register(ktObject)
                    } else {
                        MinecraftForge.EVENT_BUS.register(ktObject)
                    }
                } catch (e: Throwable) {
                    logger.fatal(Logging.LOADING, "Failed to load mod class {} for @EventBusSubscriber annotation", annotationData.classType, e)
                    throw RuntimeException(e)
                }
            }
        }
    }
}
