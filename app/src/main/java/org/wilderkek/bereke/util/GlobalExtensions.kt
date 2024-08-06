package org.wilderkek.bereke.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.doOnDetach
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.wilderkek.bereke.R
import java.io.ByteArrayOutputStream

fun Fragment.onBackPressedCallback(
    enabled: Boolean,
    onBackClick: () -> Unit
) {
    requireActivity().onBackPressedDispatcher.addCallback(
        viewLifecycleOwner,
        object : OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                onBackClick.invoke()
            }
        })
}

fun ViewPager2.onPageSelected(
    onPageChanged: (
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
    ) -> Unit
): ViewPager2.OnPageChangeCallback {
    val listener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) = onPageChanged(position, positionOffset, positionOffsetPixels)
    }
    registerOnPageChangeCallback(listener)
    return listener
}

fun ViewPager.onPageSelected(onPageSelected: (position: Int) -> Unit): ViewPager.OnPageChangeListener {
    val pageChangeListener = object : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) = onPageSelected(position)
    }
    addOnPageChangeListener(pageChangeListener)
    return pageChangeListener
}

fun getPagerAdapter(
    fragmentManager: FragmentManager,
    items: List<Fragment>
): FragmentStatePagerAdapter {
    return object : FragmentStatePagerAdapter(
        fragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {
        override fun getCount(): Int = items.size
        override fun getItem(position: Int): Fragment = items[position]
    }
}

fun onPageChanged(onPageChanged: (position: Int) -> Unit): ViewPager.SimpleOnPageChangeListener {
    val pageChangeListener = object : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            onPageChanged(position)
        }
    }
    return pageChangeListener
}

fun getFragmentLifecycleCallback(
    onViewCreated: (f: Fragment) -> Unit,
    onViewDestroyed: (f: Fragment) -> Unit,
    onFragmentStarted: (f: Fragment) -> Unit,
    onFragmentStopped: (f: Fragment) -> Unit
): FragmentManager.FragmentLifecycleCallbacks {
    val callback = object : FragmentManager.FragmentLifecycleCallbacks() {

        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
            onViewDestroyed(f)
        }

        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            onViewCreated(f)
        }

        override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
            super.onFragmentStarted(fm, f)
            onFragmentStarted(f)
        }

        override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
            super.onFragmentStopped(fm, f)
            onFragmentStopped(f)
        }


    }
    return callback
}


typealias OnSystemInsetsChangedListener = (statusBarSize: Int, navigationBarSize: Int) -> Unit

object InsetUtil {

    fun doEdgeDisplay(view: View, listener: OnSystemInsetsChangedListener) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            ViewCompat.onApplyWindowInsets(
                view,
                insets.replaceSystemWindowInsets(0, 0, 0, 0)
            )
        }
    }

    fun removeSystemInsets(view: View, listener: OnSystemInsetsChangedListener) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            ViewCompat.onApplyWindowInsets(
                view,
                insets.replaceSystemWindowInsets(0, 0, 0, insets.systemWindowInsetBottom)
            )
        }
    }

    fun returnSystemInsets(view: View, listener: OnSystemInsetsChangedListener) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            ViewCompat.onApplyWindowInsets(
                view,
                insets.replaceSystemWindowInsets(
                    0,
                    insets.systemWindowInsetTop,
                    0,
                    insets.systemWindowInsetBottom,
                )
            )
        }
    }

}

fun Activity.setWindowTransparency(listener: OnSystemInsetsChangedListener = { _, _ -> }) {
    InsetUtil.removeSystemInsets(window.decorView, listener)
//    window.navigationBarColor = Color.TRANSPARENT
    window.statusBarColor = Color.TRANSPARENT
}

fun Activity.cancelWindowTransparency(listener: OnSystemInsetsChangedListener = { _, _ -> }) {
    window.statusBarColor = window.context.getColor(R.color.app_main_color)
    InsetUtil.returnSystemInsets(window.decorView, listener)
}

