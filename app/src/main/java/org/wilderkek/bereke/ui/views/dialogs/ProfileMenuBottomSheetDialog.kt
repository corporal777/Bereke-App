package org.wilderkek.bereke.ui.views.dialogs

import android.content.Context
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.BottomSheetProfileMenuBinding

class ProfileMenuBottomSheetDialog(
    private val context: Context,
    private val userLoggedOut: Boolean
) {

    private var onLoginClick: () -> Unit = {}
    private var onChangeTownClick: () -> Unit = {}

    private val mBinding = BottomSheetProfileMenuBinding.inflate(LayoutInflater.from(context))
    private var mDialog = BottomSheetDialog(context, R.style.TransparentBottomSheetDialogTheme)

    init {

        mDialog.setContentView(mBinding.root)
        mDialog.setCancelable(true)

        mBinding.btnLogin.apply {
            text = if (userLoggedOut) context.getString(R.string.login_to_account)
            else context.getString(R.string.logout_from_account)

            setOnClickListener {
                onLoginClick.invoke()
                mDialog.dismiss()
            }
        }

        mBinding.btnChangeTown.setOnClickListener {
            onChangeTownClick.invoke()
            mDialog.dismiss()
        }

        mBinding.btnCancel.setOnClickListener {
            mDialog.dismiss()
        }

        mDialog.show()
    }

    fun setLoginCallback(block: () -> Unit): ProfileMenuBottomSheetDialog {
        onLoginClick = block
        return this
    }

    fun setChangeTownCallback(block: () -> Unit): ProfileMenuBottomSheetDialog {
        onChangeTownClick = block
        return this
    }


}