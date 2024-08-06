package org.wilderkek.bereke.ui.profile

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import org.wilderkek.bereke.ui.views.ToolbarIconView
import org.wilderkek.bereke.ui.views.dialogs.ProfileMenuBottomSheetDialog
import org.wilderkek.bereke.util.setUserImage
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.FragmentProfileBinding
import org.wilderkek.bereke.ui.base.BaseFragment
import org.wilderkek.bereke.ui.interfaces.ToolbarFragment
import kotlin.reflect.KClass


class ProfileFragment : BaseFragment<ProfileViewModel, FragmentProfileBinding>(), ToolbarFragment {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadUser()
        mBinding.apply {
            tvProfileSettings.setOnClickListener { showProfileSettings() }
            tvMyNotes.setOnClickListener { showMyNotes() }
        }
        observeUser()
        observeContentLoading()
    }

    private fun observeUser() {
        mBinding.apply {
            viewModel.userData.observe(viewLifecycleOwner) { user ->
                lnActiveNotes.isVisible = user != null
                lnModerationNotes.isVisible = user != null
                tvUserId.isVisible = user != null

                tvCurrentTown.text = viewModel.getCurrentTown() ?: root.context.getString(R.string.not_chosen)
                user.let {
                    ivUserPhoto.setUserImage(it?.image ?: R.drawable.avatar_empty_square)
                    tvUserName.text = it?.nameLastName?:getString(R.string.user_unauthorized)
                    tvUserId.text = it?.publicId
                    tvActiveNotes.text = it?.userNotes?.activeNotes
                    tvModerationNotes.text = it?.userNotes?.pendingNotes
                }
            }
        }
    }


    private fun observeContentLoading() {
        viewModel.loadingData.observe(viewLifecycleOwner) {
            mBinding.lnProfile.isVisible = !it
            val anim = AlphaAnimation(0.0f, 1.0f)
            anim.duration = 150
            mBinding.lnProfile.startAnimation(anim)
        }
    }

    private fun showPopup(view: View) {
        ProfileMenuBottomSheetDialog(requireContext(), viewModel.isUserLoggedOut()).apply {
            setChangeTownCallback { showTown() }
            setLoginCallback { showLogin() }
        }
    }

    private fun showLogin() {
        if (viewModel.isUserLoggedOut())
            findNavController().navigate(R.id.login_fragment, bundleOf("fromProfile" to true))
        else viewModel.logout()
    }

    private fun showProfileSettings() {
        findNavController().navigate(R.id.profile_settings_fragment)
    }

    private fun showMyNotes(){
        findNavController().navigate(R.id.my_notes_fragment)
    }

    private fun showTown() {
        findNavController().navigate(R.id.town_fragment)
    }

    override val layoutId: Int = R.layout.fragment_profile
    override fun getViewModelClass(): KClass<ProfileViewModel> = ProfileViewModel::class
    override val title: CharSequence by lazy { getString(R.string.profile) }
    override fun toolbarIconsContainer(viewGroup: ViewGroup) {
        viewGroup.apply {
            addView(ToolbarIconView(context).apply {
                setImageAsIcon(R.drawable.ic_profile_menu)
                setOnClickListener {
                    showPopup(it)
                }
            })
        }
    }

    override fun dispatchBack(): (() -> Unit)? = null
}