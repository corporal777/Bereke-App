package org.wilderkek.bereke.ui.base

import android.app.Activity
import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.wilderkek.bereke.ui.main.MainActivity
import kotlin.reflect.KClass

abstract class BaseBottomSheetFragment<V : BaseViewModel, Binding : ViewDataBinding>(val fullScreen: Boolean = false) :
    BottomSheetDialogFragment() {

    lateinit var mBinding: Binding

    protected abstract val layoutId: Int
    protected lateinit var viewModel: V
    abstract fun getViewModelClass(): KClass<V>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel(null, getViewModelClass())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.behavior.skipCollapsed = true
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        dialog.setOnShowListener {
            if (fullScreen) setupFullHeight(dialog)
        }
        return dialog
    }

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val bottomSheet: BottomSheetDialog =
//            super.onCreateDialog(savedInstanceState) as BottomSheetDialog
//
//        val view: View = View.inflate(requireContext(), R.layout.fragment_metro_stations, null)
//        bottomSheet.setContentView(view)
//        val bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
//        (bottomSheetBehavior as BottomSheetBehavior<*>).peekHeight =
//            ScreenUtils(requireContext()).height
//        (bottomSheetBehavior as BottomSheetBehavior<*>).state = BottomSheetBehavior.STATE_EXPANDED
//
//        bottomSheet.setOnShowListener {
//            setupFullHeight(bottomSheet)
//        }
//        return bottomSheet
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (::mBinding.isInitialized.not()) {
            mBinding = DataBindingUtil.inflate(layoutInflater, layoutId, container, false)
            mBinding.lifecycleOwner = viewLifecycleOwner
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.progressData.observe(viewLifecycleOwner) {
            if (it) (requireActivity() as MainActivity).showLoadingDialog()
            else (requireActivity() as MainActivity).hideLoadingDialog()
        }
        viewModel.loadingData.observe(viewLifecycleOwner) {
            if (it) (requireActivity() as MainActivity).showLoadingView()
            else (requireActivity() as MainActivity).hideLoadingView()
        }
    }

    fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun showSnackBar(message: String, icon: Int) {
        (requireActivity() as MainActivity).showSnackBar(message, icon)
    }

    fun hideKeyboard(view: View) {
        view.clearFocus()
        val inn: InputMethodManager? =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        inn?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet: FrameLayout =
            dialog!!.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        showFullScreenBottomSheet(bottomSheet)
    }

    private fun showFullScreenBottomSheet(bottomSheet: FrameLayout) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = Resources.getSystem().displayMetrics.heightPixels
        bottomSheet.layoutParams = layoutParams
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding.unbind()
    }
}