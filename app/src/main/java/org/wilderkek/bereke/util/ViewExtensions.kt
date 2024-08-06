package org.wilderkek.bereke.util

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.ImageRequest
import coil.size.Scale
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import coil.transform.Transformation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import org.wilderkek.bereke.R
import java.io.File


fun TextView.onTextChanged(onTextChanged: (text: CharSequence?) -> Unit): TextWatcher {
    val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) =
            onTextChanged(s)
    }
    addTextChangedListener(watcher)
    return watcher
}

fun TextView.onFocusChanged(onFocusChanged: (hasFocus: Boolean) -> Unit): View.OnFocusChangeListener {
    val watcher = View.OnFocusChangeListener { v, hasFocus ->
        onFocusChanged(hasFocus)
    }
    onFocusChangeListener = watcher
    return watcher
}

fun EditText.initInput(text: String? = "", onTextChanged: (text: CharSequence?) -> Unit) {
    setText(text)
    onTextChanged(onTextChanged)
}

fun EditText.initInputClick(text: String? = "", onClickListener: (text: CharSequence?) -> Unit) {
    setText(text)
    setOnClickListener {
        onClickListener.invoke(this.text)
    }
}


fun ImageView.loadImage(image: Any?, height: Int = 0, width: Int = 0) {
    Glide
        .with(context)
        .load(image)
        .optionalCenterCrop()
        .into(this)
}

fun ImageView.loadNoteImage(image: Any?, height: Int = 0, width: Int = 0) {
    Glide
        .with(context)
        .load(image)
        .let {
            if (image == null) it.optionalCenterCrop()
            else it
        }
        .placeholder(R.drawable.background_image_placeholder)
        .error(R.drawable.item_no_photo)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .transition(DrawableTransitionOptions.withCrossFade(500))
        .into(this)
}


fun ImageView.setUserImage(
    image: Any?, crossfad: Int? = 500,
    placeholder: Int? = R.drawable.background_image_placeholder,
    error: Int? = R.drawable.avatar_empty_square,
    transformations: List<Transformation>? = listOf(RoundedCornersTransformation(25f))
) {
    val resImage: Any = image ?: ""
    when (resImage) {
        is Int -> load(resImage) {
            setParams(crossfad, placeholder, error, transformations)
        }
        is String ->
            if (Patterns.WEB_URL.matcher(resImage).matches())
                load(resImage) {
                    setParams(crossfad, placeholder, error, transformations)
                }
            else
                load(File(resImage)) {
                    setParams(crossfad, placeholder, error, transformations)
                }
        is Drawable ->
            load(resImage) {
                setParams(crossfad, placeholder, error, transformations)
            }
        is Bitmap -> load(resImage) {
            setParams(crossfad, placeholder, error, transformations)
        }
    }
}

fun ImageView.setImage(
    image: Any?, crossfad: Int? = 500,
    placeholder: Int? = R.drawable.background_image_placeholder,
    error: Int? = null,
    transformations: List<Transformation>? = null
) {
    val resImage: Any = image ?: ""
    when (resImage) {
        is Int -> load(resImage) {
            setParams(crossfad, placeholder, error, transformations)
        }
        is String ->
            if (Patterns.WEB_URL.matcher(resImage).matches())
                load(resImage) {
                    setParams(crossfad, placeholder, error, transformations)
                }
            else
                load(File(resImage)) {
                    setParams(crossfad, placeholder, error, transformations)
                }
        is Drawable ->
            load(resImage) {
                setParams(crossfad, placeholder, error, transformations)
            }
        is Bitmap -> load(resImage) {
            setParams(crossfad, placeholder, error, transformations)
        }
    }
}

fun ImageView.setCircleAvatar(
    image: Any?, crossFad: Int? = 500,
    placeholder: Int? = R.drawable.background_image_placeholder,
    error: Int? = R.drawable.empty_avatar
) {
    val resImage: Any = image ?: ""
    when (resImage) {
        is Int -> load(resImage) {
            setParams(crossFad, placeholder, error, listOf(CircleCropTransformation()))
        }
        is String ->
            if (Patterns.WEB_URL.matcher(resImage).matches())
                load(resImage) {
                    setParams(crossFad, placeholder, error, listOf(CircleCropTransformation()))
                }
            else
                load(File(resImage)) {
                    setParams(crossFad, placeholder, error, listOf(CircleCropTransformation()))
                }
        is Drawable ->
            load(resImage) {
                setParams(crossFad, placeholder, error, listOf(CircleCropTransformation()))
            }
        is Bitmap -> load(resImage) {
            setParams(crossFad, placeholder, error, listOf(CircleCropTransformation()))
        }
    }
}


fun ImageRequest.Builder.setParams(
    crossfad: Int? = 500,
    placeholder: Int? = R.drawable.background_image_placeholder,
    error: Int? = R.drawable.background_image_placeholder,
    transformations: List<Transformation>? = null
) {
    if (crossfad != null) crossfade(crossfad)
    if (placeholder != null) placeholder(placeholder)
    if (error != null) error(error)
    if (!transformations.isNullOrEmpty())
        transformations(transformations)
    scale(Scale.FILL)
}

fun RecyclerView.onScrolled(
    onScrolled: (dx: Int, dy: Int) -> Unit,
): RecyclerView.OnScrollListener {
    val listener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) = onScrolled(dx, dy)
    }
    addOnScrollListener(listener)
    return listener
}

fun RecyclerView.onScrollStateChanged(
    onStateChange: (recyclerView: RecyclerView, newState: Int) -> Unit
): RecyclerView.OnScrollListener {
    val listener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) =
            onStateChange(recyclerView, newState)

    }
    addOnScrollListener(listener)
    return listener
}