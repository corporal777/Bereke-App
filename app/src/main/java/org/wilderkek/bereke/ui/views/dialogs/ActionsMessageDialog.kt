package org.wilderkek.bereke.ui.views.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import org.wilderkek.bereke.databinding.DialogAcceptCancelBinding

class ActionsMessageDialog(
    val context: Context,
    val title: CharSequence,
    val message: CharSequence,
    val btnAcceptText: String = "",
    val isCancelable: Boolean = true
) {

    private val mBinding = DialogAcceptCancelBinding.inflate(LayoutInflater.from(context))

    private var clickAccept: () -> Unit = {}
    private var clickCancel: () -> Unit = {}

    private lateinit var mAlertDialog: AlertDialog
    private val mBuilder = AlertDialog.Builder(context)

    init {
        mBuilder.setView(mBinding.root)
        mBuilder.setCancelable(isCancelable)

        mBinding.tvMessage.apply {
            text = message

        }
        mBinding.apply {
            tvTitle.text = title
            tvMessage.text = message

            btnAccept.apply {
                if (!btnAcceptText.isNullOrBlank()) text = btnAcceptText
                setOnClickListener {
                    clickAccept.invoke()
                    mAlertDialog.dismiss()
                }
            }

            btnCancel.setOnClickListener {
                clickCancel.invoke()
                mAlertDialog.dismiss()
            }
        }


        mAlertDialog = mBuilder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 40)
        mAlertDialog.window?.setBackgroundDrawable(inset)
        mAlertDialog.show()
    }

    fun setAcceptCallback(block: () -> Unit): ActionsMessageDialog {
        clickAccept = block
        return this
    }

    fun setCancelCallback(block: () -> Unit): ActionsMessageDialog {
        clickCancel = block
        return this
    }

}