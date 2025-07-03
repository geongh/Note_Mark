package com.solaisc.notemark.feature.auth.data

import kotlinx.serialization.Serializable

@Serializable
data class LogoutRequest(
    val refreshToken: String
)
