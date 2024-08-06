package org.wilderkek.bereke.ui.detailNote.fullScreenImages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.wilderkek.bereke.di.data.AppData
import org.wilderkek.bereke.ui.base.BaseViewModel
import org.wilderkek.bereke.ui.detailNote.fullScreenImages.data.FullImagesData

class FullScreenImagesViewModel(private val appData: AppData) : BaseViewModel(appData){


    private val _imagesData = MutableLiveData<FullImagesData>()
    val imagesData : LiveData<FullImagesData> get() = _imagesData


    fun setFullImages(images : FullImagesData){
        images.setFirstImage()
        _imagesData.value = images
    }
}