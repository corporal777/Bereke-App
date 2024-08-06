package org.wilderkek.bereke.ui.location

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import org.wilderkek.bereke.R
import org.wilderkek.bereke.databinding.FragmentMetroStationsBinding
import org.wilderkek.bereke.ui.base.BaseBottomSheetFragment
import org.wilderkek.bereke.ui.location.items.MetroStationItem
import org.wilderkek.bereke.ui.views.SimpleTextWatcher
import kotlin.reflect.KClass

class MetroStationsFragment(
    private val city: String?,
    private val selectedStations: List<String>
) : BaseBottomSheetFragment<MetroStationsViewModel, FragmentMetroStationsBinding>(true) {

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()


    private val simpleTextWatcher = SimpleTextWatcher().setAfterTextChangeRunnable {
        viewModel.searchStation(it.toString())
    }

    private var onSelectedStations: (list: List<String>) -> Unit = {}
    private var onSelectedStationCoordinates: (coords: Pair<String?, String?>) -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadMetroStations(city, selectedStations)
        isCancelable = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            metroStationsList.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = groupAdapter
            }
            etStation.apply {
                addTextChangedListener(simpleTextWatcher)
            }
            btnAccept.setOnClickListener {
                onSelectedStations.invoke(viewModel.getSelectedStations())
                onSelectedStationCoordinates.invoke(viewModel.getSelectedStationCoords())
                dismiss()
            }
            ivClose.setOnClickListener {
                dismiss()
            }
        }
        observeStations()
        observeSelectedStations()
    }

    private fun observeStations() {
        viewModel.metroStations.observe(viewLifecycleOwner) { list ->
            if (!list.isNullOrEmpty()) {
                groupAdapter.update(list.map {
                    MetroStationItem(
                        it.name,
                        it.color,
                        viewModel.getSelectedStations()
                    ) { station ->
                        viewModel.selectStation(station)
                    }
                })
            }
        }
    }

    private fun observeSelectedStations() {
        viewModel.selectedStations.observe(viewLifecycleOwner) { list ->
            mBinding.apply {
                lnSelectedStations.isVisible = !list.isNullOrEmpty()
                //btnAccept.isEnabled = !list.isNullOrEmpty()
                tvSelectedStations.apply {
                    text = list.joinToString("\n") { it }
                }
            }
        }
    }

    fun setSelectedStationsCallback(block: (list: List<String>) -> Unit): MetroStationsFragment {
        onSelectedStations = block
        return this
    }

    fun setSelectedStationCoordinatesCallback(block: (coords: Pair<String?, String?>) -> Unit): MetroStationsFragment {
        onSelectedStationCoordinates = block
        return this
    }

    override val layoutId: Int = R.layout.fragment_metro_stations
    override fun getViewModelClass(): KClass<MetroStationsViewModel> = MetroStationsViewModel::class
}