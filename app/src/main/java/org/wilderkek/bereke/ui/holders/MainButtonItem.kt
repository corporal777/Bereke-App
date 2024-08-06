package org.wilderkek.bereke.ui.holders

import androidx.core.view.isVisible
import com.xwray.groupie.databinding.BindableItem
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.ItemMainButtonBinding

class MainButtonItem(
    private val withDivider: Boolean = true,
    private val buttonText: String,
   private val onButtonClick: () -> Unit
) : BindableItem<ItemMainButtonBinding>() {



    override fun bind(viewBinding: ItemMainButtonBinding, position: Int) {
        viewBinding.apply {
            divider.isVisible = withDivider
            btnSave.apply {
                text = buttonText
                setOnClickListener {
                    onButtonClick.invoke()
                }
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_main_button
}