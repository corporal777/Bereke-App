package org.wilderkek.bereke.ui.splash

import android.os.Bundle
import android.view.View
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.FragmentSplashBinding
import org.wilderkek.bereke.ui.base.BaseFragment
import kotlin.reflect.KClass

class SplashFragment : BaseFragment<SplashViewModel, FragmentSplashBinding>() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override val layoutId: Int = R.layout.fragment_splash
    override fun getViewModelClass(): KClass<SplashViewModel> = SplashViewModel::class
}