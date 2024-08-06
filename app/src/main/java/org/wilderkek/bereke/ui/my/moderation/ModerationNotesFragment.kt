package org.wilderkek.bereke.ui.my.moderation

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.FragmentMyNotesListBinding
import org.wilderkek.bereke.ui.base.BaseFragment
import org.wilderkek.bereke.ui.detailNote.NoteDetailFragmentArgs
import org.wilderkek.bereke.ui.holders.NoteEmptyItem
import org.wilderkek.bereke.ui.holders.OnNoteActionsListener
import org.wilderkek.bereke.ui.home.items.NotesListItem
import org.wilderkek.bereke.ui.home.items.TitleItemHome
import org.wilderkek.bereke.ui.my.BaseMyNotesFragment
import org.wilderkek.bereke.ui.my.MyNotesFragment
import org.wilderkek.bereke.ui.my.MyNotesViewModel
import org.wilderkek.bereke.ui.views.ActionType
import org.wilderkek.bereke.util.findItemBy
import org.wilderkek.bereke.util.onScrolled
import org.wilderkek.bereke.util.updateItem
import kotlin.reflect.KClass

class ModerationNotesFragment(
    onScrollState: MyNotesFragment.OnScrollStateChange,
    private val viewModel: MyNotesViewModel,
) : BaseMyNotesFragment(onScrollState) {


    private val notesSection = Section()
    override fun getGroupAdapter() = GroupAdapter<GroupieViewHolder>().apply {
        add(notesSection)
    }

    private val onNoteClickListener = object : OnNoteActionsListener {
        override fun onNoteClick(noteId: String) = showNoteDetail(noteId)
        override fun onNoteLikeClick(noteId: String) {}
        override fun onNoteDislikeClick(noteId: String) {}
        override fun onNoteEditClick(noteId: String) {}
    }

    override fun observeNotesList() {
        viewModel.moderationNotes.observe(viewLifecycleOwner) { list ->
            notesSection.apply {
                if (list.isNullOrEmpty()) {
                    removeHeader()
                    updateItem(NoteEmptyItem(getString(R.string.no_result_found)))
                } else {
                    setHeader(TitleItemHome(getString(R.string.moderation_notes_title)))
                    val item = findItemBy<NotesListItem> { true }
                    if (item == null) updateItem(
                        NotesListItem(
                            list,
                            onNoteClickListener,
                            ActionType.NONE
                        )
                    )
                    else item.updateItems(list)
                }
            }
        }
    }
}