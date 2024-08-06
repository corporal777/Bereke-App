package org.wilderkek.bereke.ui.createNote.items

import android.graphics.Bitmap
import android.util.Log
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import org.wilderkek.bereke.ui.holders.GridListItem
import org.wilderkek.bereke.util.imagepicker.model.LocalImage
import org.wilderkek.bereke.util.updateItem

class ImagesListItem(
    val list: List<LocalImage>?,
    private val onShowGalleryClick: () -> Unit,
    private val onRemoveImageClick: (bitmap: LocalImage) -> Unit
) : GridListItem<GroupieViewHolder>(-500L) {

    private val addImageItem = GalleryImageItem(null,
        { onShowGalleryClick.invoke() }, { }
    )
    val items = arrayListOf<Item<*>>()

    private val imagesSection = Section()

    init {
        items.add(addImageItem)
        if (!list.isNullOrEmpty()) {
            list.forEach { image ->
                items.add(0, GalleryImageItem(image, {}, {
                    onRemoveImageClick.invoke(it)
                }))
            }
        }
        imagesSection.update(items)

        adapter = GroupAdapter<GroupieViewHolder>().apply {
            add(imagesSection)
        }
    }

    fun update(list: List<LocalImage>?) {
        if (!list.isNullOrEmpty()) {
            imagesSection.update(
                list.map {
                    GalleryImageItem(it, {}, { b ->
                        onRemoveImageClick.invoke(b)
                    })
                }.plus(listOf(addImageItem))
            )
        } else imagesSection.updateItem(addImageItem)
    }

}