package com.dtc.theendormap

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dtc.theendormap.map.MapUiState
import com.dtc.theendormap.map.MapViewModel
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class MapViewModelTest {

    // définit une règle d'exécution qui s'applique au livedata
    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun loadPoiTriggersLoading() {
        val viewModel = MapViewModel()
        val observer = viewModel.getUiState().testObserver()
        viewModel.loadPois(0.0, 0.0)

        Assert.assertEquals(MapUiState.Loading, observer.observedValues[0])
    }
}