package org.wilderkek.bereke.ui.detailNote.items

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.databinding.GroupieViewHolder
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.runtime.ui_view.ViewProvider
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.ItemNoteDetailAddressBinding
import org.wilderkek.bereke.util.formatToDayMonth

class DetailAddressItem(
    private val city: String?,
    private val region: String?,
    private val metro: List<String>?,
    private val date: String,
) : BindableItem<ItemNoteDetailAddressBinding>() {

    private var isMapInitialized = false

    override fun bind(viewBinding: ItemNoteDetailAddressBinding, position: Int) {
        viewBinding.apply {
            tvCity.text =
                if (city.isNullOrEmpty()) root.context.getString(R.string.not_chosen) else city
            tvRegion.text =
                if (region.isNullOrEmpty()) root.context.getString(R.string.not_chosen) else region

            if (metro.isNullOrEmpty()) tvMetro.text = root.context.getString(R.string.not_chosen)
            else tvMetro.text = metro.joinToString("\n") { it }

            tvDate.text = date.formatToDayMonth()

            mapView.apply {
                if (!isMapInitialized){
                    isMapInitialized = true
                    onStart()
                    map.move(
                        CameraPosition(Point(55.687147, 37.5723), 15.54f, 15.54f, 0.0f),
                        Animation(Animation.Type.SMOOTH, 0f),
                        null
                    )
                    map.isRotateGesturesEnabled = false
                    map.isScrollGesturesEnabled = false
                    map.isLiteModeEnabled = false
                    map.isZoomGesturesEnabled = false
                    map.isTappableAreaRenderingEnabled = false
                    map.isTiltGesturesEnabled = false
                }
            }
        }
    }

    override fun unbind(viewHolder: GroupieViewHolder<ItemNoteDetailAddressBinding>) {
        viewHolder.binding.mapView.onStop()
        super.unbind(viewHolder)
    }

    override fun hasSameContentAs(other: Item<*>?): Boolean {
        if (other !is DetailAddressItem) return false
        if (city != other.city) return false
        if (region != other.region) return false
        if (metro != other.metro) return false
        if (date != other.date) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_note_detail_address

}