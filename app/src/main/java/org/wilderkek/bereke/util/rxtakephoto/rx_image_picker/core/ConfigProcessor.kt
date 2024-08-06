package org.wilderkek.bereke.util.rxtakephoto.rx_image_picker.core

import org.wilderkek.bereke.util.rxtakephoto.rx_image_picker.entity.ConfigProvider
import org.wilderkek.bereke.util.rxtakephoto.rx_image_picker.entity.sources.SourcesFrom
import org.wilderkek.bereke.util.rxtakephoto.rx_image_picker.scheduler.IRxImagePickerSchedulers
import org.wilderkek.bereke.util.rxtakephoto.rx_image_picker.ui.ActivityPickerViewController
import io.reactivex.Observable

class ConfigProcessor(private val schedulers: IRxImagePickerSchedulers) {

    fun process(configProvider: ConfigProvider): Observable<*> {
        return Observable.just(0)
            .flatMap {
                if (!configProvider.asFragment) {
                    return@flatMap ActivityPickerViewController.instance.pickImage()
                }
                when (configProvider.sourcesFrom) {
                    SourcesFrom.GALLERY,
                    SourcesFrom.CAMERA -> configProvider.pickerView.pickImage()
                }
            }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
    }
}