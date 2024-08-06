package org.wilderkek.bereke.util.imagepicker.model

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Image(
    val id: Int,
    val path: Uri,
    var selected: Boolean
) : Parcelable {
}

@Parcelize
data class LocalImage(
    val id: Int,
    val path: Uri,
    val bitmap : Bitmap,
    var selected: Boolean
) : Parcelable
