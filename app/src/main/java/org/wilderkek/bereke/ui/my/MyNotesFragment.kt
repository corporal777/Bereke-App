package org.wilderkek.bereke.ui.my

import android.animation.Animator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.wilderkek.bereke.util.onPageChanged
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.FragmentMyNotesBinding
import org.wilderkek.bereke.ui.base.BaseFragment
import org.wilderkek.bereke.ui.interfaces.ToolbarFragment
import org.wilderkek.bereke.ui.my.active.ActiveNotesFragment
import org.wilderkek.bereke.ui.my.inactive.InActiveNotesFragment
import org.wilderkek.bereke.ui.my.moderation.ModerationNotesFragment
import org.wilderkek.bereke.util.getPagerAdapter
import org.wilderkek.bereke.util.onPageSelected
import kotlin.reflect.KClass

class MyNotesFragment : BaseFragment<MyNotesViewModel, FragmentMyNotesBinding>(true),
    ToolbarFragment {


    private val onScrollChange = object : OnScrollStateChange {
        override fun onScrollUp(value: Int) = showView(mBinding.lnTabsContainer)
        override fun onScrollDown(value: Int) = hideView(mBinding.lnTabsContainer)
        override fun onScrollOffset(value: Float) {}
    }

    private val fragments by lazy {
        listOf(
            ActiveNotesFragment(onScrollChange, viewModel),
            InActiveNotesFragment(onScrollChange, viewModel),
            ModerationNotesFragment(onScrollChange, viewModel)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            viewPager.run {
                doOnPreDraw { startPostponedEnterTransition() }
                isSaveEnabled = false
                adapter = getPagerAdapter(childFragmentManager, fragments)
                viewModel.setTabPosition(currentItem)
                onPageSelected { viewModel.setTabPosition(it) }
            }
            btnTabActive.setOnClickListener { viewPager.currentItem = 0 }
            btnTabInactive.setOnClickListener { viewPager.currentItem = 1 }
            btnTabModeration.setOnClickListener { viewPager.currentItem = 2 }
        }
        observeTabPosition()
    }

    private fun observeTabPosition() {
        viewModel.tabPosition.observe(viewLifecycleOwner) { pos ->
            mBinding.lnTabs.apply {
                for (p in 0 until childCount) {
                    getChildAt(p).isSelected = p == pos
                }
            }
        }
    }

    private fun hideView(animationView: View) {
        animationView.animate()
            .translationY((-animationView.bottom).toFloat())
            .setInterpolator(AccelerateInterpolator()).start()

//        if (animationView == null || animationView.visibility == View.GONE) {
//            return
//        }
//        val animationDown =
//            AnimationUtils.loadAnimation(animationView.context, R.anim.move_up)
//        animationDown.setAnimationListener(object : Animation.AnimationListener {
//            override fun onAnimationStart(animation: Animation) {
//                animationView.visibility = View.VISIBLE
//                isFinishAnimation = false
//            }
//
//            override fun onAnimationEnd(animation: Animation) {
//                animationView.visibility = View.GONE
//                isFinishAnimation = true
//            }
//
//            override fun onAnimationRepeat(animation: Animation) {}
//        })
//        if (isFinishAnimation) {
//            animationView.startAnimation(animationDown)
//        }
    }

    private fun showView(animationView: View) {
        animationView.animate().translationY(0f)
            .setInterpolator(DecelerateInterpolator())
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }

            })
            .start()

//        if (animationView == null || animationView.visibility == View.VISIBLE) {
//            return
//        }
//        val animationUp =
//            AnimationUtils.loadAnimation(animationView.context, R.anim.move_down)
//        animationUp.setAnimationListener(object : Animation.AnimationListener {
//            override fun onAnimationStart(animation: Animation) {
//                animationView.visibility = View.VISIBLE
//                isFinishAnimation = false
//            }
//
//            override fun onAnimationEnd(animation: Animation) {
//                isFinishAnimation = true
//            }
//
//            override fun onAnimationRepeat(animation: Animation) {}
//        })
//        if (isFinishAnimation) {
//            animationView.startAnimation(animationUp)
//        }
    }

    interface OnScrollStateChange {
        fun onScrollUp(value: Int)
        fun onScrollDown(value: Int)
        fun onScrollOffset(value: Float)
    }

    override val layoutId = R.layout.fragment_my_notes
    override fun getViewModelClass(): KClass<MyNotesViewModel> = MyNotesViewModel::class
    override val title: CharSequence by lazy { getString(R.string.my_notes) }
    override fun toolbarIconsContainer(viewGroup: ViewGroup) {}
    override fun dispatchBack(): (() -> Unit) = { findNavController().navigateUp() }
}