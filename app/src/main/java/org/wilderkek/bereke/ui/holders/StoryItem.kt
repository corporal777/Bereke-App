package org.wilderkek.bereke.ui.holders

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.ItemStoryBinding
import org.wilderkek.bereke.model.response.StoriesModel

class StoryItem(
    private val story: StoriesModel,
    private val onStoryClick: (story: StoriesModel) -> Unit
) : BindableItem<ItemStoryBinding>() {


    override fun bind(viewBinding: ItemStoryBinding, position: Int) {
        viewBinding.apply {
            ivImage.apply {
                Glide
                    .with(context)
                    .load(story.logo)
                    .optionalCenterCrop()
                    .placeholder(R.drawable.background_image_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .into(this)
            }
            tvTitle.text = story.title
            clStory.setOnClickListener {
                onStoryClick.invoke(story)
            }
        }
    }

    override fun hasSameContentAs(other: Item<*>?): Boolean {
        if (other !is StoryItem) return false
        if (story != other.story) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_story
}