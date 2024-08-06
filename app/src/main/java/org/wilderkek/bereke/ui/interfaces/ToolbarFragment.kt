package org.wilderkek.bereke.ui.interfaces

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup

interface ToolbarFragment {
    val title: CharSequence
    fun toolbarIconsContainer(viewGroup: ViewGroup)
    fun dispatchBack() : (() -> Unit)? = null
}