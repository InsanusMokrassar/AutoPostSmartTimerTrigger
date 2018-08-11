package com.github.insanusmokrassar.AutoPostSmartTimerTrigger

import com.github.insanusmokrassar.IObjectKRealisations.load
import com.github.insanusmokrassar.IObjectKRealisations.toObject

fun main(args: Array<String>) {
    load(args[0]).also {
        println(it)
    }
        .toObject(SmartTimerConfig::class.java).also {
            println(
                it.triggerTimes.joinToString("\n")
            )
        }
}
