package com.github.insanusmokrassar.AutoPostSmartTimerTrigger

import com.github.insanusmokrassar.AutoPostSmartTimerTrigger.utils.*
import kotlinx.serialization.*
import kotlinx.serialization.internal.ArrayListSerializer
import org.joda.time.DateTime

@Serializable
class SmartTimerConfigTimeItem (
    val from: String = timeFormat.print(zeroHour),
    val to: String = timeFormat.print(nextDayZeroHour),
    val period: String = timeFormat.print(3600)
) {
    @Transient
    private val fromDateTime: DateTime by lazy {
        timeFormat.parseDateTime(from)
    }
    @Transient
    private val toDateTime: DateTime by lazy {
        timeFormat.parseDateTime(to).let {
            if (fromDateTime.isAfter(it) || fromDateTime.isEqual(it)) {
                it.plusDays(1)
            } else {
                it
            }
        }
    }

    @Transient
    private val periodMillis: Long by lazy {
        timeFormat.withZoneUTC().parseDateTime(period).millis
    }

    @Transient
    val triggerTimes: List<DateTime> by lazy {
        (fromDateTime.millis until toDateTime.millis step periodMillis).map {
            DateTime(it).withDayOfYear(1)
        }.sorted()
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("Times:\n")
        stringBuilder.append(
            triggerTimes.joinToString("\n")
        )
        return stringBuilder.toString()
    }
}

object SmartTimerConfigTimeItemsSerializer : KSerializer<List<SmartTimerConfigTimeItem>> by ArrayListSerializer(
    SmartTimerConfigTimeItem.serializer()
)
