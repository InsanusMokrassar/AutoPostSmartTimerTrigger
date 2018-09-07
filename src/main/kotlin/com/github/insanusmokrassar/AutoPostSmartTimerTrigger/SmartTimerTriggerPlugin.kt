package com.github.insanusmokrassar.AutoPostSmartTimerTrigger

import com.github.insanusmokrassar.AutoPostSmartTimerTrigger.utils.near
import com.github.insanusmokrassar.AutoPostSmartTimerTrigger.utils.nowTime
import com.github.insanusmokrassar.AutoPostTelegramBot.base.models.FinalConfig
import com.github.insanusmokrassar.AutoPostTelegramBot.base.plugins.*
import com.github.insanusmokrassar.AutoPostTelegramBot.plugins.choosers.Chooser
import com.github.insanusmokrassar.AutoPostTelegramBot.plugins.publishers.Publisher
import com.github.insanusmokrassar.IObjectK.interfaces.IObject
import com.github.insanusmokrassar.IObjectKRealisations.toObject
import com.pengrad.telegrambot.TelegramBot
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.joda.time.DateTime
import java.util.*

class SmartTimerTriggerPlugin(
    params: IObject<Any>
) : Plugin {
    private val triggerTimes: List<DateTime> = params.toObject(
        SmartTimerConfig::class.java
    ).triggerTimes

    override fun onInit(bot: TelegramBot, baseConfig: FinalConfig, pluginManager: PluginManager) {
        super.onInit(bot, baseConfig, pluginManager)

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

        launch {
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