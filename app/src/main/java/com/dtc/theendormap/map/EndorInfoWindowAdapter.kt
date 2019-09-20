package com.dtc.theendormap.map

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.dtc.theendormap.R
import com.dtc.theendormap.poi.Poi
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class EndorInfoWindowAdapter(context: Context) : GoogleMap.InfoWindowAdapter{

    private val contents: View = LayoutInflater.from(context).inflate(R.layout.info_window_endor, null)

    // contenu affiché visuellement (window marker icon)
    override fun getInfoContents(marker: Marker): View {
        // récupérer les infos associé aux marker
        val poi = marker.tag as Poi

        with (contents) {
            val imageId = if (poi.imageId > 0) poi.imageId else R.drawable.marker_frodo
            findViewById<ImageView>(R.id.imageView).setImageResource(imageId)

            findViewById<TextView>(R.id.titleTextView).text = poi.title
            findViewById<TextView>(R.id.descriptionTextView).text = poi.description
        }

        return contents
    }

    override fun getInfoWindow(p0: Marker?): View? = null
}