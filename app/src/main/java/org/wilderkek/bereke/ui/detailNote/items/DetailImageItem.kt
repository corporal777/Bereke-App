package org.wilderkek.bereke.ui.detailNote.items

import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.ItemNoteDetailImageBinding
import org.wilderkek.bereke.ui.detailNote.fullScreenImages.data.FullImagesData

class DetailImageItem(
    private val image: String?,
    private val onImageClick: () -> Unit
) : BindableItem<ItemNoteDetailImageBinding>() {


    override fun bind(viewBinding: ItemNoteDetailImageBinding, position: Int) {
        viewBinding.apply {
            if (image.isNullOrEmpty()) {
                noteDetailImage.isVisible = false
                lnEmptyImagePlaceholder.isVisible = true
            } else {
                Glide
                    .with(root.context)
                    .load(image)
                    .optionalCenterCrop()
                    .placeholder(R.drawable.background_image_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .into(noteDetailImage)

                clImage.setOnClickListener {
                    onImageClick.invoke()
                }
            }
        }
    }

    override fun hasSameContentAs(other: Item<*>?): Boolean {
        if (other !is DetailImageItem) return false
        if (image != other.image) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_note_detail_image
}