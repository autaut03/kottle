package net.alexwells.kottle

import net.minecraftforge.fml.Logging.LOADING
import net.minecraftforge.forgespi.language.IModInfo
import net.minecraftforge.forgespi.language.IModLanguageProvider
import net.minecraftforge.forgespi.language.ModFileScanData
import org.apache.logging.log4j.LogManager

/**
 * Similar to FMLModTarget, just with ModContainer class changed.
 */
class FMLKotlinModTarget(private val className: String, val modId: String) : IModLanguageProvider.IModLanguageLoader {
    private val logger = LogManager.getLogger()

    override fun <T> loadMod(info: IModInfo, modClassLoader: ClassLoader, modFileScanResults: ModFileScanData): T {
        // We can NOT just instantiate FMLKotlinModContainer() directly, because that would use the wrong class loader,
        // which has a different reference to the ModContainer class, meaning ModLoader will throw CannotCastException
        // as soon as it tries to load any mod. Instead, we'll use the exact same thing as FMLModTarget uses -
        // thread-bound class loader, set somewhere deep in the Forge sources. Do NOT try to change this.
        try {
            logger.debug(LOADING, "Loading FMLKotlinModContainer from classloader {}", Thread.currentThread().contextClassLoader)
            val fmlContainer = Class.forName("net.alexwells.kottle.FMLKotlinModContainer", true, Thread.currentThread().contextClassLoader)
            logger.debug(LOADING, "Loading FMLKotlinModContainer got {}", fmlContainer.classLoader)

            @Suppress("UNCHECKED_CAST")
            return fmlContainer
                    .getConstructor(IModInfo::class.java, String::class.java, ClassLoader::class.java, ModFileScanData::class.java)
                    .newInstance(info, className, modClassLoader, modFileScanResults) as T
        } catch (e: ReflectiveOperationException) {
            logger.fatal(LOADING, "Unable to load FMLKotlinModContainer, wut?", e)
            throw e
        }
    }
}