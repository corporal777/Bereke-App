package org.wilderkek.bereke.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import org.wilderkek.bereke.util.dp
import org.wilderkek.bereke.R

class ToolbarIconView : AppCompatImageView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    init {
        background = ContextCompat.getDrawable(context, R.drawable.custom_btn_toolbar_selectable)
        layoutParams = LinearLayout.LayoutParams(35.dp, 35.dp)
        initPadding(5.dp, 5.dp, 5.dp, 5.dp)
        imageTintList = ContextCompat.getColorStateList(context, R.color.white)
    }

    fun initPadding(top: Int, bottom: Int, left: Int, right: Int) {
        setPadding(left, top, right, bottom)
    }

    fun setImageAsIcon(image: Int) {
        setImageResource(image)
    }

    fun setAlphaVision(enabled: Boolean) {
        this.apply {
            isEnabled = enabled
            alpha = if (enabled) 1f
            else 0.6f
        }
    }
}