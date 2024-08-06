package org.wilderkek.bereke.util.imagepicker.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.wilderkek.bereke.util.RESULT_NAME

@Parcelize
data class SetUp(
    var max: Int = 30,
    var name: String = RESULT_NAME,
    var title: String? = null,
    var single: Boolean = false
) : Parcelable {

    fun max(action: () -> Int) {
        max = action()
    }

    fun name(action: () -> String) {
        name = action()
    }

    fun title(action: () -> String?) {
        title = action()
    }

    fun single(action: () -> Boolean) {
        single = action()
    }

    fun build(): SetUp = this
}