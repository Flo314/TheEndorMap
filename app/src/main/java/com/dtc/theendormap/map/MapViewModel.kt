package com.dtc.theendormap.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dtc.theendormap.poi.Poi
import com.dtc.theendormap.poi.generatePois
import com.dtc.theendormap.poi.generateUserPoi
import timber.log.Timber

// décrit l'ensemble des états que le système pourra prendre
// la sealed class oblige à déclarer tous les états dans la class MapUiState
sealed class MapUiState {
    object Loading : MapUiState()
    data class Error(val errorMessage: String) : MapUiState()
    data class PoiReady(
        val usePoi: Poi? = null,
        val pois: List<Poi>? = null
    ) : MapUiState()
}

class MapViewModel : ViewModel() {
    private val uiState = MutableLiveData<MapUiState>()
    fun getUiState() : LiveData<MapUiState> = uiState

    // alimente le livedata
    fun loadPois(latitude: Double, longitude: Double) {
        Timber.i("loadPois()")
        // est ce que les coordonnées sont valides
        if (!(latitude in -90.0..90.0 && longitude in -180.0..180.0)) {
            uiState.value =
                MapUiState.Error("Invalid coordinates: lat=$latitude, long=$longitude")
            return
        }

        uiState.value = MapUiState.Loading
        uiState.value = MapUiState.PoiReady(
            usePoi = generateUserPoi(latitude, longitude),
            pois = generatePois(latitude, longitude)
        )
    }
}