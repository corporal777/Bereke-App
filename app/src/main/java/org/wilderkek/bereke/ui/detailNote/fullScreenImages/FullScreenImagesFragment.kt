package org.wilderkek.bereke.ui.detailNote.fullScreenImages

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.FragmentFullScreenImagesBinding
import org.wilderkek.bereke.ui.base.BaseFragment
import org.wilderkek.bereke.ui.detailNote.NoteDetailFragmentArgs
import org.wilderkek.bereke.ui.detailNote.fullScreenImages.items.FullScreenImageItem
import kotlin.reflect.KClass

class FullScreenImagesFragment :
    BaseFragment<FullScreenImagesViewModel, FragmentFullScreenImagesBinding>() {

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args: FullScreenImagesFragmentArgs by navArgs()
        viewModel.setFullImages(args.images)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            viewPagerFullImages.apply {
                adapter = groupAdapter
                offscreenPageLimit = 5
            }
            ivClose.setOnClickListener {
                findNavController().navigateUp()
            }
        }
        observeImages()
    }

    private fun observeImages() {
        viewModel.imagesData.observe(viewLifecycleOwner) {
            groupAdapter.update(it.images.map { image ->
                FullScreenImageItem(image)
            })
        }
    }

    override val layoutId: Int = R.layout.fragment_full_screen_images
    override fun getViewModelClass(): KClass<FullScreenImagesViewModel> =
        FullScreenImagesViewModel::class
}