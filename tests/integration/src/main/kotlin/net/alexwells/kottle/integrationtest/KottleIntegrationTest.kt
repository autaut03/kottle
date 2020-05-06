@file:Suppress("unused")

package net.alexwells.kottle.integrationtest

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.minecraft.client.Minecraft
import net.minecraft.server.dedicated.DedicatedServer
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.concurrent.CompletableFuture
import java.util.stream.Stream
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.memberFunctions
import kotlin.streams.asSequence


@Mod("kottle_integration_test")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object KottleIntegrationTest {
    private val logger: Logger = LogManager.getLogger()

    @OnlyIn(Dist.DEDICATED_SERVER)
    private var server: DedicatedServer? = null

    private var job: Job? = null

    @SubscribeEvent
    fun onCommonSetup(event: FMLCommonSetupEvent) {
        Stream.of(1, 2, 3).asSequence()
                .sum()
                .also { logger.info("Base Kotlin (with JRE8 Extension) Test: 1+2+3 = $it") }

        ReflectionTest::class
                .companionObject!!
                .memberFunctions
                .find { it.name == "getThing" }!!
                .call(ReflectionTest::class.companionObjectInstance!!)
                .also { logger.info("Kotlin Reflection Test: $it") }

        val future: CompletableFuture<String> = CompletableFuture.supplyAsync {
            Thread.sleep(2000L)
            "success!"
        }

        // https://github.com/autaut03/kottle/pull/26
        logger.info("Random int: ${listOf(1, 2, 3).random()}")

        job = GlobalScope.launch {
            logger.info("Future dispatched")
            future.await().also { logger.info("Kotlin Coroutine (with JRE8 Extension) Test: $it") }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    fun onClientLoadComplete(event: FMLLoadCompleteEvent) {
        cleanUp()
        Minecraft.getInstance().shutdown()
    }

    @SubscribeEvent
    @OnlyIn(Dist.DEDICATED_SERVER)
    fun onServerStarted(event: FMLDedicatedServerSetupEvent) {
        server = event.serverSupplier.get()
    }

    @SubscribeEvent
    @OnlyIn(Dist.DEDICATED_SERVER)
    fun onServerLoadComplete(event: FMLLoadCompleteEvent) {
        cleanUp()
        server?.initiateShutdown(false)
    }

    private fun cleanUp() {
        runBlocking {
            logger.info("Waiting for future")
            job?.join()
        }
        logger.info("Kottle integration test was successfully loaded.")
        logger.warn("Please remove this mod if you somehow have this in your modpack. Stopping Minecraft.")
    }
}

class ReflectionTest {
    internal companion object {
        @Suppress("unused")
        internal fun getThing() = "success!"
    }
}
