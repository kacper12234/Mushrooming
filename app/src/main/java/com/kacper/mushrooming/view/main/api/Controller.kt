package com.kacper.mushrooming.view.main.api

import com.google.android.gms.maps.model.LatLng
import com.kacper.mushrooming.R
import com.kacper.mushrooming.api.ApiClient.createService
import com.kacper.mushrooming.api.dto.*
import com.kacper.mushrooming.api.service.AuthService
import com.kacper.mushrooming.api.service.FindService
import com.kacper.mushrooming.utils.AuthenticationUtils.authenticationResponse
import com.kacper.mushrooming.utils.ErrorUtils.parseError
import com.kacper.mushrooming.view.main.dialog.DialogInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Controller(var apiInterface: ApiInterface) : DialogInterface {
    private var findService = createService(FindService::class.java)
    fun logout() {
        val service = createService(AuthService::class.java)
        val request = RefreshTokenRequest(authenticationResponse!!.refreshToken!!, null)
        service.logout(request).enqueue(object : Callback<Void?> {
            override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                if (response.isSuccessful) {
                    authenticationResponse = null
                    apiInterface.logout()
                } else {
                    apiInterface.fail(R.string.logout_fail)
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                t.printStackTrace()
                apiInterface.fail(R.string.logout_fail)
            }
        })
    }

    fun getSpots(northeast: LatLng, southwest: LatLng) {
        findService.getFinds(southwest.latitude, northeast.latitude, southwest.longitude, northeast.longitude).enqueue(object : Callback<List<FindResponse>> {
            override fun onResponse(call: Call<List<FindResponse>>, response: Response<List<FindResponse>>) {
                if (response.isSuccessful) {
                    apiInterface.getSpots(response.body() as MutableList<FindResponse>)
                    println("Success")
                }
            }

            override fun onFailure(call: Call<List<FindResponse>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    override fun addFind(species: String, lat: Double, lon: Double) {
        val findRequest = FindRequest(species, lon, lat)
        findService.saveFind(findRequest).enqueue(object : Callback<FindResponse> {
            override fun onResponse(call: Call<FindResponse>, response: Response<FindResponse>) {
                if (response.isSuccessful) apiInterface.addFind(response.body()!!)
            }

            override fun onFailure(call: Call<FindResponse?>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    override fun addVisit(state: String, count: Int, findId: Long) {
        val visitDto = VisitRequest(findId, state, count)
        findService.saveVisit(visitDto).enqueue(object : Callback<VisitResponse> {
            override fun onResponse(call: Call<VisitResponse>, response: Response<VisitResponse>) {
                if (response.isSuccessful) apiInterface.addVisit(response.body()!!)
            }

            override fun onFailure(call: Call<VisitResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}