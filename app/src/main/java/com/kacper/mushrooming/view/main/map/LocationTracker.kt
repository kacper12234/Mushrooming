package com.kacper.mushrooming.view.main.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.SphericalUtil
import com.kacper.mushrooming.R
import com.kacper.mushrooming.view.main.MainActivity

class LocationTracker(private val mMap: GoogleMap, private val activity: MainActivity) :
    OnCameraIdleListener {
    private lateinit var stop: LatLngBounds
    private var start = mMap.projection.visibleRegion.latLngBounds
    lateinit var location: LatLng
    private lateinit var prev: LatLng
    private val callback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (result.lastLocation != null)
                location = LatLng(result.lastLocation.latitude, result.lastLocation.longitude)
            if (mMap.projection.visibleRegion.latLngBounds.contains(location) && prev != location
                || !::prev.isInitialized
            ) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
                activity.checkVisit(location)
            }
            prev = location
        }

        override fun onLocationAvailability(check: LocationAvailability?) {
            super.onLocationAvailability(check)
            if (!check!!.isLocationAvailable && ::prev.isInitialized)
                activity.showSnackbar(R.string.location_unavailable)
        }
    }

    override fun onCameraIdle() {
        stop = mMap.projection.visibleRegion.latLngBounds
        if ((!start.contains(stop.northeast) || !start.contains(stop.southwest))
            && (getDistance(start.northeast, stop.northeast) ||
                    getDistance(start.southwest, stop.southwest))
        ) {
            if (!start.contains(stop.northeast) && !start.contains(stop.southwest)) {
                start = stop
            } else if (!start.contains(stop.northeast)) start = start.including(stop.northeast)
            else if (!start.contains(stop.southwest)) start = start.including(stop.southwest)
            activity.getSpots(start)
        }
    }

    private fun getDistance(start: LatLng, stop: LatLng): Boolean {
        return SphericalUtil.computeDistanceBetween(start, stop) > 200
    }

    init {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) activity.requestPermissions()
        val locationRequest = LocationRequest()
        locationRequest.apply {
            interval = 4000
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        LocationServices.getFusedLocationProviderClient(activity)
            .requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())
    }
}

