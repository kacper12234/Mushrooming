package com.kacper.mushrooming.api.dto

class AuthenticationResponse(
        var authenticationToken: String? = null,
        val id: Long? = null,
        var expiresAt: String? = null,
        var refreshToken: String? = null
)