package com.kborowy.shortie.routes.login

import kotlinx.serialization.Serializable

@Serializable
data class LoginPayloadDTO(val user: String, val password: String)

@Serializable
data class LoginResponseDTO(val accessToken: String, val refreshToken: String)