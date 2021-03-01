package com.kacper.mushrooming.view.main

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class NotificationService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private val callback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (result.lastLocation != null) println("Service")
        }
    }

    @SuppressLint("MissingPermission")
    private fun startNotificationService() {
        val channelId = "New find nearby"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val resultIntent = Intent()
        val pendingIntent = PendingIntent.getActivity(
                applicationContext, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(applicationContext, channelId)
        builder.setContentTitle(channelId)
        builder.setContentText("text")
        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(false)
        builder.priority = NotificationCompat.PRIORITY_MAX
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(channelId) == null) {
                val notificationChannel = NotificationChannel(channelId, "Mushrooming", NotificationManager.IMPORTANCE_HIGH)
                notificationChannel.description = "This channel is used by Mushrooming"
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }
        val locationRequest = LocationRequest()
        locationRequest.interval = 60000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())
        startForeground(175, builder.build())
    }

    private fun stopNotificationService() {
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(callback)
        stopForeground(true)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.getBooleanExtra("notification", false)) startNotificationService() else stopNotificationService()
        return super.onStartCommand(intent, flags, startId)
    }
}