package org.wilderkek.bereke.ui.location.items

import android.content.res.ColorStateList
import androidx.core.view.isVisible
import com.xwray.groupie.databinding.BindableItem
import org.wilderkek.bereke.util.parseColor
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.ItemMetroStationBinding

class MetroStationItem(
    val name: String,
    val colors: List<String>,
    val selectedStations : List<String>,
    val onStationClick: (station: String) -> Unit
) : BindableItem<ItemMetroStationBinding>() {

    private var isChecked = selectedStations.contains(name)

    override fun bind(viewBinding: ItemMetroStationBinding, position: Int) {
        viewBinding.apply {
            tvStation.text = name
            ivDotOne.apply {
                isVisible = !colors.isNullOrEmpty() && !colors.firstOrNull().isNullOrEmpty()
                val color = ("#" + colors.firstOrNull()).parseColor()
                if (color != null) imageTintList = ColorStateList.valueOf(color)
            }
            ivDotTwo.apply {
                isVisible = colors.size > 1 && !colors[1].isNullOrEmpty()
                if (colors.size > 1) {
                    val color = ("#" + colors[1]).parseColor()
                    if (color != null) imageTintList = ColorStateList.valueOf(color)
                }
            }

            scBox.isChecked = this@MetroStationItem.isChecked
            clRoot.setOnClickListener {
                isChecked = !isChecked
                scBox.isChecked = isChecked
                onStationClick.invoke(name)
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_metro_station
}