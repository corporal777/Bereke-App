package org.wilderkek.bereke.util.imagepicker.utils

import android.content.Context

internal object StringUtil {

    fun getStringRes(context: Context, resId: Int): String {
        return context.resources.getString(resId)
    }

    fun getStringRes(context: Context, resId: Int, count: Int): String {
        return String.format(context.resources.getString(resId), count)
    }

    fun getStringRes(context: Context, resId: Int, count: Int, max: Int): String {
        return String.format(context.resources.getString(resId), count, max)
    }
}