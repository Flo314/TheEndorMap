package com.dtc.theendormap

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import timber.log.Timber

// requestcode -> jeton qui rapelle
private const val REQUEST_PERMISSION_LOCATION_LAST_LOCATION = 1

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // récupération d'un client de location qui est fused qui va requêter le wifi, le gps et renvoyer une location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        updateLastLocation()
    }

    private fun updateLastLocation() {
        Timber.d("updateLocation()")
        // vérifier si on a la permission pour vérifier la location
        if (!checkLocationPermission()) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            Timber.i("Last Location $location")
        }
    }

    // vérifie si on a la permission
    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // demande de permission (dialog)
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSION_LOCATION_LAST_LOCATION
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

        }
    }
}
