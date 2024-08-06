package org.wilderkek.bereke.ui.detailNote.items

import androidx.core.view.isVisible
import com.google.android.material.tabs.TabLayoutMediator
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import com.xwray.groupie.databinding.BindableItem
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.ItemNoteDetailImagesBinding
import org.wilderkek.bereke.ui.detailNote.fullScreenImages.data.FullImagesData
import org.wilderkek.bereke.util.updateItem

class DetailImagesItem(
    private val listImages: List<String>?,
    private val onImageClick: (images: FullImagesData) -> Unit
) : BindableItem<ItemNoteDetailImagesBinding>() {

    private val imagesSection = Section()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>().apply {
        add(imagesSection)
    }


    init {
        imagesSection.apply {
            if (!listImages.isNullOrEmpty()) {
                update(listImages.map {
                    DetailImageItem(it) {
                        onImageClick.invoke(FullImagesData(mBinding.viewPagerImages.currentItem, listImages))
                    }
                })
            } else updateItem(DetailImageItem(null) {})
        }
    }

    private lateinit var mBinding: ItemNoteDetailImagesBinding
    override fun bind(viewBinding: ItemNoteDetailImagesBinding, position: Int) {
        mBinding = viewBinding
        viewBinding.apply {
            viewPagerImages.apply {
                adapter = groupAdapter
                setOnClickListener {
                    if (!listImages.isNullOrEmpty()) {
                        onImageClick.invoke(FullImagesData(currentItem, listImages))
                    }
                }
            }
            tabDots.apply {
                isVisible = (listImages?.size ?: 0) > 1
                TabLayoutMediator(this, viewPagerImages) { tab, position ->
                }.attach()
            }
        }
    }

    override fun hasSameContentAs(other: Item<*>?): Boolean {
        if (other !is DetailImagesItem) return false
        if (listImages != other.listImages) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_note_detail_images
}