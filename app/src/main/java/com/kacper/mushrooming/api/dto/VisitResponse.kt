package com.kacper.mushrooming.api.dto

import com.kacper.mushrooming.R
import com.kacper.mushrooming.utils.DateUtils.formatDate
import com.kacper.mushrooming.utils.Strings.get

class VisitResponse(
        val findId: Long,
        val userId: Long,
        private val user: String,
        val state: String,
        private val count: Int,
        visitedAt: String)
{
    var visitedAt = visitedAt
    get() = formatDate(field)

    override fun toString(): String {
        return """${get(R.string.visit_author)} $user
${get(R.string.visit_visited)} $visitedAt
${get(R.string.visit_amount)} $count
${get(R.string.visit_state)} $state"""
    }
}