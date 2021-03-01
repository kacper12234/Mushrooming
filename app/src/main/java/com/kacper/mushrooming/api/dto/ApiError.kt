package com.kacper.mushrooming.api.dto

import android.util.Log

// for debugging purpose
class ApiError {
    private val status = 0
    private val error: String? = null
    private val path: String? = null
    fun logError() {
        Log.d("Status-$status", error!!)
        Log.d("Path", path!!)
    }
}