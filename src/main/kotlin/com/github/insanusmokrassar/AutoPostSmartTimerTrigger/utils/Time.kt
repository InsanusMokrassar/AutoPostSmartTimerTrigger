package com.github.insanusmokrassar.AutoPostSmartTimerTrigger.utils

import org.joda.time.DateTime
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
