package org.wilderkek.bereke.util.rxtakephoto

import android.graphics.Bitmap
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.isseiaoki.simplecropview.CropImageView
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.Single
import org.wilderkek.bereke.ui.main.MainActivity
import org.wilderkek.bereke.util.RESULT_NAME
import org.wilderkek.bereke.util.imagepicker.model.LocalImage
import org.wilderkek.bereke.util.imagepicker.ui.ImagePickerView
import org.wilderkek.bereke.util.rxtakephoto.CropActivity.Companion.CROP_MODE_DEFAULT
import org.wilderkek.bereke.util.rxtakephoto.rx_image_picker.core.RxImagePicker
import org.wilderkek.bereke.util.rxtakephoto.rx_image_picker.entity.Result
import org.wilderkek.bereke.util.rxtakephoto.CropActivity
import org.wilderkek.bereke.util.rxtakephoto.CropCallbackHelper
import org.wilderkek.bereke.util.rxtakephoto.ResultRotation
import org.wilderkek.bereke.util.urlToBitmap
import java.io.IOException
import kotlin.random.Random


class RxTakePhoto(private val context: MainActivity) {

    private val rxPermissions = RxPermissions(context)
    private val rxImagePicker = RxImagePicker.create()

    fun takeCameraImage(): Observable<ResultRotation> {
//        return rxPermissions.request(
//            Manifest.permission.CAMERA,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//        ).flatMap {
//            if (it) rxImagePicker.openCamera(context).findRotation()
//            else Observable.error<ResultRotation>(PermissionNotGrantedException())
//        }
        return rxImagePicker.openCamera(context).findRotation()
    }

    fun takeGalleryImage(): Observable<ResultRotation> {
//        return rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
//            .flatMap {
//                if (it) rxImagePicker.openGallery(context).findRotation()
//                else Observable.error<ResultRotation>(PermissionNotGrantedException())
//            }
        return rxImagePicker.openGallery(context).findRotation()
    }

    fun takeMultipleImages(): Single<List<LocalImage>> {
        ImagePickerView.Builder()
            .setup {
                name { RESULT_NAME }
                max { 6 }
                title { "Галлерея" }
                single { false }
            }
            .start(context)
        return CropCallbackHelper.createMultiplePickSubject()
    }

    fun toLocalImage(list: List<String>?): List<LocalImage> {
        if (list.isNullOrEmpty()) return emptyList()
        else return list.mapIndexed { index, s ->
            LocalImage(Random.nextInt(0, 900) , Uri.parse(s), s.urlToBitmap(context), true)
        }
    }

    fun crop(
        resultRotation: ResultRotation,
        outputMaxWidth: Int = 0,
        outputMaxHeight: Int = 0,
        outputQuality: Int = 0,
        cropMode: CropImageView.CropMode = CROP_MODE_DEFAULT
    ): Single<Bitmap> {
        return crop(
            resultRotation.uri,
            resultRotation.rotation,
            outputMaxWidth,
            outputMaxHeight,
            outputQuality,
            cropMode
        )
    }

    fun crop(
        uri: Uri,
        rotation: Int = 0,
        outputMaxWidth: Int = 0,
        outputMaxHeight: Int = 0,
        outputQuality: Int = 0,
        cropMode: CropImageView.CropMode = CROP_MODE_DEFAULT
    ): Single<Bitmap> {
        context.startActivity(
            CropActivity.getStartIntent(
                context,
                uri,
                rotation,
                outputMaxWidth,
                outputMaxHeight,
                outputQuality,
                cropMode
            )
        )
        return CropCallbackHelper.createRequest(uri.toString())
    }

    private fun Observable<Result>.findRotation(): Observable<ResultRotation> {
        return this.map {
            val uri = it.uri
            ResultRotation(uri, findImageRotation(uri))
        }
    }

    private fun findImageRotation(uri: Uri): Int {
        return try {
            with(context.contentResolver.openInputStream(uri)!!) {
                val exifInterface = ExifInterface(this)

                var rotation = 0
                val orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotation = 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotation = 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotation = 270
                }

                rotation
            }
        } catch (ignored: IOException) {
            0
        }
    }
}