package org.wilderkek.bereke.ui.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import org.wilderkek.bereke.databinding.DialogLoadingProgressBinding

class LoadingProgressDialog (val context: Context) {

    private val mBinding = DialogLoadingProgressBinding.inflate(LayoutInflater.from(context))

    private lateinit var mAlertDialog: AlertDialog
    private val mBuilder = AlertDialog.Builder(context)

    init {
        mBuilder.setView(mBinding.root)
        mBuilder.setCancelable(false)

        mAlertDialog = mBuilder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 0)
        mAlertDialog.window?.setBackgroundDrawable(inset)
    }

    fun showLoading() {
        mBinding.progressView.playAnimation()
        mAlertDialog.show()
    }

    fun hideLoading(){
        mBinding.progressView.pauseAnimation()
        mAlertDialog.dismiss()
    }
}