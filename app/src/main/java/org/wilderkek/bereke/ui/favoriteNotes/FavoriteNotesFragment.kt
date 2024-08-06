package org.wilderkek.bereke.ui.favoriteNotes

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.CompoundButton
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import org.wilderkek.bereke.ui.holders.NoteEmptyItem
import org.wilderkek.bereke.ui.holders.OnNoteActionsListener
import org.wilderkek.bereke.ui.home.items.NotesListItem
import org.wilderkek.bereke.ui.home.items.TitleItemHome
import org.wilderkek.bereke.ui.interfaces.ToolbarFragment
import org.wilderkek.bereke.ui.views.FilterButtonView
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.FragmentFavoriteNotesBinding
import org.wilderkek.bereke.ui.base.BaseFragment
import org.wilderkek.bereke.ui.detailNote.NoteDetailFragmentArgs
import org.wilderkek.bereke.ui.views.ActionLikeView
import org.wilderkek.bereke.ui.views.ActionType
import org.wilderkek.bereke.ui.views.ToolbarIconView
import org.wilderkek.bereke.ui.views.dialogs.ActionsBottomSheetDialog
import org.wilderkek.bereke.util.*
import kotlin.reflect.KClass


class FavoriteNotesFragment : BaseFragment<FavoriteNotesViewModel, FragmentFavoriteNotesBinding>(),
    ToolbarFragment {

    private var filtersAdded = false

    private val notesSection by lazy { Section() }
    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(notesSection)
        }
    }

    private val onNoteClickListener = object : OnNoteActionsListener {
        override fun onNoteClick(noteId: String) = showNoteDetail(noteId)
        override fun onNoteLikeClick(noteId: String) = viewModel.addNoteToFavorite(noteId)
        override fun onNoteDislikeClick(noteId: String) = viewModel.removeNoteFromFavorite(noteId)
        override fun onNoteEditClick(noteId: String) {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFilters()
        mBinding.apply {
            listNotes.apply {
                adapter = groupAdapter
                onScrolled { dx, dy ->
                    if (dy > 20) hideToolbarFilters()
                    else if (dy < -5) showToolbarFilters()
                }
            }
            observeNotesList()
            observeEmptyListPlaceholder()
        }
    }

    private fun observeNotesList() {
        viewModel.notesList.observe(viewLifecycleOwner) { list ->
            notesSection.apply {
                setHeader(TitleItemHome(getString(R.string.favorite_notes_tite)))
                val item = findItemBy<NotesListItem> { true }
                if (item == null) updateItem(
                    NotesListItem(
                        list,
                        onNoteClickListener,
                        ActionType.LIKE
                    )
                )
                else item.updateItems(list)
            }
        }
    }


    private fun observeEmptyListPlaceholder() {
        viewModel.placeholder.observe(viewLifecycleOwner) {
            notesSection.apply {
                removeHeader()
                updateItem(NoteEmptyItem(it))
            }
        }
    }

    private fun showNoteDetail(noteId: String) {
        findNavController().navigate(
            R.id.note_detail_fragment,
            NoteDetailFragmentArgs.Builder(noteId).build().toBundle()
        )
    }

    private fun showActionsDialog() {
        ActionsBottomSheetDialog(
            requireContext(),
            getString(R.string.remove_all_active),
            getString(R.string.remove_all_inactive)
        )
            .setActionFirstCallback { viewModel.removeAllFavoriteNotes(APPROVED) }
            .setActionSecondCallback { viewModel.removeAllFavoriteNotes(INACTIVE) }
    }

    private fun initFilters() {
        if (!filtersAdded) {
            val createChip: (FavoriteFilter) -> CompoundButton = { filter ->
                FilterButtonView(requireContext()).apply {
                    id = filter.id
                    text = filter.name
                    isChecked = filter.isChecked
                    isClickable = viewModel.isUserAuthorized()
                    setOnCheckedChangeListener { _, isChecked ->
                        filter.isChecked = isChecked
                        viewModel.onFilterChanged(filter)
                    }
                }
            }
            mBinding.include.lnFiltersContainer.apply {
                removeAllViews()
                viewModel.getFavoriteFilters().forEach { s -> addView(createChip(s)) }
                filtersAdded = true
            }
        }
    }

    private fun hideToolbarFilters() {
        mBinding.favoriteAppBar.animate()
            .translationY((-mBinding.include.filtersToolbar.bottom).toFloat())
            .setInterpolator(AccelerateInterpolator()).start()
    }

    private fun showToolbarFilters() {
        mBinding.favoriteAppBar.animate().translationY(0f)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }


    override val layoutId = R.layout.fragment_favorite_notes
    override fun getViewModelClass(): KClass<FavoriteNotesViewModel> = FavoriteNotesViewModel::class

    override val title: CharSequence by lazy { getString(R.string.favorite_notes) }
    override fun toolbarIconsContainer(viewGroup: ViewGroup) {
        viewGroup.apply {
            addView(ToolbarIconView(context).apply {
                viewModel.hasFavoriteNotes.observe(viewLifecycleOwner){
                    isVisible = it
                }
                setImageAsIcon(R.drawable.ic_profile_menu)
                setOnClickListener { showActionsDialog() }
            })
        }
    }
    override fun dispatchBack(): (() -> Unit)? = null
}