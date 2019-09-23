package com.dtc.theendormap.geofence

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.dtc.theendormap.poi.Poi
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import timber.log.Timber

private const val GEOFENCE_ID_MORDOR = "Mordor"

// gère le geofencing
class GeofenceManager(context: Context) {

    private val appContext = context.applicationContext

    private val geofencingClient = LocationServices.getGeofencingClient(appContext)
    private val geofenceList = mutableListOf<Geofence>()

    fun createGeofence(poi: Poi, radiusMeter: Float, requestId: String){
        Timber.d("Creating geofence at coordinates ${poi.latitude}, ${poi.longitude}")

        // builder de geofence
        geofenceList.add(
            Geofence.Builder()
                // configuration du geofence
                .setRequestId(requestId)
                .setExpirationDuration(10 *60 *1000)// 10min en milliseconde
                // position et taille
                .setCircularRegion(
                    poi.latitude,
                    poi.longitude,
                    radiusMeter
                )
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()
        )

        // ajouter ce geofence au geofenceClient
        val task = geofencingClient.addGeofences(getGeofencingRequest(), geofencePendingIntent)
        task.addOnSuccessListener {
            Timber.i("Geofence added")
        }
        task.addOnFailureListener { exception ->
            Timber.e(exception, "Cannot add geofence")
        }
    }

    private fun getGeofencingRequest(): GeofencingRequest? {
        return GeofencingRequest.Builder()
            // définit le comportement du déclanchement du geofencing lorqu'il crée
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofenceList)
            .build()
    }

    // "by lazy" -> permet d'initialiser PendingIntent seulement à la première fois qu'on y fait référence (qu'on l'apelle)
    // qu'est ce qu'on souhaite déclancher au moment ou on entre et sort du geofencing
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent()
        PendingIntent.getService(
            appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}