package org.wilderkek.bereke.ui.detailNote.fullScreenImages.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.wilderkek.bereke.model.response.StoryModel

@Parcelize
data class FullImagesData(
    val position: Int,
    var images: List<String>
) : Parcelable {

    fun setFirstImage(){
        if (images.size > 1){
            val newList = arrayListOf<String>().apply {
                addAll(images)
            }
            val image = newList[position]
            newList.removeAt(position)
            newList.add(0, image)
            this.images = newList
        }
    }
}