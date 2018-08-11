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

class SmartTimerTriggerPlugin(
    params: IObject<Any>
) : Plugin {
    private val config: SmartTimerConfig = params.toObject(SmartTimerConfig::class.java)

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
                val chosen = config.items.minBy {
                    it.nextTriggerTime
                } ?.let {
                    delay(it.nextTriggerTime.millis)
                    chooser.triggerChoose()
                } ?: break

                chosen.forEach {
                    launch {
                        publisher.publishPost(it)
                    }
                }
            }
        }
    }
}