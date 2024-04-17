package com.azeroth.healthapp.utils

import com.google.android.gms.maps.model.LatLng
import kotlin.math.pow

object LocationUtils {

    private const val EARTH_RADIUS_KM = 6371.0 // Radius of the Earth in kilometers

    fun calculatePolylineDistance(polyList: List<LatLng>): Double {
        var totalDistance = 0.0

        // Calculate the distance between consecutive points
        for (i in 0 until polyList.size - 1) {
            val lat1 = polyList[i].latitude
            val lon1 = polyList[i].longitude
            val lat2 = polyList[i + 1].latitude
            val lon2 = polyList[i + 1].longitude
            val segmentDistance = calculateDistance(lat1, lon1, lat2, lon2)
            totalDistance += segmentDistance
        }

        return totalDistance
    }

    private fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        // Convert latitude and longitude from degrees to radians
        val lat1Rad = Math.toRadians(lat1)
        val lon1Rad = Math.toRadians(lon1)
        val lat2Rad = Math.toRadians(lat2)
        val lon2Rad = Math.toRadians(lon2)

        // Calculate the change in coordinates
        val deltaLat = lat2Rad - lat1Rad
        val deltaLon = lon2Rad - lon1Rad

        // Calculate distance using Haversine formula
        val a = Math.sin(deltaLat / 2).pow(2) + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.sin(
            deltaLon / 2
        ).pow(2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val distance = EARTH_RADIUS_KM * c

        return distance
    }


}
