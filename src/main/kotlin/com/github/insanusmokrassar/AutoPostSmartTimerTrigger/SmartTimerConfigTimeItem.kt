package com.github.insanusmokrassar.AutoPostSmartTimerTrigger

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

private val timeFormat: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm")

private val zeroHour: DateTime by lazy {
    timeFormat.parseDateTime("00:00")
}

private val nextDayZeroHour: DateTime by lazy {
    zeroHour.plusDays(1)
}

class SmartTimerConfigTimeItem (
    val from: String = timeFormat.print(zeroHour),
    val to: String = timeFormat.print(nextDayZeroHour),
    val period: String = timeFormat.print(3600)
) {
    private val fromDateTime: DateTime by lazy {
        timeFormat.parseDateTime(from)
    }
    private val toDateTime: DateTime by lazy {
        timeFormat.parseDateTime(to)
    }
    private val periodMillis: Long by lazy {
        timeFormat.withZoneUTC().parseDateTime(period).millis
    }

    private val timePairs: List<Pair<DateTime, DateTime>> by lazy {
        if (from > to) {
            listOf(
                zeroHour to toDateTime,
                fromDateTime to nextDayZeroHour
            )
        } else {
            listOf(fromDateTime to toDateTime)
        }
    }

    val triggerTimes: List<DateTime> by lazy {
        timePairs.flatMap {
            it.first.millis until it.second.millis step periodMillis
        }.map {
            DateTime(it)
        }.sorted()
    }

    val nextTriggerTime: DateTime
        get() {
            val now = DateTime.now()
            return triggerTimes.firstOrNull {
                it.isAfter(now)
            } ?: triggerTimes.first()
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
