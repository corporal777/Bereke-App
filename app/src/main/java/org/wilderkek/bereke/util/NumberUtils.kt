package org.wilderkek.bereke.util

import android.content.Context
import android.content.res.Resources
import org.wilderkek.bereke.R
import kotlin.math.ceil

val Float.dp: Float
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f)
val Float.sp: Float
    get() = (this * Resources.getSystem().displayMetrics.scaledDensity + 0.5f)
val Float.px: Float
    get() = (this / Resources.getSystem().displayMetrics.density)

val Int.dp: Int
    get() = this.toFloat().dp.toInt()
val Int.sp: Int
    get() = this.toFloat().sp.toInt()
val Int.px: Int
    get() = this.toFloat().px.toInt()

fun isPhoneIsValid(phone: String?): Boolean {
    var valid = true
    if (!phone.isNullOrEmpty()) {
        if (phone.contains("+")) {
            if (phone.length == 12) {
                if (phone.substring(0, 3) != "+79") valid = false
            } else valid = false
        } else {
            if (phone.length == 11) {
                val firstNumber = phone.substring(0, 2)
                if (firstNumber != "79" && firstNumber != "89") valid = false
            } else valid = false
        }
    } else {
        valid = false
    }
    return valid
}

fun isPhone(text: String?): Boolean {
    if (text.isNullOrEmpty()) return false
    val regex = Regex(pattern = "[0-9]+")
    return regex.containsMatchIn(text)
}

fun isContainLetters(text: String?): Boolean {
    if (text.isNullOrEmpty()) return false
    val regex = Regex(pattern = "[A-Za-z]+")
    return regex.containsMatchIn(text)
}

fun validatePhoneBeforeSend(phone: String): String {
    if (phone == "") return ""
    val phoneResult = if (!phone.contains("+")) "+$phone" else phone
    val sb = StringBuilder(phoneResult)
    if (phone.substring(0, 1) == "8")
        sb.setCharAt(1, '7')
    return sb.toString()
}

fun timerFormatter(time: Int, context: Context): String =
    if (time > 119) {
        val seconds = time - 60
        val minute = ceil((time - seconds).toDouble() / 60).toInt()
        context.resources.getQuantityString(R.plurals.minutes_timer, minute, minute)
    } else if (time > 59) {
        val minute = ceil(time.toDouble() / 60).toInt()
        context.resources.getQuantityString(R.plurals.minutes_timer, minute, minute)
    } else context.resources.getQuantityString(R.plurals.seconds_timer, time, time)
