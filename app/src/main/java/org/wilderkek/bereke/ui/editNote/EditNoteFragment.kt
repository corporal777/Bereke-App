package org.wilderkek.bereke.ui.editNote

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.FragmentCreateNoteBinding
import org.wilderkek.bereke.ui.base.BaseFragment
import org.wilderkek.bereke.ui.createNote.items.AddressDataItem
import org.wilderkek.bereke.ui.createNote.items.ImagesListItem
import org.wilderkek.bereke.ui.createNote.items.MainDataGroup
import org.wilderkek.bereke.ui.createNote.items.TitleDataItem
import org.wilderkek.bereke.ui.holders.MainButtonItem
import org.wilderkek.bereke.ui.interfaces.ToolbarFragment
import org.wilderkek.bereke.ui.views.dialogs.ActionsMessageDialog
import org.wilderkek.bereke.ui.views.dialogs.SuccessActionBottomSheetDialog
import org.wilderkek.bereke.util.*
import kotlin.reflect.KClass

class EditNoteFragment : BaseFragment<EditNoteViewModel, FragmentCreateNoteBinding>(),
    ToolbarFragment {


    private val mainDataSection by lazy {
        Section().apply {
            setHeader(TitleDataItem(getString(R.string.main_information)))
            setHideWhenEmpty(true)
        }
    }

    private val addressDataSection by lazy {
        Section().apply {
            setHeader(TitleDataItem(getString(R.string.address_title)))
            setHideWhenEmpty(true)
        }
    }

    private val imagesSection by lazy {
        Section().apply {
            setHeader(TitleDataItem(getString(R.string.photos_title)))
            setHideWhenEmpty(true)
        }
    }

    private val saveButtonSection by lazy { Section() }

    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(mainDataSection)
            add(addressDataSection)
            add(imagesSection)
            add(saveButtonSection)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navArgs<EditNoteFragmentArgs>().apply {
            viewModel.noteId = this.value.noteId
            viewModel.loadUserNote(this.value.noteId)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressedCallback(true) {
            showGoToBackDialog()
        }
        mBinding.apply {
            createNoteList.adapter = groupAdapter
        }
        observeNote()
        observeNoteUpdated()
    }

    private fun observeNote() {
        viewModel.userNote.observe(viewLifecycleOwner) { draftModel ->
            mainDataSection.updateGroup(MainDataGroup(requireContext(), draftModel))
            addressDataSection.updateItem(
                AddressDataItem(
                    requireActivity(),
                    draftModel?.address?.city,
                    draftModel?.address?.region,
                    draftModel?.address?.metro
                )
            )
            imagesSection.apply {
                viewModel.selectedImages.observe(viewLifecycleOwner) { list ->
                    val item = findItemBy<ImagesListItem> { true }
                    if (item == null) {
                        updateItem(
                            ImagesListItem(list,
                                { viewModel.showGallery() },
                                { viewModel.removeSelectedImage(it) }
                            )
                        )
                    } else item.update(list)
                }
            }


            saveButtonSection.updateItem(MainButtonItem(true, getString(R.string.publicate)) {
                updateNote()
            })
        }
    }

    private fun observeNoteUpdated() {
        viewModel.noteUpdated.observe(viewLifecycleOwner) {
            if (it) {
                SuccessActionBottomSheetDialog(
                    requireContext(),
                    "Объявление изменено",
                    "Ваше объявление изменено, пользователи увидят изменения в ближайшее время.",
                    "Принять"
                ).setActionCallback {
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun updateNote() {
        val mainDataItem = mainDataSection.findGroupBy<MainDataGroup> { true }
        val addressDataItem = addressDataSection.findItemBy<AddressDataItem> { true }

        if (mainDataItem != null) {
            val noteRequest = mainDataItem.getNoteRequest(addressDataItem)
            if (noteRequest != null) viewModel.updateUserNote(noteRequest)
        }
    }

    private fun showGoToBackDialog() {
        ActionsMessageDialog(
            requireContext(),
            "Изменения в объявлении",
            "Вы уверены, что хотите выйти? Внесенные изменения не сохранятся.",
            "Выйти"
        ).setAcceptCallback { findNavController().navigateUp() }
    }

    override val layoutId: Int = R.layout.fragment_create_note
    override fun getViewModelClass(): KClass<EditNoteViewModel> = EditNoteViewModel::class
    override val title: CharSequence by lazy { getString(R.string.edit_note) }
    override fun toolbarIconsContainer(viewGroup: ViewGroup) {}
    override fun dispatchBack(): (() -> Unit) = { showGoToBackDialog() }
}