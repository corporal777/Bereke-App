package org.wilderkek.bereke.ui.createNote.items

import android.graphics.Bitmap
import androidx.core.view.isVisible
import com.xwray.groupie.databinding.BindableItem
import org.wilderkek.bereke.util.loadImage
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.ItemImageGalleryBinding
import org.wilderkek.bereke.util.imagepicker.model.LocalImage


class GalleryImageItem(
    private val image: LocalImage?,
    private val onShowGalleryClick: () -> Unit,
    private val onDeleteImageClick: (bitmap : LocalImage) -> Unit,
) : BindableItem<ItemImageGalleryBinding>() {


    override fun bind(viewBinding: ItemImageGalleryBinding, position: Int) {
        viewBinding.apply {
            lnNoImage.isVisible = image == null
            ivImage.apply {
                isVisible = image != null
                loadImage(image?.bitmap)
            }
            ivClose.apply {
                isVisible = image != null
                setOnClickListener {
                    image?.let { b -> onDeleteImageClick.invoke(b) } }
            }
            clImage.setOnClickListener {
                onShowGalleryClick.invoke()
            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is GalleryImageItem) return false
        if (image != other.image) return false
        //if (image?.bitmap?.sameAs(other.image?.bitmap) != true) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_image_gallery
}