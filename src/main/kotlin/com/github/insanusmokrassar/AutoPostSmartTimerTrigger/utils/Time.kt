package com.github.insanusmokrassar.AutoPostSmartTimerTrigger.utils

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

val timeFormat: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm")

val zeroHour: DateTime by lazy {
    timeFormat.parseDateTime("00:00")
}

val nextDayZeroHour: DateTime by lazy {
    zeroHour.plusDays(1)
}

fun nowTime(): DateTime {
    return timeFormat.parseDateTime(
        timeFormat.print(DateTime.now())
    )
}

fun List<DateTime>.near(): DateTime? {
    val now = nowTime()

    return filter {
        it.isAfter(now)
    }
        .sorted()
        .firstOrNull()
        ?: sorted().firstOrNull() ?.plusDays(1)
}
