package org.wilderkek.bereke.ui.detailNote.fullScreenImages.items

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.xwray.groupie.databinding.BindableItem
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.ItemNoteDetailFullImageBinding

class FullScreenImageItem(private val image: String) :
    BindableItem<ItemNoteDetailFullImageBinding>() {


    @SuppressLint("ClickableViewAccessibility")
    override fun bind(viewBinding: ItemNoteDetailFullImageBinding, position: Int) {
        viewBinding.apply {
            noteDetailFullImage.apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setOnTouchListener { view, event ->
                    var result = true
                    if (event.pointerCount >= 2 || view.canScrollHorizontally(1) && canScrollHorizontally(
                            -1
                        )
                    ) {
                        result = when (event.action) {
                            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                                parent.requestDisallowInterceptTouchEvent(true)
                                false
                            }
                            MotionEvent.ACTION_UP -> {
                                parent.requestDisallowInterceptTouchEvent(false)
                                true
                            }
                            else -> true
                        }
                    }
                    result
                }


                Glide
                    .with(context)
                    .load(image)
                    .placeholder(R.drawable.background_image_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .into(this)
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_note_detail_full_image
}