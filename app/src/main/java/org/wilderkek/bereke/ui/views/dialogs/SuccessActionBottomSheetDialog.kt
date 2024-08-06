package org.wilderkek.bereke.ui.views.dialogs

import android.content.Context
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.wilderkek.bereke.databinding.BottomSheetNoteCreatedBinding

class SuccessActionBottomSheetDialog(
    val context: Context,
    val title: String,
    val message: String,
    val btnActionText: String
) {

    private var onActionClick: () -> Unit = {}

    private val mBinding = BottomSheetNoteCreatedBinding.inflate(LayoutInflater.from(context))
    private var mDialog = BottomSheetDialog(context)

    init {

        mDialog.setContentView(mBinding.root)
        mDialog.setCancelable(false)

        mBinding.tvTitle.text = title
        mBinding.tvMessage.text = message

        mBinding.btnGoHome.apply {
            text = btnActionText
            setOnClickListener {
                onActionClick.invoke()
                mDialog.dismiss()
            }
        }

        mDialog.show()
    }

    fun setActionCallback(block: () -> Unit): SuccessActionBottomSheetDialog {
        onActionClick = block
        return this
    }


}