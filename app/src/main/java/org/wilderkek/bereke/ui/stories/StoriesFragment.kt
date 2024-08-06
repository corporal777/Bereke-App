package org.wilderkek.bereke.ui.stories

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import jp.shts.android.storiesprogressview.StoriesProgressView
import org.wilderkek.bereke.util.formatToDayMonth
import org.wilderkek.bereke.util.setImage
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.FragmentStoriesBinding
import org.wilderkek.bereke.ui.base.BaseFragment
import kotlin.reflect.KClass

class StoriesFragment : BaseFragment<StoriesViewModel, FragmentStoriesBinding>() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val stories = StoriesFragmentArgs.fromBundle(requireArguments()).story
        viewModel.setStories(stories)
    }

    @SuppressLint("ClickableViewAccessibility")
    private val onTouchListener = View.OnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mBinding.stories.pause()
                return@OnTouchListener false
            }
            MotionEvent.ACTION_UP -> {
                mBinding.stories.resume()
                return@OnTouchListener false
            }
        }
        false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.apply {
            stories.apply {
                setStoriesCount(viewModel.getStoriesCount())
                setStoryDuration(3000L)
                setStoriesListener(object : StoriesProgressView.StoriesListener {
                    override fun onComplete() {
                        findNavController().navigateUp()
                    }
                    override fun onPrev() = viewModel.onPrevStory()
                    override fun onNext() = viewModel.onNextStory()
                })
                startStories()
            }
            reverse.apply {
                setOnClickListener {
                    stories.reverse()
                }
                setOnTouchListener(onTouchListener)
            }
            skip.apply {
                setOnClickListener {
                    stories.skip()
                }
                setOnTouchListener(onTouchListener)
            }
            ivClose.setOnClickListener {
                findNavController().navigateUp()
            }
        }
        observeStory()
    }

    private fun observeStory() {
        viewModel.story.observe(viewLifecycleOwner) {
            mBinding.apply {
                tvTitle.text = it.title
                tvDescription.text = it.message
                ivBackgroundImage.setImage(it.image)
                tvDate.text = it.date.formatToDayMonth()
                btnCall.apply {
                    isVisible = !it.phone.value.isNullOrEmpty()
                    text = if (it.phone.type == "whatsapp") getString(R.string.write_on_whatsapp)
                    else getString(R.string.call)
                    setOnClickListener {

                    }
                }
            }
        }
    }

    override val layoutId: Int = R.layout.fragment_stories

    override fun getViewModelClass(): KClass<StoriesViewModel> = StoriesViewModel::class
}