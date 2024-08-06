package org.wilderkek.bereke.ui.createNote.items

import androidx.databinding.ViewDataBinding
import com.xwray.groupie.databinding.BindableItem

abstract class AdditionalDataItem<T : ViewDataBinding> : BindableItem<T>() {


    abstract fun getDataToSave() : MutableMap<String, String>
    abstract fun getDraftToSave() : String
    abstract fun isAdditionalDataValid() : Boolean
}