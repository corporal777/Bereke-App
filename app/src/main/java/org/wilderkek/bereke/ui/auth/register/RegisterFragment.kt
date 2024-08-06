package org.wilderkek.bereke.ui.auth.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import org.wilderkek.bereke.ui.auth.register.RegisterViewModel.Companion.ERROR_LOGIN_CORRECT
import org.wilderkek.bereke.ui.auth.register.RegisterViewModel.Companion.ERROR_LOGIN_EXISTS
import org.wilderkek.bereke.ui.auth.register.RegisterViewModel.Companion.ERROR_LOGIN_INCORRECT
import org.wilderkek.bereke.ui.base.BaseFragment
import org.wilderkek.bereke.ui.confirmCode.ConfirmCodeFragment
import org.wilderkek.bereke.ui.interfaces.ToolbarFragment
import org.wilderkek.bereke.ui.views.dialogs.ActionsMessageDialog
import org.wilderkek.bereke.util.onTextChanged
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.FragmentRegisterBinding
import kotlin.reflect.KClass

class RegisterFragment : BaseFragment<RegisterViewModel, FragmentRegisterBinding>(true),
    ToolbarFragment {

    private var fromNote : Boolean = false
    private var fromProfile : Boolean = false

    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            fromNote = RegisterFragmentArgs.fromBundle(it).fromNote
            fromProfile = RegisterFragmentArgs.fromBundle(it).fromProfile
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.doOnPreDraw { startPostponedEnterTransition() }
        view.doOnLayout { showUseGoogleAccountDataDialog() }
        observeBtnRegisterEnabled()
        observeErrors()
        observeRegisterSuccess()
        observeLoginIsUnique()

        mBinding.apply {
            etFirstName.onTextChanged {
                it.toString().let { name -> viewModel.performNameChanged(name) }
            }
            etLastName.onTextChanged {
                it.toString().let { lastName -> viewModel.performLastNameChanged(lastName) }
            }
            etEmail.onTextChanged {
                it.toString().let { email -> viewModel.performEmailChanged(email) }
            }
            passwordView.setPasswordChanged { password ->
                viewModel.performPasswordChanged(password)
            }

            btnRegister.setOnClickListener {
                hideKeyboard(it)
                viewModel.checkLoginIsUnique()
            }
        }
    }

    private fun observeLoginIsUnique() {
        viewModel.loginIsUnique.observe(viewLifecycleOwner) {
            if (it) showConfirmCodeDialog()
        }
    }

    private fun observeBtnRegisterEnabled() {
        viewModel.enableBtnRegister.observe(viewLifecycleOwner) {
            mBinding.btnRegister.isEnabled = it
        }
    }

    private fun observeErrors() {
        mBinding.apply {
            viewModel.firstNameError.observe(viewLifecycleOwner) {
                if (it) tilFirstName.error = getString(R.string.empty_field)
                else tilFirstName.error = null
            }
            viewModel.lastNameError.observe(viewLifecycleOwner) {
                if (it) tilLastName.error = getString(R.string.empty_field)
                else tilLastName.error = null
            }

            viewModel.emailError.observe(viewLifecycleOwner) {
                when (it) {
                    ERROR_LOGIN_CORRECT -> tilEmail.error = null
                    ERROR_LOGIN_EXISTS -> {
                        showToast("Логин занят. Введите другой логин!")
                        tilEmail.error = getString(R.string.login_not_free_error)
                    }
                    ERROR_LOGIN_INCORRECT -> tilEmail.error = getString(R.string.email_or_phone_incorrect)
                }
            }

            viewModel.passwordError.observe(viewLifecycleOwner) {
                passwordView.setPasswordsNotCorrect(it)
            }
        }

    }

    private fun observeRegisterSuccess() {
        viewModel.successResponse.observe(viewLifecycleOwner) {
            if (it == null) showToast("Server error!")
            else {
                showGreetings(it.firstName)
                if (fromNote) showCreateNote()
                else if (fromProfile) showProfile()
                else showHome()
            }
        }
    }

    private fun showGreetings(userName: String) {
        showSnackBar("Здравствуйте, $userName", R.drawable.ic_hello)
    }

    private fun showHome() {
        findNavController().navigate(RegisterFragmentDirections.registerToHome())
    }

    private fun showCreateNote(){
        findNavController().navigate(R.id.create_note_fragment, null, navOptions {
            popUpTo(R.id.register_fragment) { inclusive = true }
        })
    }

    private fun showProfile() {
        if (!findNavController().popBackStack(R.id.profile_fragment, false)) {
            findNavController().navigate(R.id.profile_fragment, null, navOptions {
                popUpTo(R.id.register_fragment) { inclusive = true }
            })
        }
    }

    private fun showConfirmCodeDialog() {
        val type = if (viewModel.loginType == "email") ConfirmCodeFragment.ConfirmType.EMAIL
        else ConfirmCodeFragment.ConfirmType.PHONE

        val confirmCode = ConfirmCodeFragment(type, viewModel.getValidatedLogin())
        confirmCode.show(requireActivity().supportFragmentManager, "confirm_code_dialog")
        confirmCode.setCodeCallback {
            viewModel.register()
        }
    }

    private fun showUseGoogleAccountDataDialog() {
        ActionsMessageDialog(
            requireContext(),
            "Использовать данные Google аккаунта?",
            "Поля ввода для регистрации будут заполнены данными вашего аккаунта. Приложение не получает доступ к паролю аккаунта."
        ).setAcceptCallback {
            showGoogleSignIn()
        }
    }

    private fun showGoogleSignIn() {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        val account = GoogleSignIn.getLastSignedInAccount(requireActivity())
        if (account != null) {
            setGoogleAccountData(account)
        } else {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, 101)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnCompleteListener {
                    if (it.isSuccessful && it.result != null) {
                        setGoogleAccountData(it.result)
                    } else {
                        it.exception?.printStackTrace()
                    }
                }
        }
    }

    private fun setGoogleAccountData(account: GoogleSignInAccount) {
        val name = account.givenName ?: ""
        val lastName = account.familyName ?: ""
        val email = account.email ?: ""
        mBinding.apply {
            etFirstName.setText(name)
            etLastName.setText(lastName)
            etEmail.setText(email)
            passwordView.requestInputFocus()
        }
        mGoogleSignInClient.signOut()
    }


    override val layoutId: Int = R.layout.fragment_register
    override fun getViewModelClass(): KClass<RegisterViewModel> = RegisterViewModel::class
    override val title: CharSequence by lazy { getString(R.string.registration) }
    override fun toolbarIconsContainer(viewGroup: ViewGroup) {}
    override fun dispatchBack(): (() -> Unit)? = null
}