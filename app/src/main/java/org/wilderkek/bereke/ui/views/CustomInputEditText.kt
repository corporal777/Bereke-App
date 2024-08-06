package org.wilderkek.bereke.ui.views

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText
import org.wilderkek.bereke.R

class CustomInputEditText : TextInputEditText {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        obtainAttributes(attrs)
    }

    init {
    }

    private fun obtainAttributes(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomInputEditText)
        val hint = a.getText(R.styleable.CustomInputEditText_customHint)
        a.recycle()
        setCustomHint(hint)
    }


    private fun setCustomHint(hint: CharSequence) {
        val spannableHint = SpannableString(hint)
        val font = Typeface.createFromAsset(context.assets, "noto_sans_regular.ttf")
        spannableHint.setSpan(
            CustomTypefaceSpan("", font),
            0,
            spannableHint.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val textSize = resources.getDimensionPixelSize(R.dimen.text_input_hint_size)
        spannableHint.setSpan(
            AbsoluteSizeSpan(textSize),
            0,
            spannableHint.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        super.setHint(spannableHint)
    }
}