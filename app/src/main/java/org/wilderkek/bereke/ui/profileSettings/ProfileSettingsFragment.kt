package org.wilderkek.bereke.ui.profileSettings

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.navigation.fragment.findNavController
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.FragmentProfileSettingsBinding
import org.wilderkek.bereke.ui.base.BaseFragment
import org.wilderkek.bereke.ui.interfaces.ToolbarFragment
import org.wilderkek.bereke.ui.profileSettings.changeLogin.ChangeLoginFragment
import org.wilderkek.bereke.ui.profileSettings.changePassword.ChangePasswordFragment
import org.wilderkek.bereke.ui.views.dialogs.ActionsBottomSheetDialog
import org.wilderkek.bereke.util.onTextChanged
import org.wilderkek.bereke.util.setUserImage
import kotlin.reflect.KClass

class ProfileSettingsFragment :
    BaseFragment<ProfileSettingsViewModel, FragmentProfileSettingsBinding>(true),
    ToolbarFragment {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.doOnLayout { startPostponedEnterTransition() }
        mBinding.apply {
            etFirstName.onTextChanged { text ->
                viewModel.performChangeFirstName(text.toString())
            }

            etLastName.onTextChanged { text ->
                viewModel.performChangeLastName(text.toString())
            }

            etEmail.onTextChanged { text ->
                viewModel.performChangeEmail(text.toString())
            }

            etPhone.onTextChanged { text ->
                viewModel.performChangePhone(text.toString())
            }

            tvEditLogin.setOnClickListener { showChangeLogin() }
            tvEditPassword.setOnClickListener { showChangePassword() }
            btnChangeImage.setOnClickListener { showChangePhotoDialog() }
            btnSave.setOnClickListener { viewModel.updateUser() }

        }
        observeUser()
        observeBtnSaveEnable()
        observeUserUpdated()
    }

    private fun observeUser() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                mBinding.apply {
                    ivUserPhoto.setUserImage(user.image)
                    etFirstName.setText(user.firstName)
                    etLastName.setText(user.lastName)
                    etLogin.setText(user.login)
                    etPassword.setText("password")
                    etEmail.setText(user.contactEmail)
                    etPhone.setText(user.contactPhone)
                }
            }
        }
    }

    private fun observeBtnSaveEnable() {
        viewModel.enableBtnSave.observe(viewLifecycleOwner) {
            mBinding.btnSave.isEnabled = it
        }
    }

    private fun observeUserUpdated() {
        viewModel.userUpdated.observe(viewLifecycleOwner) {
            if (it) findNavController().navigateUp()
        }
    }

    private fun showChangePhotoDialog() {
        ActionsBottomSheetDialog(
            requireContext(),
            getString(R.string.open_gallery),
            getString(R.string.open_camera)
        )
            .setActionFirstCallback { viewModel.takeGalleryImage() }
            .setActionSecondCallback { viewModel.takeCameraImage() }
    }

    private fun showChangePassword() {
        val changePasswordDialog = ChangePasswordFragment()
        changePasswordDialog.show(
            requireActivity().supportFragmentManager,
            "change_password_dialog"
        )
    }

    private fun showChangeLogin() {
        val changeLoginDialog = ChangeLoginFragment()
        changeLoginDialog.show(requireActivity().supportFragmentManager, "change_login_dialog")
        changeLoginDialog.setLoginCallback {
            mBinding.etLogin.setText(it)
        }
    }

    override val layoutId: Int = R.layout.fragment_profile_settings
    override fun getViewModelClass(): KClass<ProfileSettingsViewModel> =
        ProfileSettingsViewModel::class

    override val title: CharSequence by lazy { getString(R.string.profile_settings) }
    override fun toolbarIconsContainer(viewGroup: ViewGroup) {}
    override fun dispatchBack(): (() -> Unit)? = { findNavController().navigateUp() }
}