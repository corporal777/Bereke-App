package org.wilderkek.bereke.ui.base

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.wilderkek.bereke.ui.main.MainViewModel
import org.wilderkek.bereke.databinding.ActivityMainBinding

abstract class BaseActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    val viewModel: MainViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplash()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun installSplash() {
        installSplashScreen().apply {
            viewModel.tokenUpdated.observe(this@BaseActivity) { updated ->
                setKeepVisibleCondition { !updated }
            }
        }
    }
}