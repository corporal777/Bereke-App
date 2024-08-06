package org.wilderkek.bereke.util.rxtakephoto.rx_image_picker.scheduler

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import org.wilderkek.bereke.util.rxtakephoto.rx_image_picker.scheduler.IRxImagePickerSchedulers

class RxImagePickerTestSchedulers : IRxImagePickerSchedulers {

    override fun ui(): Scheduler {
        return Schedulers.io()
    }

    override fun io(): Scheduler {
        return Schedulers.io()
    }
}