package com.github.insanusmokrassar.AutoPostSmartTimerTrigger

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.internal.ArrayListSerializer
import org.joda.time.DateTime

@Serializable
data class SmartTimerConfig (
    @Serializable(SmartTimerConfigTimeItemsSerializer::class)
    val items: List<SmartTimerConfigTimeItem> = emptyList()
) {
    @Transient
    val triggerTimes: List<DateTime> by lazy {
        items.flatMap {
            it.triggerTimes
        }.sorted()
    }
}

object SmartTimerConfigTimeItemsSerializer : KSerializer<List<SmartTimerConfigTimeItem>> by ArrayListSerializer(
    SmartTimerConfigTimeItem.serializer()
)
