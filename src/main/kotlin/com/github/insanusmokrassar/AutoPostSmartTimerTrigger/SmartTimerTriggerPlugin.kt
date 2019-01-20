package com.github.insanusmokrassar.AutoPostSmartTimerTrigger

import com.github.insanusmokrassar.AutoPostSmartTimerTrigger.utils.near
import com.github.insanusmokrassar.AutoPostSmartTimerTrigger.utils.nowTime
import com.github.insanusmokrassar.AutoPostTelegramBot.base.models.FinalConfig
import com.github.insanusmokrassar.AutoPostTelegramBot.base.plugins.*
import com.github.insanusmokrassar.AutoPostTelegramBot.plugins.choosers.Chooser
import com.github.insanusmokrassar.AutoPostTelegramBot.plugins.publishers.Publisher
import com.github.insanusmokrassar.AutoPostTelegramBot.utils.NewDefaultCoroutineScope
import org.joda.time.DateTime
import java.util.*
import com.github.insanusmokrassar.TelegramBotAPI.bot.RequestsExecutor
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

private val SmartTimerTriggerPluginScope = NewDefaultCoroutineScope(1)

@Serializable
class SmartTimerTriggerPlugin(
    private val config: SmartTimerConfig
) : Plugin {
    @Transient
    private val triggerTimes: List<DateTime>
        get() = config.triggerTimes

    override suspend fun onInit(
        executor: RequestsExecutor,
        baseConfig: FinalConfig,
        pluginManager: PluginManager
    ) {
        super.onInit(executor, baseConfig, pluginManager)

        val chooser = pluginManager.plugins.firstOrNull { it is Chooser } as? Chooser ?:let {
            commonLogger.warning(
                "$name can't be init: chooser is absent"
            )
            return
        }
        val publisher = pluginManager.plugins.firstOrNull { it is Publisher } as? Publisher ?:let {
            commonLogger.warning(
                "$name can't be init: publisher is absent"
            )
            return
        }

        SmartTimerTriggerPluginScope.launch {
            while (isActive) {
                val current = triggerTimes.near()

                val nowTime = nowTime()

                current ?.also {
                    delay(
                        it.millis - nowTime.millis + 1
                    )
                } ?: break

                try {
                    chooser.triggerChoose().forEach {
                        try {
                            publisher.publishPost(it)
                        } catch (e: Exception) {
                            commonLogger.throwing(
                                this@SmartTimerTriggerPlugin::class.java.simpleName,
                                "trigger publishing",
                                e
                            )
                        }
                    }
                } catch (e: Exception) {
                    commonLogger.throwing(
                        this@SmartTimerTriggerPlugin::class.java.simpleName,
                        "trigger choosing",
                        e
                    )
                }
            }
        }
    }
}