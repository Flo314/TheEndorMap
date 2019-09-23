package com.dtc.theendormap

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import timber.log.Timber

// point d'entrée de l'application pour pouvoir utiliser Timber
class App : Application() {

    // singleton qui permet d'avoir des variables static comme en java
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "EndorMap"
    }

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        createNotificationChannel()
    }

    // configuration notifications oreo
    private fun createNotificationChannel() {
        // vérifier le n° de version
        if (Build.VERSION.SDK_INT <  Build.VERSION_CODES.O) {
            return
        }

        val name = "Endor Map Notifications"
        val descriptionText = "Be notified when you enter Middle Earth special areas"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}