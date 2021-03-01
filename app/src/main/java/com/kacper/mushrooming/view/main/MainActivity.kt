package com.kacper.mushrooming.view.main

import android.Manifest
import android.app.ActivityManager
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.SphericalUtil
import com.kacper.mushrooming.R
import com.kacper.mushrooming.api.dto.FindResponse
import com.kacper.mushrooming.api.dto.VisitResponse
import com.kacper.mushrooming.utils.AuthenticationUtils.authenticationResponse
import com.kacper.mushrooming.view.login.LoginActivity
import com.kacper.mushrooming.view.main.api.ApiInterface
import com.kacper.mushrooming.view.main.api.Controller
import com.kacper.mushrooming.view.main.dialog.FindDialog
import com.kacper.mushrooming.view.main.dialog.VisitDialog
import com.kacper.mushrooming.view.main.dialog.VisitsDialog
import com.kacper.mushrooming.view.main.map.InfoWindowAdapter
import com.kacper.mushrooming.view.main.map.LocationTracker
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    ApiInterface {
    private lateinit var mMap: GoogleMap
    private lateinit var controller: Controller
    private lateinit var tracker: LocationTracker
    private lateinit var finds: MutableList<FindResponse>
    private lateinit var markerMap: MutableMap<Long, Marker>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val add = findViewById<FloatingActionButton>(R.id.fab)
        add.setOnClickListener {
            if (getFindInRange(tracker.location) == null)
                checkTerrain(mMap.projection.toScreenLocation(tracker.location))
            else showSnackbar(R.string.find_already_here)
        }
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)
        controller = Controller(this)
        finds = ArrayList()
        markerMap = HashMap()

        val service = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!service.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(
                applicationContext,
                getString(R.string.location_disabled),
                Toast.LENGTH_LONG
            ).show()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            mMap = googleMap
            mMap.let {
                it.moveCamera(CameraUpdateFactory.zoomTo(13f))
                tracker = LocationTracker(it, this)
                it.setMinZoomPreference(10f)
                it.setOnCameraIdleListener(tracker)
                it.setInfoWindowAdapter(InfoWindowAdapter(this))
                it.setOnInfoWindowClickListener { marker ->
                    VisitsDialog(visitsToString((marker.tag as FindResponse).visits)).show(
                        supportFragmentManager,
                        "Visits"
                    )
                }
            }
            launchMyLocation(true)
        }
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        if (id == R.id.nav_logout) controller.logout()
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun logout() {
        AlertDialog.Builder(this@MainActivity)
            .setMessage(R.string.logout_successful)
            .setNeutralButton("OK") { _: DialogInterface?, _: Int ->
                startActivity(
                    Intent(
                        this@MainActivity,
                        LoginActivity::class.java
                    ).putExtra("logout", true)
                )
                finishActivity(0)
            }
            .show()
    }

    override fun fail(text: Int) {
        Toast.makeText(this@MainActivity, text, Toast.LENGTH_LONG).show()
    }

    override fun getSpots(newFinds: MutableList<FindResponse>) {
        finds = if (finds.isNotEmpty()) {
            newFinds.removeAll(finds)
            finds.map { find ->
                newFinds.firstOrNull { newFind ->
                    find.id == newFind.id || !finds.contains(newFind)
                } ?: find
            }.toMutableList()
        } else
            newFinds
        for (find in newFinds)
            if (!markerMap.containsKey(find.id)) addMarker(find) else markerMap[find.id]!!.snippet =
                find.toString()
    }

    fun getSpots(bounds: LatLngBounds) {
        controller.getSpots(bounds.northeast, bounds.southwest)
    }

    private fun visitsToString(visits: List<VisitResponse>?): List<String> {
        return visits!!.map { obj -> obj.toString() }
    }

    private fun addMarker(find: FindResponse?) {
        val marker = mMap.addMarker(
            MarkerOptions().position(LatLng(find!!.lat, find.lon)).snippet(find.toString())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mushroom))
        )
        marker.tag = find
        markerMap[find.id] = marker
    }

    override fun addFind(find: FindResponse) {
        addMarker(find)
        finds.add(find)
    }

    override fun addVisit(visit: VisitResponse) {
        finds.firstOrNull { find -> find.id == visit.findId }?.let { find ->
            find.visits.add(visit)
            markerMap[find.id]!!.snippet = find.toString()
        }
    }

    private fun getFindInRange(loc: LatLng?): FindResponse? {
        return finds.firstOrNull { find ->
            SphericalUtil.computeDistanceBetween(
                loc,
                LatLng(find.lat, find.lon)
            ) < 25
        }
    }

    fun checkVisit(loc: LatLng?) {
            if (supportFragmentManager.findFragmentByTag("Visit")==null && finds.isNotEmpty()) {
                getFindInRange(loc)?.let { find ->
                    if (find.visits.none { visit -> visit.userId == authenticationResponse!!.id }
                    ) VisitDialog(controller, find.id).show(supportFragmentManager, "Visit")
                }
            }
    }

    private val isServiceRunning: Boolean
        get() {
            val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) if (NotificationService::class.java.name == service.service.className) return service.foreground
            return false
        }

    fun showSnackbar(string: Int) {
        Snackbar.make(
            findViewById<View>(android.R.id.content).rootView,
            string,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun checkTerrain(point: Point) {
        val zoom = mMap.cameraPosition.zoom
        launchMyLocation(false)
        if (zoom >= 14) mMap.moveCamera(CameraUpdateFactory.zoomTo(13.9f))
        mMap.snapshot { bitmap: Bitmap ->
            val pixel = bitmap.getPixel(point.x, point.y)
            if (Color.red(pixel) == 206 && Color.blue(pixel) == 206 && Color.green(pixel) == 239) FindDialog(
                controller,
                tracker.location
            ).show(
                supportFragmentManager,
                "Find"
            ) else showSnackbar(R.string.add_fail)
            launchMyLocation(true)
            if (mMap.cameraPosition.zoom != zoom) mMap.moveCamera(
                CameraUpdateFactory.zoomTo(
                    zoom
                )
            )
        }
    }

    fun requestPermissions() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), 1
        )
    }

    private fun launchMyLocation(enable: Boolean) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) requestPermissions()
        mMap.isMyLocationEnabled = enable
    }

    //in development
    private fun startNotificationService() {
        if (!isServiceRunning) {
            val intent = Intent(applicationContext, NotificationService::class.java)
            intent.putExtra("notification", true)
            startService(intent)
        }
    }

    private fun stopNotificationService() {
        if (isServiceRunning) {
            val intent = Intent(applicationContext, NotificationService::class.java)
            intent.putExtra("notification", false)
            startService(intent)
        }
    }
}