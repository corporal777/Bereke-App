package org.wilderkek.bereke.ui.home

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import org.wilderkek.bereke.ui.interfaces.ToolbarFragment
import org.wilderkek.bereke.util.findItemBy
import org.wilderkek.bereke.util.updateItem
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.FragmentHomeBinding
import org.wilderkek.bereke.model.response.StoriesModel
import org.wilderkek.bereke.ui.base.BaseFragment
import org.wilderkek.bereke.ui.detailNote.NoteDetailFragmentArgs
import org.wilderkek.bereke.ui.holders.OnNoteActionsListener
import org.wilderkek.bereke.ui.holders.StoriesListItem
import org.wilderkek.bereke.ui.home.items.CategoriesItem
import org.wilderkek.bereke.ui.home.items.NotesListItem
import org.wilderkek.bereke.ui.home.items.TitleItemHome
import org.wilderkek.bereke.ui.stories.StoriesFragmentArgs
import org.wilderkek.bereke.ui.views.ActionLikeView
import org.wilderkek.bereke.ui.views.ActionType
import kotlin.reflect.KClass

class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>(), ToolbarFragment {


    private val storiesSection by lazy {
        Section().apply {
            setHeader(TitleItemHome(getString(R.string.special_offer)))
            setHideWhenEmpty(true)
        }
    }

    private val categoriesSection by lazy {
        Section().apply {
            setHeader(TitleItemHome(""))
            add(CategoriesItem())
        }
    }

    private val notesSection by lazy {
        Section().apply {
            setHeader(TitleItemHome(getString(R.string.actual_notes)))
            setHideWhenEmpty(true)
        }
    }
    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(storiesSection)
            add(categoriesSection)
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
        mBinding.apply {
            homeList.adapter = groupAdapter
        }
        observeNotesList()
        observeStoriesList()
        observeUpdatedNote()
        viewModel.getStoriesList()
        viewModel.getNotesList()
    }


    private fun observeNotesList() {
        viewModel.notesList.observe(viewLifecycleOwner) { list ->
            val item = notesSection.findItemBy<NotesListItem> { true }
            if (item == null) notesSection.updateItem(
                NotesListItem(
                    list,
                    onNoteClickListener,
                    ActionType.LIKE
                )
            )
            else item.updateItems(list)
        }
    }

    private fun observeStoriesList() {
        viewModel.storiesList.observe(viewLifecycleOwner) {
            val item = storiesSection.findItemBy<StoriesListItem> { true }
            if (item == null) storiesSection.updateItem(StoriesListItem(it) { s -> showStories(s) })
            else item.updateItems(it)
        }
    }

    private fun observeUpdatedNote() {
        viewModel.updatedNote.observe(viewLifecycleOwner) {
            notesSection.findItemBy<NotesListItem> { true }?.updateItem(it)
        }
    }

    private fun showStories(stories: StoriesModel) {
        findNavController().navigate(
            R.id.stories_fragment,
            StoriesFragmentArgs.Builder(stories).build().toBundle()
        )
    }

    private fun showNoteDetail(noteId: String) {
        findNavController().navigate(
            R.id.note_detail_fragment,
            NoteDetailFragmentArgs.Builder(noteId).build().toBundle()
        )
    }


    override val layoutId: Int = R.layout.fragment_home
    override fun getViewModelClass(): KClass<HomeViewModel> = HomeViewModel::class
    override val title: CharSequence by lazy { getString(R.string.home) }
    override fun toolbarIconsContainer(viewGroup: ViewGroup) {}
    override fun dispatchBack(): (() -> Unit)? = null
}