package org.wilderkek.bereke.util.imagepicker.ui

import android.view.View
import org.wilderkek.bereke.util.imagepicker.model.Image

internal interface GalleryListener {

    // set Checked status with start Animation
    fun onChecked(image: Image)

    // Move to Detail View
    fun onShowDetail(view: View, image: Image)

    // single selected
    fun onClick(image: Image)

    // image is already checked
    var isMultipleChecked: Boolean
}