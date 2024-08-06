package org.wilderkek.bereke.util.imagepicker.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.SimpleItemAnimator
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.SingleSubject
import kotlinx.android.synthetic.main.activity_crop.*
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.ActivityGalleryBinding
import org.wilderkek.bereke.util.EXTRA_SETUP
import org.wilderkek.bereke.util.RESULT_NAME
import org.wilderkek.bereke.util.imagepicker.core.ImageLoader
import org.wilderkek.bereke.util.imagepicker.core.ImageLoaderImpl
import org.wilderkek.bereke.util.imagepicker.model.Image
import org.wilderkek.bereke.util.imagepicker.model.LocalImage
import org.wilderkek.bereke.util.imagepicker.model.SetUp
import org.wilderkek.bereke.util.imagepicker.utils.GridSpacingItemDecoration
import org.wilderkek.bereke.util.imagepicker.utils.PermissionUtil
import org.wilderkek.bereke.util.imagepicker.utils.StringUtil
import org.wilderkek.bereke.util.imagepicker.utils.toOptionCompat
import org.wilderkek.bereke.util.performOnBackgroundOutOnMain
import org.wilderkek.bereke.util.rxtakephoto.CropCallbackHelper
import org.wilderkek.bereke.util.rxtakephoto.PermissionNotGrantedException
import org.wilderkek.bereke.util.uriToBitmap

internal class Gallery() : AppCompatActivity(), GalleryListener {

    private lateinit var binding: ActivityGalleryBinding
    private lateinit var pickSubject: SingleSubject<List<LocalImage>>
    private val compositeDisposable = CompositeDisposable()


    private val setUp by lazy {
        intent.getParcelableExtra<SetUp>(
            EXTRA_SETUP
        )
    }

    private var maxSize = MAXIMUM_SELECTION

    private val imageList = mutableListOf<Image>()
    private val selectedList = mutableListOf<Image>()
    private var selectedText = ""
    private var isSingle = false

    private val imageLoader: ImageLoader by lazy {
        ImageLoaderImpl(this)
    }

    private var resultName = RESULT_NAME
    override var isMultipleChecked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pickSubject = CropCallbackHelper.getMultiplePickSubject()

        setUp?.let {
            maxSize = it.max
            resultName = it.name
            isSingle = it.single
            selectedText = it.title ?: return@let
        }
        initToolbar()

        binding.recyclerView.apply {
            adapter = ImagePickerAdapter(imageList, this@Gallery, isSingle)
            addItemDecoration(GridSpacingItemDecoration(3, 1, true))
            setHasFixedSize(true)
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            postponeEnterTransition()
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }

        PermissionUtil.hasGalleryPermissionDenied(this) {
            if (it) {
                PermissionUtil.requestGalleryPermission(
                    this,
                    REQUEST_PERMISSION
                )
            } else {
                loadImages()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION) {
            if (permissions.size == 1 &&
                permissions[0] == Manifest.permission.READ_EXTERNAL_STORAGE &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                loadImages()
            } else {
                pickSubject.onError(PermissionNotGrantedException())
                Log.d("Missing Permission", "Check for permission")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }
        if (requestCode == REQUEST_GALLERY) {
            data?.data?.let { uri ->
                Log.d("uri", uri.toString())
            }
        }
    }

    private fun loadImages() {
//        compositeDisposable += imageLoader.loadAsync()
//            .performOnBackgroundOutOnMain()
//            .subscribeBy {
//                imageList.addAll(it)
//                (binding.recyclerView.adapter as ImagePickerAdapter).notifyDataSetChanged()
//                binding.progressBar.isVisible = false
//            }
        imageLoader.load {
            imageList.addAll(it)
            (binding.recyclerView.adapter as ImagePickerAdapter).notifyDataSetChanged()
            binding.progressBar.isVisible = false
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun initToolbar() {
        binding.apply {
            ivBack.setOnClickListener {
                finish()
            }
            ivDone.setOnClickListener {
                onGalleryDone()
            }
            tvTitle.text = selectedText
        }
    }

    private fun selectedList(): List<Image> {
        return imageList.filter { it.selected }
    }

    private fun selectedImage(image: Image) {
        if (image.selected) {
            imageList.find { it == image }?.selected = false
            selectedList.remove(image)
        } else {
            if (selectedList.size < maxSize) {
                imageList.find { it == image }?.selected = true
                selectedList.add(image)
            } else {
                Log.d(
                    TAG, StringUtil.getStringRes(
                        this,
                        R.string.select_max_toast,
                        maxSize
                    )
                )
            }
        }
        isMultipleChecked = isImageMultipleSelected()
        (binding.recyclerView.adapter as ImagePickerAdapter).updateItem(image)
        toolbarText(selectedList.size)
    }

    private fun toolbarText(count: Int) {
        selectedText = if (isSingle) {
            setUp?.title ?: ""
        } else {
            if (count > 0) {
                "$count/6"
            } else {
                setUp?.title ?: ""
            }
        }
        binding.tvTitle.text = selectedText
    }

    override fun onChecked(image: Image) {
        Log.d(TAG, "item Checked")
        selectedImage(image)
    }

    override fun onShowDetail(view: View, image: Image) {
        Log.d(TAG, "item Clicked")
        startActivity(
            Detail.starterIntent(
                this,
                image
            ),
            toOptionCompat(view, R.id.item_image).toBundle()
        )
    }

    override fun onClick(image: Image) {
        selectedImage(image).also {
            onGalleryDone()
        }
    }

    private fun onGalleryDone() {
        selectedList().let { list ->
            if (list.isNullOrEmpty()){
                pickSubject.onError(java.lang.NullPointerException())
                finish()
            }else {
                receiveImages(list)
            }
        }
    }

    private fun receiveImages(uris: List<Image>) {
        compositeDisposable += Maybe.fromCallable {
            uris.map {
                LocalImage(
                    it.id,
                    it.path,
                    it.path.uriToBitmap(this@Gallery),
                    it.selected
                )
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = {
                    it.printStackTrace()
                    pickSubject.onError(it)
                },
                onSuccess = {
                    pickSubject.onSuccess(it)
                    finish()
                }
            )
    }

    companion object {
        private const val TAG = "ImagePickerView"
        private const val REQUEST_GALLERY = 1011
        private const val REQUEST_PERMISSION = 1013

        private const val MAXIMUM_SELECTION = 30


        fun starterIntent(context: Context, setup: SetUp?): Intent {
            return Intent(context, Gallery::class.java).apply {
                putExtra(EXTRA_SETUP, setup)
            }
        }
    }

    private fun isImageMultipleSelected(): Boolean {
        return imageList.find { it.selected } != null
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

}