package org.wilderkek.bereke.ui.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.ViewDataBindingKtx
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.FragmentMyNotesListBinding
import org.wilderkek.bereke.ui.detailNote.NoteDetailFragmentArgs
import org.wilderkek.bereke.ui.editNote.EditNoteFragment
import org.wilderkek.bereke.ui.editNote.EditNoteFragmentArgs
import org.wilderkek.bereke.ui.views.dialogs.ActionsMessageDialog
import org.wilderkek.bereke.util.onScrolled

abstract class BaseMyNotesFragment(
    private val onScrollState: MyNotesFragment.OnScrollStateChange
) : Fragment() {

    lateinit var mBinding: FragmentMyNotesListBinding

    abstract fun getGroupAdapter(): GroupAdapter<GroupieViewHolder>
    abstract fun observeNotesList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (::mBinding.isInitialized.not()) {
            mBinding = DataBindingUtil.inflate(
                layoutInflater,
                R.layout.fragment_my_notes_list,
                container,
                false
            )
            mBinding.lifecycleOwner = viewLifecycleOwner
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.myNotesList.apply {
            adapter = getGroupAdapter()
            onScrolled { _, dy ->
                onScrollState.onScrollOffset(this.computeVerticalScrollOffset().toFloat())
                if (dy > 20) onScrollState.onScrollDown(dy)
                else if (dy < -5) onScrollState.onScrollUp(dy)
            }
        }
        observeNotesList()
    }


    fun showNoteDetail(noteId: String) {
        findNavController().navigate(
            R.id.note_detail_fragment,
            NoteDetailFragmentArgs.Builder(noteId).build().toBundle()
        )
    }

    fun showEditNote(noteId: String) {
        findNavController().navigate(
            R.id.edit_note_fragment,
            EditNoteFragmentArgs.Builder(noteId).build().toBundle()
        )
    }

    fun showDeleteNoteDialog(onAcceptCallback: () -> Unit) {
        ActionsMessageDialog(
            requireContext(),
            "Удалить",
            "Вы действительно хотите удалить объявление? Отменить это действие будет невозможно.",
            "Удалить"
        ).setAcceptCallback {
            onAcceptCallback.invoke()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding.unbind()
    }
}