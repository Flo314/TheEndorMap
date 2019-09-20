package com.dtc.theendormap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

// requestcode -> jeton qui rapelle
private const val REQUEST_PERMISSION_LOCATION_START_UPDATE = 2
private const val REQUEST_CHECK_SETTINGS = 1

// s'abonner au livedata et en fonction de ce quelle reçoit une position ou une erreur elle fait le traitement qu'il faut
class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var viewModel: MapViewModel
    private lateinit var map: GoogleMap
    private lateinit var locationLiveData: LocationLiveData

    private var firstLocation = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // options de la carte
        val mapOptions = GoogleMapOptions()
            .mapType(GoogleMap.MAP_TYPE_NORMAL)
            .zoomControlsEnabled(true)
            .zoomGesturesEnabled(true)

        // instancié un fragment pour l'ajouter à l'activité et remplir le framelayout d'activity.main.xml
        val mapFragment = SupportMapFragment.newInstance(mapOptions)
        // s'abonner aux event de google maps
        mapFragment.getMapAsync(this)
        supportFragmentManager.beginTransaction()
            .replace(R.id.content, mapFragment)
            .commit()

        locationLiveData = LocationLiveData(this)
        locationLiveData.observe(this, Observer { handleLocationData(it!!) })

        // donne un viewModel on ne s'occupe pas de son cycle de vie
        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
        // s'abonner
        viewModel.getUiState().observe(this, Observer { updateUiState(it!!) })
    }

    private fun updateUiState(state: MapUiState) {
        // logger le state à chaque fois qu'il arrive
        Timber.i("$state")
        // quand on fait un return de when on peut générer tous les states
        return when (state) {
            MapUiState.Loading -> loadingProgressBar.show()
            is MapUiState.Error -> {
                loadingProgressBar.hide()
                Toast.makeText(this, "Error: ${state.errorMessage}", Toast.LENGTH_SHORT).show()
            }
            is MapUiState.PoiReady -> {
                loadingProgressBar.hide()

                state.usePoi?.let {

                }

                state.pois?.let {

                }
                return
            }
        }
    }

    /* appel au retour de l'activity crée ->
    if (exception is ResolvableApiException) {
                exception.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> locationLiveData.startRequestLocation()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        // configurer la carte et chargé le fichier raw qui contient le style de la map
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.maps_style))
    }

    private fun handleLocationData(locationData: LocationData) {
        if (handleLocationException(locationData.exception)) {
            return
        }
        // ce bloc sera exécuté que si une location est définit
        locationData.location?.let {
            if (firstLocation) {
                firstLocation = false
                viewModel.loadPois(it.latitude, it.longitude)
            }
        }
//        Timber.i("Last location from LIVEDATA ${locationData.location}")
    }

    // exception remonté par requestLocation de live data
    private fun handleLocationException(exception: Exception?): Boolean {
        exception ?: return false

        Timber.e(exception, "handleLocationException")
        when (exception) {
            is SecurityException -> checkLocationPermission((REQUEST_PERMISSION_LOCATION_START_UPDATE))
            is ResolvableApiException -> exception.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
        }
        return true
    }

    // vérifie si on a la permission
    private fun checkLocationPermission(requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // demande de permission (dialog)
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                requestCode
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
       // si pas reçu de résultats
        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            return
        }

        when (requestCode) {
            REQUEST_PERMISSION_LOCATION_START_UPDATE -> locationLiveData.startRequestLocation()
        }
    }
}
