package org.wilderkek.bereke.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import org.wilderkek.bereke.util.dp
import org.wilderkek.bereke.R

class ActionLikeView : LinearLayout {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    private val likeView = AppCompatImageView(context)
    var onLikeClickListener: OnLikeClickListener? = null
    var onDisLikeClickListener: OnLikeClickListener? = null
    var onEditClickListener: OnLikeClickListener? = null

    init {
        background = ContextCompat.getDrawable(context, R.drawable.circle_alpha_background)
        foreground = ContextCompat.getDrawable(context, R.drawable.custom_btn_toolbar_selectable)
        addView(likeView.apply {
            layoutParams = LinearLayout.LayoutParams(20.dp, 20.dp)
            setPadding(0, 2.dp, 0, 0)
            setImageResource(R.drawable.ic_like_empty)
            imageTintList = ContextCompat.getColorStateList(context, R.color.app_main_color)
        })
    }


    fun setActionLike(isLiked : Boolean, isOwner : Boolean){
        if (isOwner) this.isInvisible = true
        else {
            likeView.apply {
                if (isLiked){
                    imageTintList = ContextCompat.getColorStateList(context, R.color.liked_heart_text_color)
                    setImageResource(R.drawable.ic_like_fill)
                    this@ActionLikeView.setOnClickListener {
                        onDisLikeClickListener?.invoke()
                    }
                } else {
                    imageTintList = ContextCompat.getColorStateList(context, R.color.app_main_color)
                    setImageResource(R.drawable.ic_like_empty)
                    this@ActionLikeView.setOnClickListener {
                        onLikeClickListener?.invoke()
                    }
                }
            }
        }
    }

    fun setActionEdit() {
        likeView.apply {
            imageTintList = ContextCompat.getColorStateList(context, R.color.app_main_color)
            setImageResource(R.drawable.ic_edit_note)
            this@ActionLikeView.setOnClickListener {
                onEditClickListener?.invoke()
            }
        }
    }

    fun setActionNone() {
        this.isInvisible = true
    }
}

enum class ActionType() {
    LIKE, EDIT, NONE
}

typealias OnLikeClickListener = () -> Unit