package org.wilderkek.bereke.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.LayoutPasswordBinding

class PasswordView : FrameLayout {

    private var onAgreeChangedSelection: (isChecked: Boolean) -> Unit = {}
    private var onPasswordValid: (password: PasswordData) -> Unit = {}

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var firstPasswordIsValid = false
    private var againPasswordIsValid = false


    private var mBinding = LayoutPasswordBinding.inflate(LayoutInflater.from(context), this, true)


    init {
        initPasswordField()
    }

    private fun initPasswordField() {
        mBinding.apply {
            etPassword.doAfterTextChanged {
                firstPasswordIsValid = validatePassword(mBinding.tvPasswordError, it.toString())
                validatePasswordsSame()
                onPasswordValid.invoke(PasswordData(matchPasswords(), it.toString()))
            }
            etAgainPassword.doAfterTextChanged {
                againPasswordIsValid = validatePassword(mBinding.tvAgainPasswordError, it.toString())
                validatePasswordsSame()
                onPasswordValid.invoke(PasswordData(matchPasswords(), it.toString()))
            }
        }
    }


    private fun matchPasswords(): Boolean {
        var isValid = true
        if (mBinding.etPassword.text.isNullOrEmpty()) {
            isValid = false
        }
        if (mBinding.etAgainPassword.text.isNullOrEmpty()) {
            isValid = false
        }
        if (mBinding.etPassword.text.toString() != mBinding.etAgainPassword.text.toString()) {
            isValid = false
        }
        if (!firstPasswordIsValid || !againPasswordIsValid) {
            isValid = false
        }
        return isValid
    }

    private fun validatePasswordsSame() {
        if (!firstPasswordIsValid || !againPasswordIsValid) {
            mBinding.tvPasswordsNotSameError.isVisible = false
        } else {
            mBinding.tvPasswordsNotSameError.isVisible =
                mBinding.etPassword.text.toString() != mBinding.etAgainPassword.text.toString()
        }
    }


    private fun validatePassword(textViewError: TextView, password: String?): Boolean {
        var isValid = true
        val errors = mutableListOf<String>()

        if (password.isNullOrEmpty()) {
            isValid = false
            textViewError.isVisible = false
        } else {
            textViewError.isVisible = true
            if ((password.length) < PASSWORD_MIN_LENGTH) {
                isValid = false
                errors.add(resources.getString(R.string.password_has_not_minimum_length))
            }

            if (!Regex(pattern = "\\d+").containsMatchIn(password)) {
                isValid = false
                errors.add(resources.getString(R.string.password_has_not_minimum_digit))
            }

            if (password.matches(Regex(".*[A-Z].*")) != true) {
                isValid = false
                errors.add(resources.getString(R.string.password_has_not_minimum_big_letter))
            }
            if (!isValid) {
                textViewError.text = errors.joinToString("\n", postfix = ".")
            } else {
                textViewError.isVisible = false
            }
        }
        return isValid
    }


    fun setPasswordsNotCorrect(visible: Boolean) {
        mBinding.tvPasswordsNotCorrectError.isVisible = visible
    }

    fun setPasswordChanged(block: (password: PasswordData) -> Unit): PasswordView {
        onPasswordValid = block
        return this
    }

    fun requestInputFocus(){
        mBinding.etPassword.requestFocus()
    }

    companion object {
        const val PASSWORD_MIN_LENGTH = 6
    }
}

data class PasswordData(
    var isValid: Boolean,
    var password: String
)