package com.kacper.mushrooming.view.main.map

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class InfoWindowAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {
    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View {
        val info = LinearLayout(context)
        info.orientation = LinearLayout.VERTICAL
        val snippet = TextView(context)
        snippet.text = marker.snippet
        info.addView(snippet)
        return info
    }
}