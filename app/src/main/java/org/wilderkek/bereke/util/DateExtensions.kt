package org.wilderkek.bereke.util

import java.text.SimpleDateFormat
import java.util.*

val defaultTimeHourMinutesFormat
    get() = SimpleDateFormat("HH:mm", Locale.getDefault())


fun Long.calendar(): Calendar = Calendar.getInstance().apply { timeInMillis = this@calendar }

fun Date.calendar(): Calendar = Calendar.getInstance().apply { time = this@calendar }

fun Calendar.isSameDay(other: Calendar): Boolean {
    return isSameYear(other) && this.get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)
}

fun Calendar.isYesterday(from: Calendar): Boolean {
    return isSameYear(from) && this.get(Calendar.DAY_OF_YEAR) == from.get(Calendar.DAY_OF_YEAR) - 1
}

fun Calendar.isSameMonth(other: Calendar): Boolean {
    return isSameYear(other) && this.get(Calendar.MONTH) == other.get(Calendar.MONTH)
}

fun Calendar.isSameYear(other: Calendar): Boolean {
    return this.get(Calendar.YEAR) == other.get(Calendar.YEAR)
}

fun longToDate(date: Long): String =
    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date)

fun String.asTime(): String {
    Calendar.getInstance()
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return timeFormat.format(time)
}

fun Long.timeHoursAndMinutes(): String {
    val time = Date(this)
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return defaultTimeHourMinutesFormat.format(time)
}

fun String.formatToDayMonth(): String {
    var date = ""
    val time = Date(this.toLong())
    val noteDay = time.calendar()
    val today = System.currentTimeMillis().calendar()

    if (noteDay.isSameDay(today)) {
        date = "Сегодня" + ", в " + time.time.timeHoursAndMinutes()
    } else if (noteDay.isYesterday(today)) {
        date = "Вчера" + ", в " + time.time.timeHoursAndMinutes()
    } else if (noteDay.isSameYear(today)) {
        date = noteDay.get(Calendar.DAY_OF_MONTH).toString() + " " +
                noteDay.getDisplayName(Calendar.MONTH, Calendar.LONG_FORMAT, Locale.getDefault()) +
                ", в " + time.time.timeHoursAndMinutes()
    } else {
        date = noteDay.get(Calendar.DAY_OF_MONTH).toString() + "." +
                noteDay.get(Calendar.MONTH).toString() + "." +
                noteDay.get(Calendar.YEAR).toString()
    }

    return date
}

