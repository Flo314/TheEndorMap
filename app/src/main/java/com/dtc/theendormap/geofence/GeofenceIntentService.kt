package com.dtc.theendormap.geofence

import android.app.IntentService
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import timber.log.Timber

// un IntentService est une activity qui n'a pas d'UI et qui peut rester en vie plus longtemps qu'une activity
class GeofenceIntentService : IntentService("EndorGeofenceIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        // extraire les infos de geofencingEvent à partir de l'intent
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            Timber.e("Error in Geofence Intent ${geofencingEvent.errorCode}")
            return
        }

        // récupérer le type de transition
        val geofenceTransition = geofencingEvent.geofenceTransition
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            Timber.e("Unhandle geofencing transition: $geofenceTransition")
        }

        // vérifier qu'une geofence a été déclanchée
        if (geofencingEvent.triggeringGeofences == null) {
            Timber.w("Empty triggering geofences, nothing to do")
        }

        for (triggeringGeofence in geofencingEvent.triggeringGeofences) {
            if (triggeringGeofence.requestId == GEOFENCE_ID_MORDOR) {
                Timber.w("ENTERING MORDOR")
            }
        }
    }
}