package net.alexwells.kottle

import net.minecraftforge.fml.Logging.LOADING
import net.minecraftforge.fml.Logging.SCAN
import net.minecraftforge.fml.javafmlmod.FMLJavaModLanguageProvider
import net.minecraftforge.forgespi.language.ILifecycleEvent
import net.minecraftforge.forgespi.language.IModLanguageProvider
import net.minecraftforge.forgespi.language.ModFileScanData
import org.apache.logging.log4j.LogManager
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * Similar to FMLJavaModLanguageProvider
 */
class FMLKotlinModLanguageProvider : IModLanguageProvider {
    private val logger = LogManager.getLogger()

    init {
        logger.debug(LOADING, "Init FMLKotlinModLanguageProvider")
    }

    override fun name() = "kotlinfml"

    override fun getFileVisitor() = Consumer<ModFileScanData> { scanResult ->
        val modTargetMap = scanResult.annotations
                .filter { it.annotationType == FMLJavaModLanguageProvider.MODANNOTATION }
                .map { ad ->
                    val className = ad.classType.className
                    val modId = ad.annotationData["value"] as String
                    logger.debug(SCAN, "Found @Mod class $className with id $modId")
                    modId to FMLKotlinModTarget(className)
                }.toMap()
        scanResult.addLanguageLoader(modTargetMap)
    }

    override fun <R : ILifecycleEvent<R>?> consumeLifecycleEvent(consumeEvent: Supplier<R>?) {}
}