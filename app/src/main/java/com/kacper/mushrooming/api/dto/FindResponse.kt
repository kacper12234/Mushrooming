package com.kacper.mushrooming.api.dto

import com.kacper.mushrooming.R
import com.kacper.mushrooming.utils.DateUtils.formatDate
import com.kacper.mushrooming.utils.Strings.get

class FindResponse(val id: Long,
                   private val species: String,
                   val lon: Double,
                   val lat: Double,
                   private val user: String,
                   createdDate: String,
                   val visits: MutableList<VisitResponse>) {

    private var createdDate = createdDate
    get() = formatDate(field)

    override fun toString(): String {
        val lastVisit: String
        val lastState: String
        if (visits.isNotEmpty()) {
            lastVisit = visits[visits.size - 1].visitedAt
            lastState = visits[visits.size - 1].state
        } else {
            lastVisit = createdDate
            lastState = "-"
        }
        return """${get(R.string.find_species)} $species
${get(R.string.find_lastVisit)} $lastVisit
${get(R.string.find_lastState)} $lastState
${get(R.string.find_creator)} $user
${get(R.string.find_createdAt)} $createdDate

${get(R.string.find_click)}"""
    }
}