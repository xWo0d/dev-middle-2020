package ru.skillbranch.skillarticles.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.add(duration: Int, timeUnit: TimeUnits): Date {
    time += timeUnit.tMillis(duration)
    return this
}

enum class TimeUnits {
    MILLISECOND,
    SECOND,
    MINUTE,
    HOUR,
    DAY;

    fun tMillis(duration: Int): Long = when (this) {
        MILLISECOND -> duration * 1L
        SECOND -> duration * 1000L
        MINUTE -> duration * 1000 * 60L
        HOUR -> duration * 1000 * 60 * 60L
        DAY -> duration * 1000 * 60 * 60 * 24L
    }
}

