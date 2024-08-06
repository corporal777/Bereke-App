package org.wilderkek.bereke.ui.views

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.core.content.ContextCompat
import org.wilderkek.bereke.util.dp
import org.wilderkek.bereke.R


class FilterButtonView : AppCompatToggleButton {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    init {
        setBackgroundResource(R.drawable.background_filter)
        stateListAnimator = null
        isAllCaps = false
        gravity = Gravity.CENTER
        maxLines = 1
        typeface = Typeface.createFromAsset(context.assets, "noto_sans_bold.ttf")
        setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimensionPixelSize(R.dimen.text_input_hint_size).toFloat())

        minimumHeight = 40.dp
        minimumWidth = 0
        minHeight = 40.dp
        minWidth = 0

        setTextColor(ContextCompat.getColorStateList(context, R.color.text_color_filter))
        setPadding(20.dp, 0, 20.dp, 0)
        includeFontPadding = false

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(5.dp, 0, 5.dp, 0)
        layoutParams = params
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        textOn = text
        textOff = text
    }
}