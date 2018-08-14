package com.github.insanusmokrassar.AutoPostSmartTimerTrigger

import com.github.insanusmokrassar.AutoPostSmartTimerTrigger.utils.*
import org.joda.time.DateTime

class SmartTimerConfigTimeItem (
    val from: String = timeFormat.print(zeroHour),
    val to: String = timeFormat.print(nextDayZeroHour),
    val period: String = timeFormat.print(3600)
) {
    private val fromDateTime: DateTime by lazy {
        timeFormat.parseDateTime(from)
    }
    private val toDateTime: DateTime by lazy {
        timeFormat.parseDateTime(to).let {
            if (fromDateTime.isAfter(it)) {
                it.plusDays(1)
            } else {
                it
            }
        }
    }

    private val periodMillis: Long by lazy {
        timeFormat.withZoneUTC().parseDateTime(period).millis
    }

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
