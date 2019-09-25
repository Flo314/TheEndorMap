package com.dtc.theendormap

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dtc.theendormap.map.MapUiState
import com.dtc.theendormap.map.MapViewModel
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class MapViewModelTest {

    // définit une règle d'exécution qui s'applique au livedata
    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // test sur la séquence d'état
    @Test
    fun `load Poi triggers loading success`() {
        val viewModel = MapViewModel()
        val observer = viewModel.getUiState().testObserver()
        viewModel.loadPois(0.0, 0.0)

        assertEquals(MapUiState.Loading, observer.observedValues[0])
    }

    @Test
    fun `load POIs with invalid coordinates error`() {
        val viewModel = MapViewModel()
        val observer = viewModel.getUiState().testObserver()
        viewModel.loadPois(-91.0, -181.0)

        assertTrue(observer.observedValues[0] is MapUiState.Error)
    }
}