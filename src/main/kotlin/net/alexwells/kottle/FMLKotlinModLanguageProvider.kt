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
import java.util.stream.Collectors

/**
 * Similar to FMLJavaModLanguageProvider
 */
class FMLKotlinModLanguageProvider : IModLanguageProvider {
	private val logger = LogManager.getLogger()

	init {
		logger.debug(LOADING, "Init FMLKotlinModLanguageProvider")
	}

	override fun name(): String  = "kotlinfml"

	override fun getFileVisitor(): Consumer<ModFileScanData> {
		return Consumer { scanResult ->
			val modTargetMap = scanResult.annotations.toList().stream()
					.filter { ad -> ad.annotationType == FMLJavaModLanguageProvider.MODANNOTATION }
					.peek { ad -> logger.debug(SCAN, "Found @Mod class {} with id {}", ad.classType.className, ad.annotationData["value"]) }
					.map { ad -> FMLKotlinModTarget(ad.classType.className, ad.annotationData["value"] as String) }
					.collect(Collectors.toMap(java.util.function.Function<FMLKotlinModTarget, String> { it.modId }, java.util.function.Function.identity<FMLKotlinModTarget>()))
			scanResult.addLanguageLoader(modTargetMap)
		}
	}

	override fun <R : ILifecycleEvent<R>?> consumeLifecycleEvent(consumeEvent: Supplier<R>?) {}
}