package com.github.insanusmokrassar.AutoPostSmartTimerTrigger

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

private fun nowTime(): DateTime {
    return DateTime.now().millisOfDay().dateTime
}

class SmartTimerTriggerPlugin(
    params: IObject<Any>
) : Plugin {
    private val triggerTimes: Queue<DateTime> = params.toObject(
        SmartTimerConfig::class.java
    ).triggerTimes.let {
        ArrayDeque(it)
    }

    init {
        val now = nowTime()
        val first = triggerTimes.peek()
        while (triggerTimes.peek().isAfter(now)) {
            triggerTimes.offer(
                triggerTimes.poll()
            )
            if (triggerTimes.peek() == first) {
                break
            }
        }
    }

    override fun onInit(bot: TelegramBot, baseConfig: FinalConfig, pluginManager: PluginManager) {
        super.onInit(bot, baseConfig, pluginManager)

        val chooser = pluginManager.plugins.firstOrNull { it is Chooser } as? Chooser ?:let {
            pluginLogger.warning(
                "$name can't be init: chooser is absent"
            )
            return
        }
        val publisher = pluginManager.plugins.firstOrNull { it is Publisher } as? Publisher ?:let {
            pluginLogger.warning(
                "$name can't be init: publisher is absent"
            )
            return
        }

        launch {
            while (isActive) {
                val current = triggerTimes.poll()

                val nowTime = nowTime()

                delay(
                    current.millis - nowTime.millis
                )

                try {
                    chooser.triggerChoose().forEach {
                        try {
                            publisher.publishPost(it)
                        } catch (e: Exception) {
                            pluginLogger.throwing(
                                this@SmartTimerTriggerPlugin::class.java.simpleName,
                                "trigger publishing",
                                e
                            )
                        }
                    }
                } catch (e: Exception) {
                    pluginLogger.throwing(
                        this@SmartTimerTriggerPlugin::class.java.simpleName,
                        "trigger choosing",
                        e
                    )
                }

                triggerTimes.offer(current)
            }
        }
    }
}