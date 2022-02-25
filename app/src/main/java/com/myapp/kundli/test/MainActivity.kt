package com.myapp.kundli.test

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import com.google.android.gms.location.LocationServices
import com.myapp.kundli.test.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: ActivityViewModel
    private lateinit var dataBinding: ActivityMainBinding
    private var locationManager: LocationManager? = null
    private val locationListener = LocationListener { location ->
        onLastLocation(location)
    }

    private var permissionActivityResult = registerForActivityResult(
        ActivityResultContracts
            .RequestMultiplePermissions()
    ) { grantResult ->
        if (grantResult[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grantResult[android.Manifest.permission.ACCESS_FINE_LOCATION] == true
        ) {
            viewModel.permissionErrorField.set(true)
            initializeLocationComponents()
        } else {
            viewModel.permissionErrorField.set(false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        viewModel = ViewModelProvider(this).get(ActivityViewModel::class.java)
        dataBinding.viewModel = viewModel
        dataBinding.lifecycleOwner = this

        askForPermission()
        dataBinding.layoutInclude.btnGrant.setOnClickListener {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        dataBinding.layoutInclude.btnExit.setOnClickListener {
        finish()
        }
    }

    fun askForPermission() {
        permissionActivityResult.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

    }

    private fun onLastLocation(location: Location?) {
        lifecycleScope.launch {
            location?.let {
                Log.d("test", "${it.latitude}  ${it.longitude}")
                val distanceResult =
                    distanceInKm(it.latitude, it.longitude, viewModel.latitude, viewModel.longitude)
                val number = distanceResult
                val rounded = number.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
                viewModel.coveredDistanceField.set(rounded.toString() + "mt")
                viewModel.latlongField.set("Latitude is :" + it.latitude.toString() +" "+ "Longitude is:" + it.longitude.toString())
                viewModel.locationMovedField.set("You have been moved" + " " + rounded.toString() + "mt" + " " + "from your previous location")
                viewModel.latitude = it.latitude
                viewModel.longitude = it.longitude

            }
        }
    }

    fun distanceInKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist =
            Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(
                deg2rad(lat2)
            ) * Math.cos(deg2rad(theta))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515
        dist = dist * 1.609344
        return dist * 1000
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    private fun initializeLocationComponents() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askForPermission()
            return
        }
        if (locationManager == null) {
            locationManager =
                getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        if (isGPSEnabled()) {

            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                0F,
                locationListener
            )

            locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let { location ->
                onLastLocation(location)
            }
        }

        if (isLocationNetworkProvider()) {
            locationManager?.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                5000,
                0F,
                locationListener
            )

            locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                ?.let { location ->
                    onLastLocation(location)
                }
        }

/* Initialize Callback to get location updates */
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                onLastLocation(location)
            }

    }

}