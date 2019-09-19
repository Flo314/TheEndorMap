package com.dtc.theendormap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import timber.log.Timber

// requestcode -> jeton qui rapelle
private const val REQUEST_PERMISSION_LOCATION_LAST_LOCATION = 1
private const val REQUEST_PERMISSION_LOCATION_START_UPDATE = 2
private const val REQUEST_CHECK_SETTINGS = 1

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    // récupère l'ensemble des locations
    private val locationCallback = object: LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            // elvis opérator = (?:) sert à voir si c'est null remplace un if == null
            locationResult ?: return
            for (location in locationResult.locations) {
                Timber.d("location update $location")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // récupération d'un client de location qui est fused qui va requêter le wifi, le gps et renvoyer une location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        updateLastLocation()
        createLocationRequest()
    }

    /* appel au retour de l'activity crée ->
    if (exception is ResolvableApiException) {
                exception.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> startLocationUpdate()
        }
    }

    private fun updateLastLocation() {
        Timber.d("updateLocation()")
        // vérifier si on a la permission pour vérifier la location
        if (!checkLocationPermission(REQUEST_PERMISSION_LOCATION_LAST_LOCATION)) {
            return
        }
        // dernière position gps
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            Timber.i("Last Location $location")
        }
    }

    // crée une requête pour récupérer la geolocalisation du gps
    private fun createLocationRequest() {
        // crée la location request
        locationRequest = LocationRequest.create().apply {
            // interval du rafraichissement toutes les 10s
            interval = 10000
            fastestInterval = 5000
            // précision de geolocalisation
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        // vérifier que l'option de location est activé sur les paramètres du device
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)

        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener { locationSettingsResponse ->  
            Timber.i("Location settings satisfied. Init location request here")
            // récupérer la position courante
            startLocationUpdate()
        }

        // cas d'erreur
        task.addOnFailureListener { exception ->
            Timber.e(exception, "Failed to modify Location settings.")
            if (exception is ResolvableApiException) {
                exception.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
            }
        }
    }

    // récupération de la geolocalisation
    private fun startLocationUpdate() {
        Timber.i("startLocationUpdate()")
        // vérif si on a la permission
        if (!checkLocationPermission(REQUEST_PERMISSION_LOCATION_START_UPDATE)) {
            return
        }

        // requête récurrente sur la geolocalisation
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
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
            REQUEST_PERMISSION_LOCATION_LAST_LOCATION -> updateLastLocation()
            REQUEST_PERMISSION_LOCATION_START_UPDATE -> startLocationUpdate()
        }
    }
}
