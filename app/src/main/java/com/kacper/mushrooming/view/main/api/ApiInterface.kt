package com.kacper.mushrooming.view.main.api

import com.kacper.mushrooming.api.dto.FindResponse
import com.kacper.mushrooming.api.dto.VisitResponse

interface ApiInterface {
    fun getSpots(newFinds: MutableList<FindResponse>)
    fun fail(text: Int)
    fun logout()
    fun addFind(find: FindResponse)
    fun addVisit(visit: VisitResponse)
}