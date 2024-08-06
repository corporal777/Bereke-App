package org.wilderkek.bereke.ui.views.dialogs

import android.content.Context
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.BottomSheetChangePhotoBinding

class ActionsBottomSheetDialog(
    private val context: Context,
    private val firstActionText : String,
    private val secondActionText : String,
    private val thirdActionText : String = ""
) {


    private var onActionFirstClick: () -> Unit = {}
    private var onActionSecondClick: () -> Unit = {}
    private var onActionThirdClick: () -> Unit = {}

    private val mBinding = BottomSheetChangePhotoBinding.inflate(LayoutInflater.from(context))
    private var mDialog = BottomSheetDialog(context, R.style.TransparentBottomSheetDialogTheme)


    init {

        mDialog.setContentView(mBinding.root)
        mDialog.setCancelable(true)

        mBinding.apply {
            btnFirstAction.apply {
                text = firstActionText
                setOnClickListener {
                    onActionFirstClick.invoke()
                    mDialog.dismiss()
                }
            }
            btnSecondAction.apply {
                text = secondActionText
                setOnClickListener {
                    onActionSecondClick.invoke()
                    mDialog.dismiss()
                }
            }
            btnThirdAction.apply {
                isVisible = !thirdActionText.isNullOrBlank()
                text = thirdActionText
                setOnClickListener {
                    onActionThirdClick.invoke()
                    mDialog.dismiss()
                }
            }
            btnCancel.setOnClickListener {
                mDialog.dismiss()
            }
        }

        mDialog.show()
    }


    fun setActionFirstCallback(block: () -> Unit): ActionsBottomSheetDialog {
        onActionFirstClick = block
        return this
    }

    fun setActionSecondCallback(block: () -> Unit): ActionsBottomSheetDialog {
        onActionSecondClick = block
        return this
    }

    fun setActionThirdCallback(block: () -> Unit): ActionsBottomSheetDialog {
        onActionThirdClick = block
        return this
    }

}