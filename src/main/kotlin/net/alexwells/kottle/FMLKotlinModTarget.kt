package net.alexwells.kottle

import net.minecraftforge.fml.Logging.LOADING
import net.minecraftforge.forgespi.language.IModInfo
import net.minecraftforge.forgespi.language.IModLanguageProvider
import net.minecraftforge.forgespi.language.ModFileScanData
import org.apache.logging.log4j.LogManager
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaMethod

/**
 * Similar to FMLModTarget, just with ModContainer class changed.
 */
class FMLKotlinModTarget(private val className: String) : IModLanguageProvider.IModLanguageLoader {
    private val logger = LogManager.getLogger()

    @Suppress("UNCHECKED_CAST")
    override fun <T> loadMod(info: IModInfo, modClassLoader: ClassLoader, modFileScanResults: ModFileScanData) = try {
        // We can NOT just instantiate FMLKotlinModContainer() directly, because that would use the wrong class loader,
        // which has a different reference to the ModContainer class, meaning ModLoader will throw CannotCastException
        // as soon as it tries to load any mod. Instead, we'll use the exact same thing as FMLModTarget uses -
        // thread-bound class loader, set somewhere deep in the Forge sources. Do NOT try to change this.
        logger.debug(LOADING, "Loading FMLKotlinModContainer from classloader ${Thread.currentThread().contextClassLoader}")
        Class.forName("net.alexwells.kottle.FMLKotlinModContainer", true, Thread.currentThread().contextClassLoader)
                .also { logger.debug(LOADING, "Loading FMLKotlinModContainer got ${it.classLoader}") }
                .kotlin.primaryConstructor?.javaMethod
                ?.invoke(info, className, modClassLoader, modFileScanResults) as T
    } catch (e: ReflectiveOperationException) {
        logger.fatal(LOADING, "Unable to load FMLKotlinModContainer, wut?", e)
        throw e
    }
}