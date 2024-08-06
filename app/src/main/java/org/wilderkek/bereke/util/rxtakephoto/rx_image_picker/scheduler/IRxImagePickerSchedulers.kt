package org.wilderkek.bereke.util.rxtakephoto.rx_image_picker.scheduler

import io.reactivex.Scheduler

interface IRxImagePickerSchedulers {

    fun ui(): Scheduler

    fun io(): Scheduler
}