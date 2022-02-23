package com.myapp.kundli.test

import android.content.Context
import android.location.LocationManager


fun Context.isGPSEnabled(): Boolean {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    locationManager?.let {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
    return false
}

fun Context.isLocationNetworkProvider(): Boolean {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    locationManager?.let {
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    return false
}