package org.wilderkek.bereke.ui.main

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.vk.id.AccessToken
import com.vk.id.VKID
import com.vk.id.VKIDAuthFail
import com.vk.id.internal.log.LogEngine
import com.yandex.mapkit.MapKitFactory
import org.wilderkek.bereke.R
import org.wilderkek.bereke.ui.auth.login.LoginFragment
import org.wilderkek.bereke.ui.auth.register.RegisterFragment
import org.wilderkek.bereke.ui.base.BaseActivity
import org.wilderkek.bereke.ui.createNote.CreateNoteFragment
import org.wilderkek.bereke.ui.detailNote.NoteDetailFragment
import org.wilderkek.bereke.ui.detailNote.fullScreenImages.FullScreenImagesFragment
import org.wilderkek.bereke.ui.editNote.EditNoteFragment
import org.wilderkek.bereke.ui.favoriteNotes.FavoriteNotesFragment
import org.wilderkek.bereke.ui.home.HomeFragment
import org.wilderkek.bereke.ui.interfaces.ToolbarFragment
import org.wilderkek.bereke.ui.my.MyNotesFragment
import org.wilderkek.bereke.ui.profile.ProfileFragment
import org.wilderkek.bereke.ui.profileSettings.ProfileSettingsFragment
import org.wilderkek.bereke.ui.stories.StoriesFragment
import org.wilderkek.bereke.ui.town.TownFragment
import org.wilderkek.bereke.ui.views.CustomSnackBar
import org.wilderkek.bereke.ui.views.LoadingProgressDialog
import org.wilderkek.bereke.util.*


class MainActivity : BaseActivity() {

    private val mLoadingDialog by lazy { LoadingProgressDialog(this) }

    private val fragmentsLifecycleCallback = getFragmentLifecycleCallback(
        onViewCreated = { f ->
            setupNavBarItems(f)
            when (f) {
                is LoginFragment,
                is RegisterFragment,
                is TownFragment,
                is CreateNoteFragment,
                is StoriesFragment,
                is MyNotesFragment,
                is NoteDetailFragment,
                is FullScreenImagesFragment,
                is EditNoteFragment,
                is ProfileSettingsFragment -> hideNavBar()
                else -> showNavBar()
            }
            if (f is ToolbarFragment) {
                binding.apply {
                    mainAppBar.isVisible = true
                    authToolbar.apply {
                        customTitle.text = f.title
                        f.toolbarIconsContainer(iconsContainer.apply { removeAllViews() })
                        if (f.dispatchBack() == null){
                            ivBack.isVisible = false
                            customTitle.setPadding(10.dp, 0,0,0)
                        }else {
                            ivBack.isVisible = true
                            ivBack.setOnClickListener { f.dispatchBack()?.invoke() }
                            customTitle.setPadding(0, 0,0,0)
                        }
                    }
                }
            } else {
                binding.mainAppBar.isVisible = false
            }
        },
        onViewDestroyed = {},
        onFragmentStarted = { f ->
            when (f) {
                is StoriesFragment -> doEdgeWindow()
                is NoteDetailFragment -> setWindowTransparency()
                else -> cancelWindowTransparency()
            }
        },
        onFragmentStopped = { f ->

        }
    )

    private val backClick = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val navHost =
                supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
            val fr = navHost.childFragmentManager.fragments[0]

            if (fr is ProfileFragment || fr is FavoriteNotesFragment) {
                if (!findNavController().popBackStack(R.id.home_fragment, false)) showHome()
            } else if (fr is HomeFragment) {
                finish()
            } else {
                findNavController().navigateUp()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this, backClick)

        registerFragmentLifecycleCallback()
        setupMainNavBar()
        setUpYandexMap()
        observeUserTown()
        observeSnackBarMessage()

