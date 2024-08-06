package org.wilderkek.bereke.ui.views.dialogs

import android.content.Context
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.BottomSheetChangePhotoBinding
import org.wilderkek.bereke.databinding.BottomSheetNoteComplaintBinding
import org.wilderkek.bereke.util.onTextChanged

class ComplaintBottomSheetDialog(private val context: Context) {


    private var onActionSendClick: (message: String) -> Unit = {}

    private val mBinding = BottomSheetNoteComplaintBinding.inflate(LayoutInflater.from(context))
    private var mDialog = BottomSheetDialog(context)

    private var complaintMessage = ""

    init {

        mDialog.setContentView(mBinding.root)
        mDialog.setCancelable(true)

        mBinding.apply {
            etComplaint.onTextChanged {
                tilComplaint.error = null
                complaintMessage = it.toString()
            }
            btnSend.setOnClickListener {
                if (complaintMessage.isNullOrBlank())
                    tilComplaint.error = context.getString(R.string.empty_field)
                else {
                    onActionSendClick.invoke(complaintMessage)
                    mDialog.dismiss()
                }
            }

            ivClose.setOnClickListener {
                mDialog.dismiss()
            }
        }

        mDialog.show()
    }


    fun setActionSendCallback(block: (message: String) -> Unit): ComplaintBottomSheetDialog {
        onActionSendClick = block
        return this
    }
}