fun Activity.doEdgeWindow(listener: OnSystemInsetsChangedListener = { _, _ -> }) {
    //    window.navigationBarColor = Color.TRANSPARENT
    window.statusBarColor = Color.TRANSPARENT
    InsetUtil.doEdgeDisplay(window.decorView, listener)
}

fun Bitmap.toBodyPart(
    name: String,
    fileName: String,
    compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG
): MultipartBody.Part {
    return let { bitmap ->
        val byteArray = ByteArrayOutputStream().let {
            bitmap.compress(compressFormat, 100, it)
            it.toByteArray()
        }

        val body = byteArray.toRequestBody("application/octet-stream".toMediaTypeOrNull())
        MultipartBody.Part.createFormData(name, fileName, body)
    }
}

fun List<Bitmap>.toBodyPart(
    name: String,
    fileName: String,
    compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG
): MultipartBody {
    return MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .apply {
            forEachIndexed { index, bitmap ->
                val byteArray = ByteArrayOutputStream().let {
                    bitmap.compress(compressFormat, 100, it)
                    it.toByteArray()
                }

                val body = byteArray.toRequestBody("application/octet-stream".toMediaTypeOrNull())
                addFormDataPart(name + index.toString(), fileName + index.toString(), body)
            }
        }.build()
}


fun String?.parseColor(): Int? {
    if (this == null || isEmpty()) return null

    return try {
        Color.parseColor(this)
    } catch (e: IllegalArgumentException) {
        null
    }
}

fun showPopupMenu(view: View, list: List<PowerMenuItem>, block: (item: PowerMenuItem) -> Unit) {
//    val wrapper: Context = ContextThemeWrapper(requireContext(), R.style.CustomPopupMenuStyle)
//    val popupMenu = PopupMenu(requireContext(), view, R.style.CustomPopupMenuStyle)

//        popupMenu.apply {
//            setForceShowIcon(true)
//            setOnMenuItemClickListener { item ->
//                when (item?.itemId) {
//
//                    R.id.change_name -> {
//                        true
//                    }
//                    else -> false
//                }
//            }
//            inflate(R.menu.account_menu)
//            show()
//        }

    val popup = PowerMenu.Builder(view.context)
        .addItemList(list)
        .setAutoDismiss(true)
        .setShowBackground(false)
        .setAnimation(MenuAnimation.DROP_DOWN)
        .setMenuRadius(15f)
        .setMenuShadow(10f)
        .setTextColor(ContextCompat.getColor(view.context, R.color.black))
        .setTextSize(16)
        .setTextGravity(Gravity.START)
        .setTextTypeface(Typeface.createFromAsset(view.context.assets, "noto_sans_medium.ttf"))
        .setWidth(670)
        .setIconSize(25)

        .setMenuColor(Color.WHITE)
        .setOnMenuItemClickListener { position, item ->
            block.invoke(item)
        }
        .build()

    popup.showAsDropDown(view)
    view.doOnDetach {
        popup.dismiss()
    }
}

fun Context.isConnectedToNetwork(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    return connectivityManager?.activeNetworkInfo?.isConnected ?: false
}


inline fun <reified T> JsonElement?.fromJson(deserializer: JsonDeserializer<T>? = null): T? {
    if (this == null) return null
    return GsonBuilder()
        .apply {
            if (deserializer != null) registerTypeAdapter(T::class.java, deserializer)
        }
        .create()
        .fromJson(this, T::class.java)
}

fun Uri.uriToBitmap(context: Context): Bitmap {
    return Glide.with(context).asBitmap().load(this).submit().get()
}

fun String.urlToBitmap(context: Context): Bitmap {
    return Glide.with(context).asBitmap().load(this).submit().get()
}

fun List<Uri>.uriToBitmap(context: Context): List<Bitmap> {
    return this.map {
        Glide.with(context).asBitmap()
            .load(it).submit().get()
    }
}

fun List<String>.urlToBitmap(context: Context): List<Bitmap> {
    return this.map {
        Glide.with(context).asBitmap()
            .load(it).submit().get()
    }
}





