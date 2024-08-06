package org.wilderkek.bereke.util.rxtakephoto

import android.graphics.Bitmap
import io.reactivex.subjects.SingleSubject
import org.wilderkek.bereke.util.imagepicker.model.LocalImage

object CropCallbackHelper {

    private val cropRequests = mutableMapOf<String, SingleSubject<Bitmap>>()
    private lateinit var multiplePickSubject: SingleSubject<List<LocalImage>>

    fun createRequest(key: String): SingleSubject<Bitmap> {
        return cropRequests.getOrPut(key) { SingleSubject.create() }
    }

    fun getRequest(key: String): SingleSubject<Bitmap>? {
        return cropRequests[key]
    }

    fun createMultiplePickSubject(): SingleSubject<List<LocalImage>> {
        multiplePickSubject = SingleSubject.create()
        return multiplePickSubject
    }

    fun getMultiplePickSubject() = multiplePickSubject
}