package com.dtc.theendormap

import android.app.Application
import timber.log.Timber

// point d'entrée de l'application pour pouvoir utiliser Timber
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}