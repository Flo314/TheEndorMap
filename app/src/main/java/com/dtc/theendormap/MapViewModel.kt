package com.dtc.theendormap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

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
}