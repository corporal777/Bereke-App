package org.wilderkek.bereke.ui.profileSettings.changeLogin

import android.os.Bundle
import android.view.View
import org.wilderkek.bereke.util.onTextChanged
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.BottomSheetChangeLoginBinding
import org.wilderkek.bereke.ui.base.BaseBottomSheetFragment
import kotlin.reflect.KClass

class ChangeLoginFragment :
    BaseBottomSheetFragment<ChangeLoginViewModel, BottomSheetChangeLoginBinding>() {


    private var onSaveNewLogin: (login: String) -> Unit = {}


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            ivClose.setOnClickListener {
                dismiss()
            }
            etLogin.onTextChanged {
                viewModel.performChangeLogin(it.toString())
            }
            btnSave.setOnClickListener {
                viewModel.updateLogin()
            }
        }
        observeLoginError()
        observeLoginUpdated()
    }


    private fun observeLoginError() {
        viewModel.loginError.observe(viewLifecycleOwner) {
            if (it == null) mBinding.tilLogin.error = null
            else mBinding.tilLogin.error = getString(it)
        }
    }

    private fun observeLoginUpdated() {
        viewModel.loginUpdated.observe(viewLifecycleOwner){
            if (it){
                onSaveNewLogin.invoke(mBinding.etLogin.text.toString())
                dismiss()
                showSnackBar(getString(R.string.login_success_changed), R.drawable.ic_done)
            }
        }
    }

    fun setLoginCallback(block: (password: String) -> Unit): ChangeLoginFragment {
        onSaveNewLogin = block
        return this
    }

    override val layoutId: Int = R.layout.bottom_sheet_change_login
    override fun getViewModelClass(): KClass<ChangeLoginViewModel> = ChangeLoginViewModel::class
}