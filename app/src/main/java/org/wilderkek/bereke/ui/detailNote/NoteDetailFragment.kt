package org.wilderkek.bereke.ui.detailNote

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import org.wilderkek.bereke.ui.views.LinearLayoutManagerAccurateOffset
import org.wilderkek.bereke.util.onScrolled
import org.wilderkek.bereke.util.updateItem
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.FragmentNoteDetailBinding
import org.wilderkek.bereke.ui.base.BaseFragment
import org.wilderkek.bereke.ui.detailNote.data.NoteDetailData
import org.wilderkek.bereke.ui.detailNote.fullScreenImages.FullScreenImagesFragment
import org.wilderkek.bereke.ui.detailNote.fullScreenImages.FullScreenImagesFragmentArgs
import org.wilderkek.bereke.ui.detailNote.fullScreenImages.data.FullImagesData
import org.wilderkek.bereke.ui.detailNote.items.*
import org.wilderkek.bereke.ui.holders.OnNoteActionsListener
import org.wilderkek.bereke.ui.home.items.NotesListItem
import org.wilderkek.bereke.ui.home.items.TitleItemHome
import org.wilderkek.bereke.ui.views.ActionLikeView
import org.wilderkek.bereke.ui.views.ActionType
import org.wilderkek.bereke.ui.views.dialogs.ActionsBottomSheetDialog
import org.wilderkek.bereke.ui.views.dialogs.ComplaintBottomSheetDialog
import org.wilderkek.bereke.ui.views.dialogs.SuccessActionBottomSheetDialog
import org.wilderkek.bereke.util.findItemBy
import kotlin.math.abs
import kotlin.reflect.KClass


class NoteDetailFragment : BaseFragment<NoteDetailViewModel, FragmentNoteDetailBinding>() {

    private val args: NoteDetailFragmentArgs by navArgs()

    private val imageSection = Section()
    private val mainInfoSection = Section()
    private val additionalInfoSection = Section()
    private val addressSection = Section()
    private val contactsSection = Section()
    private val sameNotesSection = Section()

    private val groupAdapter = GroupAdapter<GroupieViewHolder>().apply {
        add(imageSection)
        add(mainInfoSection)
        add(additionalInfoSection)
        add(addressSection)
        add(contactsSection)
        add(sameNotesSection)
    }

    private val onNoteClickListener = object : OnNoteActionsListener {
        override fun onNoteClick(noteId: String) = showNoteDetail(noteId)
        override fun onNoteLikeClick(noteId: String) = viewModel.addNoteToFavorite(noteId)
        override fun onNoteDislikeClick(noteId: String) = viewModel.removeNoteFromFavorite(noteId)
        override fun onNoteEditClick(noteId: String) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.noteId = args.noteId
        viewModel.loadNoteDetail(args.noteId)
    }


    private val mLayoutManager by lazy {
        LinearLayoutManagerAccurateOffset(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            noteDetailList.apply {
                layoutManager = mLayoutManager
                adapter = groupAdapter
                onScrolled { dx, dy ->
                    viewModel.changeScrollOffset(this.computeVerticalScrollOffset())
                }
            }
            ivMenu.setOnClickListener { showActionsDialog() }
            ivBack.setOnClickListener { findNavController().navigateUp() }
        }
        observeNoteDetail()
        observeNoteLiked()
        observeUpdatedNote()
        updateAppBarBackgroundColorValue()
    }

    private fun observeNoteLiked() {
        viewModel.noteLiked.observe(viewLifecycleOwner) { isLiked ->
            if (isLiked)
                mBinding.ivLike.apply {
                    setImageResource(R.drawable.ic_like_fill)
                    setOnClickListener {
                        viewModel.removeNoteFromFavorite(viewModel.noteId)
                    }
                }
            else mBinding.ivLike.apply {
                setImageResource(R.drawable.ic_like_empty)
                setOnClickListener {
                    viewModel.addNoteToFavorite(viewModel.noteId)
                }
            }
        }
    }