        initVk()
    }

    private fun initVk(){
        val vkid = VKID(this)
        vkid.authorize(this@MainActivity, object : VKID.AuthCallback {
            override fun onSuccess(accessToken: AccessToken) {
                val token = accessToken.token
                Log.e("TOKEN", token)
            }

            override fun onFail(fail: VKIDAuthFail) {
                Log.e("ERROR", fail.description)
            }
        })
        VKID.logsEnabled = true
        VKID.logEngine = object: LogEngine {
            override fun log(logLevel: LogEngine.LogLevel, tag: String, message: String, throwable: Throwable?) {
                Log.e(tag, message)
            }
        }
    }

    private fun observeUserTown() {
        viewModel.userTown.observe(this) {
            if (!it.isNullOrEmpty()) showHome()
            else showChangeTown()
        }
    }

    private fun observeSnackBarMessage(){
        viewModel.snackBarMessage.observe(this){
            showSnackBar(it, R.drawable.ic_done)
        }
    }


    private fun setupMainNavBar() {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        binding.navInclude.mainBottomNavView.setupWithNavController(navHost.navController)
        binding.navInclude.mainBottomNavView.setOnItemReselectedListener { item ->
        }
        binding.navInclude.mainBottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.main -> {
                    if (!findNavController().popBackStack(R.id.home_fragment, false)) {
                        showHome()
                    }
                    true
                }
                R.id.profile -> {
                    findNavController().navigate(R.id.profile_fragment)
                    true
                }
                R.id.favorites -> {
                    findNavController().navigate(R.id.favorite_notes_fragment)
                    true
                }
                else -> false
            }
        }
        binding.navInclude.ivNewNote.setOnClickListener {
            if (viewModel.isUserAuthorized()) showCreateNote()
            else showLogin()
        }
    }

    private fun findNavController() = findNavController(R.id.fragmentContainerView)

    private fun showChangeTown() {
        findNavController().navigate(R.id.town_fragment, null,
            navOptions {
                popUpTo(R.id.nav_graph) { inclusive = true }
            })
    }

    private fun showHome() {
        findNavController().navigate(R.id.home_fragment, null,
            navOptions {
                popUpTo(R.id.nav_graph) { inclusive = true }
            })
    }


    private fun showCreateNote() {
        findNavController().navigate(R.id.create_note_fragment)
    }

    private fun showLogin() {
        findNavController().navigate(R.id.login_fragment, bundleOf("fromNote" to true))
    }

    private fun setUpYandexMap(){
        MapKitFactory.initialize(this)
    }

    private fun setupNavBarItems(f: Fragment) {
        when (f) {
            is HomeFragment -> binding.navInclude.mainBottomNavView.menu.findItem(R.id.main).isChecked =
                true
            is ProfileFragment -> binding.navInclude.mainBottomNavView.menu.findItem(R.id.profile).isChecked =
                true
        }
    }

    override fun onDestroy() {
        unregisterFragmentLifecycleCallback()
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        MapKitFactory.getInstance().onStop()
    }

    private fun registerFragmentLifecycleCallback() {
        mainActivity = this
        val fr =
            (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment)
        fr.childFragmentManager.registerFragmentLifecycleCallbacks(
            fragmentsLifecycleCallback,
            false
        )
    }

    private fun unregisterFragmentLifecycleCallback() {
        mainActivity = null
        val fr =
            (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment)
        fr.childFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentsLifecycleCallback)
    }

    fun getMainAppBar() = binding.mainAppBar

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun showSnackBar(message: String, icon: Int) {
        val snack = CustomSnackBar.make(binding.root, Snackbar.LENGTH_LONG)
        //snack.anchorView = binding.mainBottomNavView
        snack.setText(message)
        snack.setIcon(icon)
        snack.show()
    }

    fun showLoadingDialog() {
        mLoadingDialog.showLoading()
    }

    fun hideLoadingDialog() {
        mLoadingDialog.hideLoading()
    }

    fun showLoadingView() {
        binding.progressView.isVisible = true
        binding.progressView.playAnimation()
    }

    fun hideLoadingView() {
        binding.progressView.isVisible = false
        binding.progressView.pauseAnimation()
    }

    private fun hideNavBar() {
        binding.navInclude.navBarContainer.isVisible = false
        //binding.fab.hide()
        // binding.mainNavBar.performHide(true)
    }


    private fun showNavBar() {
        // binding.fab.show()
        binding.navInclude.navBarContainer.isVisible = true
    }

    companion object {
        private var mainActivity: MainActivity? = null
        fun getInstance() = mainActivity
    }


}
