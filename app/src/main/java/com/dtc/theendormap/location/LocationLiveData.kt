package com.dtc.theendormap.location

import android.content.Context
import androidx.lifecycle.LiveData
import com.google.android.gms.location.*
import timber.log.Timber

// je récupère la position je la remonte sinon je remonte l'erreur
class LocationLiveData(context: Context) : LiveData<LocationData>() {
    private val appContext = context.applicationContext
    // récupération d'un client de location qui est fused qui va requêter le wifi, le gps et renvoyer une location
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext)

    // crée la location request
    private val locationRequest = LocationRequest.create().apply {
        // interval du rafraichissement toutes les 10s
        interval = 10000
        fastestInterval = 5000
        // précision de geolocalisation
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val locationCallback = object: LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            // elvis opérator = (?:) sert à voir si c'est null remplace un if == null
            locationResult ?: return
            for (location in locationResult.locations) {
                value = LocationData(location = location)
            }
        }
    }

    // utiliser pour le premier qui s'abonne pour envoyer la première location
    private var firstSubscriber = true

    // on s'abonne au livedata
    override fun onActive() {
        super.onActive()
        if (firstSubscriber){
            requestLastLocation()
            requestLocation()
            firstSubscriber = false
        }
    }

    // se désabonner
    override fun onInactive() {
        super.onInactive()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        firstSubscriber = true
    }

    fun startRequestLocation() {

        // vérifier que l'option de location est activé sur les paramètres du device
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(appContext)

        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener { locationSettingsResponse ->
            Timber.i("Location settings satisfied. Init location request here")
            // récupérer la position courante
            requestLocation()
        }

        // cas d'erreur
        task.addOnFailureListener { exception ->
            Timber.e(exception, "Failed to modify Location settings.")
            value = LocationData(exception = exception)
        }
    }

    private fun requestLocation() {
        // requête récurrente sur la geolocalisation
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } catch (e: SecurityException) {
            value = LocationData(exception = e)
        }
    }

    private fun requestLastLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                value = LocationData(location = location)
            }

            fusedLocationClient.lastLocation.addOnFailureListener { exception ->
                value = LocationData(exception = exception)
            }
        } catch (e: SecurityException) {
            value = LocationData(exception = e)
        }

    }
}