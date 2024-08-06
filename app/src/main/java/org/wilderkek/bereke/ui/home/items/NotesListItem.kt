package org.wilderkek.bereke.ui.home.items

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import org.wilderkek.bereke.model.response.NoteModel
import org.wilderkek.bereke.ui.holders.NoteItem
import org.wilderkek.bereke.ui.holders.GridListItem
import org.wilderkek.bereke.ui.holders.OnNoteActionsListener
import org.wilderkek.bereke.ui.holders.PlaceholderItem
import org.wilderkek.bereke.ui.views.ActionType
import org.wilderkek.bereke.util.findItemBy

class NotesListItem(
    val list: List<NoteModel?>,
    val listener: OnNoteActionsListener,
    val actionType: ActionType,
    private val itemId: Long = -100L
) : GridListItem<GroupieViewHolder>(itemId) {


    init {
        adapter = GroupAdapter<GroupieViewHolder>().apply {
            update(list.map { note ->
                if (note == null) PlaceholderItem(PlaceholderItem.Type.NOTE)
                else NoteItem(note, actionType, listener)
            })
            //addAll(items)
        }
    }

    fun updateItems(list: List<NoteModel?>) {
        getAdapter().apply {
            update(list.map { note ->
                if (note != null) NoteItem(note, actionType, listener)
                else null
            })
        }
    }

    fun updateItem(updated: NoteModel) {
        getAdapter().findItemBy { item: NoteItem -> item.id == updated.id.toLong() }
            ?.notifyChanged(updated)
    }
}