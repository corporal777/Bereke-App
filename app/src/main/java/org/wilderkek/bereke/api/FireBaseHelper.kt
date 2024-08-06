package org.wilderkek.bereke.api

import android.content.Context
import android.net.Uri
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.Maybe
import org.wilderkek.bereke.util.*
import java.util.*

object FireBaseHelper {

    private const val workCategory = "Работа, Вакансии"
    private const val transportCategory = "Транспорт, Перевозки"
    private const val healthCategory = "Медицина, Красота"
    private const val buySellCategory = "Продажа, Покупка"
    private const val flatsCategory = "Квартиры, Гостиницы"
    private const val studyCategory = "Обучение, Услуги"
    private val imagesUrlList = ArrayList<String>()
    private lateinit var mRefAds: DatabaseReference


    fun getFcmToken() : Maybe<String> {
        return Maybe.create { emitter ->
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token ->
                    emitter.onSuccess(token)
                }
                .addOnFailureListener { e -> emitter.onError(e) }
        }
    }
}