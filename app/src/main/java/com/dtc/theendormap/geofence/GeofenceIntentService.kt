package com.dtc.theendormap.geofence

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.dtc.theendormap.App
import com.dtc.theendormap.R
import com.dtc.theendormap.map.MapActivity
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import timber.log.Timber

private const val NOTIFICATION_ID_MORDOR = 0

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
                sendMordorNotification(geofenceTransition)
            }
        }
    }

    private fun sendMordorNotification(transitionType: Int) {
        // préparer le titre
        val title: String
        val text: String
        val drawable: Drawable

        when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                title = "You entered the Mordor!"
                text = "Be careful... Sauron is always watching..."
                drawable = ContextCompat.getDrawable(this, R.drawable.sauroneye)!!
            }
            else -> {
                title = "You left the Mordor"
                text = "You can breath now... But where is the One Ring?"
                drawable = ContextCompat.getDrawable(this, R.drawable.mordorgate)!!
            }
        }

        // convertir drawable en bitmap
        val bitmap = (drawable as BitmapDrawable).bitmap

        // Action sur le clic de la notification
        val intent = Intent(this, MapActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        // construire la notifications
        val builder = NotificationCompat.Builder(this, App.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(text)
            .setLargeIcon(bitmap)
            .setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(bitmap)
                .bigLargeIcon(null))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID_MORDOR, builder.build())
    }
}