package org.wilderkek.bereke.ui.holders

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import org.wilderkek.bereke.model.response.NoteModel
import org.wilderkek.bereke.model.response.StoriesModel

class StoriesListItem(
    val list: List<StoriesModel>,
    private val itemId: Long = -101L,
    private val onStoryClick: (story: StoriesModel) -> Unit,
) : HorizontalListItem<GroupieViewHolder>(itemId) {

    init {
        adapter = GroupAdapter<GroupieViewHolder>().apply {
            update(list.map { story -> StoryItem(story) { onStoryClick(story) } })
        }
    }

    fun updateItems(list: List<StoriesModel>) {
        getAdapter().apply {
            update(list.map { story -> StoryItem(story) { onStoryClick(story) } })
        }
    }


}