package org.wilderkek.bereke.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.icu.util.Calendar
import android.net.ConnectivityManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import org.wilderkek.bereke.R
import org.wilderkek.bereke.ui.views.adapters.NoFilterArrayAdapter
import org.wilderkek.bereke.util.imagepicker.ui.ImagePickerView
import kotlin.random.Random

fun generateId() : String {
    return Random.nextInt(0, 90000).toString()
}

fun hasConnection(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
    if (wifiInfo != null && wifiInfo.isConnected) {
        return true
    }
    wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
    if (wifiInfo != null && wifiInfo.isConnected) {
        return true
    }
    wifiInfo = cm.activeNetworkInfo
    return wifiInfo != null && wifiInfo.isConnected
}


fun getCategoriesList(): List<String> {
    val categoriesList = ArrayList<String>()
    categoriesList.add("Работа, Вакансии")
    //categoriesList.add("Транспорт, Перевозки")
    //categoriesList.add("Медицина, Красота")
    //categoriesList.add("Продажа, Покупка")
    categoriesList.add("Квартиры, Гостиницы")
    //categoriesList.add("Обучение, Услуги")
    return categoriesList
}

fun getWorkSchedulesList(): MutableList<String> {
    return mutableListOf<String>(
        "Полный день",
        "Не полный день",
        "Свободный график",
        "Сменный график",
        "Гибкий график",
        "Индивидуальный график",
        "5/2",
        "6/1",
        "Вахта"
    )
}

fun getWorkSubCategoriesList(): MutableList<String> {
    return mutableListOf<String>(
        "Вакансия",
        "Подработка",
        "Стажировка",
        "Ищу работу",
        "Ищу подработку"
    )
}

fun getHouseSubCategoriesList(): MutableList<String> {
    return mutableListOf(
        "Сдаю",
        "Cниму"
    )
}

fun getHouseTypesList(): MutableList<String> {
    return mutableListOf<String>(
        "Квартира",
        "Гостиница",
        "Комната",
        "Койко место",
        "Общежитие"
    )
}

fun AutoCompleteTextView.setAdapterData(list: MutableList<String>) {
    keyListener = null
    setAdapter(
        NoFilterArrayAdapter(
            context,
            R.layout.layout_town,
            R.id.tv_text,
            list
        )
    )
}

fun Fragment.openImagePicker() {
    ImagePickerView.Builder()
        .setup {
            name { RESULT_NAME }
            max { 6 }
            title { "Галлерея" }
            single { false }
        }
        .start(this)
}





