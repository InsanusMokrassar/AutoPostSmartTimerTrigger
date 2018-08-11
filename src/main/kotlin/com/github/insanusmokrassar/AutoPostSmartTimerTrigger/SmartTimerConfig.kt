package com.github.insanusmokrassar.AutoPostSmartTimerTrigger

import org.joda.time.DateTime

data class SmartTimerConfig (
    val items: List<SmartTimerConfigTimeItem> = emptyList()
) {
    val triggerTimes: List<DateTime> by lazy {
        items.flatMap {
            it.triggerTimes
        }.sorted()
    }
}