    private fun observeNoteDetail() {
        viewModel.noteDetail.observe(viewLifecycleOwner) { data ->
            mBinding.ivLike.isVisible = !data.note.isOwner
            mBinding.likeMask.isVisible = !data.note.isOwner

            imageSection.updateItem(
                DetailImagesItem(data.note.images) { showFullScreenImage(it) }
            )
            mainInfoSection.updateItem(
                DetailMainInfoItem(
                    data.note.name,
                    data.note.getNoteSalary(),
                    data.note.description,
                    data.note.likes,
                    "0"
                )
            )
            if (data.noteData != null) {
                additionalInfoSection.updateItem(
                    when (data) {
                        is NoteDetailData.WorkData -> DetailWorkInfoItem(data.note, data.getData())
                        is NoteDetailData.HouseData -> DetailHouseInfoItem(
                            data.note,
                            data.getData()
                        )
                    }
                )
            }
            addressSection.updateItem(
                DetailAddressItem(
                    data.note.address.city,
                    data.note.address.region,
                    data.note.address.metro,
                    data.note.createdAt
                )
            )
            contactsSection.updateItem(
                DetailContactsItem(
                    data.note.contacts.phone,
                    data.note.contacts.whatsapp,
                    data.author?.nameLastName,
                    data.author?.publicId,
                    data.author?.image
                )
            )
            viewModel.sameNotesList.observe(viewLifecycleOwner) {
                sameNotesSection.apply {
                    setHeader(TitleItemHome(getString(R.string.same_notes_title)))
                    updateItem(
                        NotesListItem(
                            it,
                            onNoteClickListener,
                            ActionType.LIKE
                        )
                    )
                }
            }
        }
    }

    private fun observeUpdatedNote() {
        viewModel.updatedSameNote.observe(viewLifecycleOwner) {
            sameNotesSection.findItemBy<NotesListItem> { true }?.updateItem(it)
        }
    }


    private fun showActionsDialog(){
        ActionsBottomSheetDialog(
            requireContext(),
            "Поделиться",
            "Отправить жалобу",
        )
            .setActionFirstCallback {  }
            .setActionSecondCallback { showSendComplaintDialog() }
    }

    private fun showSendComplaintDialog(){
        ComplaintBottomSheetDialog(requireContext())
            .setActionSendCallback { viewModel.sendNoteComplaint(it) }
    }

    private fun showFullScreenImage(images: FullImagesData) {
        findNavController().navigate(
            R.id.full_screen_images_fragment,
            FullScreenImagesFragmentArgs.Builder(images).build().toBundle()
        )
    }

    private fun showNoteDetail(noteId: String) {
        findNavController().navigate(
            R.id.note_detail_fragment,
            NoteDetailFragmentArgs.Builder(noteId).build().toBundle()
        )
    }

    private fun updateAppBarBackgroundColorValue() {
        viewModel.scrollOffsetData.observe(viewLifecycleOwner) { offset ->
            //Log.e("OFFSET", offset.toString())
            //Log.e("X VALUE MASK", mBinding.menuMask.x.toString())
            //Log.e("X VALUE LIKE", mBinding.ivMenu.x.toString())
            mBinding.apply {
                if (offset <= 0) {
                    tbBackground.alpha = 0f
                    backMask.alpha = 1f
                    likeMask.alpha = 1f
                    //ivBack.x = 42f
                    //backMask.x = 42f
                } else {
                    tbBackground.apply {
                        alpha = abs(offset / (700).toFloat())
                        backMask.alpha = 1f - abs(offset / (700).toFloat())
                        likeMask.alpha = 1f - abs(offset / (700).toFloat())
                        menuMask.alpha = 1f - abs(offset / (700).toFloat())
                    }
                    if (offset in 1..880) {
                        val xBack = 42f - abs(offset / (30).toFloat())
                        if (xBack in 10.2f..42.1f) {
                            ivBack.x = xBack
                            backMask.x = xBack
                        }

                        //like
                        val xMenuMask = 781f + abs(offset / (30).toFloat())
                        if (xMenuMask in 781f..810.1f) likeMask.x = xMenuMask

                        val xMenu = 789f + abs(offset / (30).toFloat())
                        if (xMenu in 789f..830.1f) ivLike.x = xMenu

                        //menu
                        val xLike = 930f + abs(offset / (30).toFloat())
                        if (xLike in 930f..958.1f) ivMenu.x = xLike

                        val xMask = 922f + abs(offset / (30).toFloat())
                        if (xMask in 922f..950.1f) menuMask.x = xMask
                    }
                }
            }
        }


    }

    override val layoutId: Int = R.layout.fragment_note_detail
    override fun getViewModelClass(): KClass<NoteDetailViewModel> = NoteDetailViewModel::class
}