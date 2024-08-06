package org.wilderkek.bereke.ui.createNote.items

import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.xwray.groupie.databinding.BindableItem
import org.wilderkek.bereke.ui.location.MetroStationsFragment
import org.wilderkek.bereke.util.initInput
import org.wilderkek.bereke.util.initInputClick
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.ItemAddressDataBinding
import org.wilderkek.bereke.model.response.NoteAddressModel

class AddressDataItem(
    private val context: FragmentActivity,
    city: String?,
    region: String?,
    stations: List<String>?
) : BindableItem<ItemAddressDataBinding>() {

    private var selectedCity = city ?: ""
    private var selectedRegion = region ?: ""
    private var selectedStations = arrayListOf<String>().apply {
        if (!stations.isNullOrEmpty()) addAll(stations)
    }
    private var stationLat = ""
    private var stationLon = ""

    private lateinit var mBinding: ItemAddressDataBinding

    override fun bind(viewBinding: ItemAddressDataBinding, position: Int) {
        mBinding = viewBinding
        viewBinding.apply {
            etCity.apply {
                tvCityRequired.isVisible = selectedCity.isNullOrBlank()
                setTextWithoutSearch(selectedCity)
                onDataChangedListener = {
                    tvCityRequired.apply {
                        if (!it.isNullOrBlank()) changeRequiredTextColor(true)
                        isVisible = it.isNullOrBlank()
                    }
                    selectedCity = it
                }
                onDataSelectedListener = {
                    selectedCity = it
                }
            }
            etRegion.initInput(selectedRegion) {
                selectedRegion = it.toString()
            }
            etStation.initInputClick(getStationsFromList()) {
                showMetroStations(selectedCity, selectedStations)
            }
        }
    }


    fun isAddressDataValid(): Boolean {
        var valid = true
        if (selectedCity.isNullOrBlank()) {
            valid = false
            mBinding.tvCityRequired.changeRequiredTextColor(false)
        }
        return valid
    }

    fun getAddressData(): NoteAddressModel {
        return NoteAddressModel(
            selectedRegion,
            selectedCity,
            null,
            null,
            selectedStations,
            stationLat,
            stationLon
        )
    }

    private fun showMetroStations(city: String?, list: List<String>) {
        val stationsFragment = MetroStationsFragment(city, list)
        stationsFragment.show(context.supportFragmentManager, "metro_stations")
        stationsFragment.setSelectedStationsCallback {
            selectedStations.clear()
            selectedStations.addAll(it)
            mBinding.etStation.setText(getStationsFromList())
        }
        stationsFragment.setSelectedStationCoordinatesCallback {
            stationLat = it.first ?: ""
            stationLon = it.second ?: ""
        }
    }

    private fun getStationsFromList(): String? {
        return if (selectedStations.isNullOrEmpty()) null
        else selectedStations.joinToString(", ") { s -> s }
    }

    private fun TextView.changeRequiredTextColor(isCorrect: Boolean) {
        if (!isCorrect) setTextColor(context.getColor(R.color.error_text_color))
        else setTextColor(context.getColor(R.color.app_main_color))
    }

    override fun getLayout(): Int = R.layout.item_address_data
}