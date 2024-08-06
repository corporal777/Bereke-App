package org.wilderkek.bereke.ui.holders

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import org.wilderkek.bereke.util.formatToDayMonth
import org.wilderkek.bereke.util.loadNoteImage
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.ItemNoteBinding
import org.wilderkek.bereke.model.response.NoteModel
import org.wilderkek.bereke.ui.views.ActionType

class NoteItem(
    private val note: NoteModel,
    private val actionType: ActionType,
    private val listener: OnNoteActionsListener
) : BindableItem<ItemNoteBinding>(note.id.toLong()) {

    private var noteId = note.id
    private var noteModel = note

    override fun bind(viewBinding: ItemNoteBinding, position: Int) {
        viewBinding.apply {
            decorViews(this)
        }
    }

    private fun decorViews(viewBinding: ItemNoteBinding) {
        viewBinding.apply {
            ivNoteImage.loadNoteImage(noteModel.images?.get(0))
            tvCategory.text = noteModel.getCategory()
            tvSalary.text = noteModel.getNoteSalary()

            tvDescription.text = noteModel.getNoteDescription()
            tvLocation.text = noteModel.getLocation()
            tvDate.text = noteModel.createdAt.formatToDayMonth()

            likeView.apply {
                when (actionType) {
                    ActionType.NONE -> setActionNone()
                    ActionType.LIKE -> {
                        setActionLike(noteModel.isNoteLiked, noteModel.isOwner)
                        onDisLikeClickListener = { listener.onNoteDislikeClick(noteId) }
                        onLikeClickListener = { listener.onNoteLikeClick(noteId) }
                    }
                    else -> {
                        setActionEdit()
                        onEditClickListener = { listener.onNoteEditClick(noteId) }
                    }
                }
            }

            setNoteStatus(viewBinding)
            cardNote.setOnClickListener {
                listener.onNoteClick(noteId)
            }
        }
    }

    private fun setNoteStatus(viewBinding: ItemNoteBinding) {
        viewBinding.apply {
            when (note.status) {
                NoteModel.Status.INACTIVE -> {
                    viewStatusMask.isVisible = true
                    tvStatus.apply {
                        isVisible = true
                        background = ContextCompat.getDrawable(context, R.drawable.background_not_active_status)
                        text = context.getString(R.string.note_status_not_active)
                    }
                }
                NoteModel.Status.PENDING -> {
                    viewStatusMask.isVisible = true
                    tvStatus.apply {
                        isVisible = true
                        background = ContextCompat.getDrawable(context, R.drawable.background_moderation_status)
                        text = context.getString(R.string.note_status_on_moderation)
                    }
                }
                else -> {
                    viewStatusMask.apply { isVisible = false }
                    tvStatus.apply { isVisible = false }
                }
            }
        }
    }

    override fun bind(viewBinding: ItemNoteBinding, position: Int, payloads: MutableList<Any>?) {
        val payload = payloads?.firstOrNull()
        if (payload == null) super.bind(viewBinding, position, payloads)
        else {
            if (payload is NoteModel) {
                noteId = payload.id
                noteModel = payload
                decorViews(viewBinding)
            }
        }
    }


    override fun hasSameContentAs(other: Item<*>?): Boolean {
        if (other !is NoteItem) return false
        if (note != other.note) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_note
}

interface OnNoteActionsListener {
    fun onNoteClick(noteId: String)
    fun onNoteLikeClick(noteId: String)
    fun onNoteDislikeClick(noteId: String)
    fun onNoteEditClick(noteId: String)
}