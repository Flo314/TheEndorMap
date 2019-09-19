package com.dtc.theendormap

/**
 * iconId et iconColor sont exclusif
 * si on en définit un on ne peut pas définir l'autre
 */
data class Poi(val title: String,
               var latitude: Double,
               var longitude: Double,
               val imageId: Int = 0,
               val iconId: Int = 0,
               val iconColor: Int = 0,
               val description: String = "",
               val detailUrl: String = "")
