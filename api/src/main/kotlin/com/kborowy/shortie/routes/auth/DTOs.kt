package com.kborowy.shortie.routes.auth

import kotlinx.serialization.Serializable

@Serializable data class LoginPayloadDTO(val user: String, val password: String)

@Serializable data class TokenResponseDTO(val accessToken: String, val refreshToken: String)

@Serializable data class TokenRefreshPayloadDTO(val refreshToken: String)

@Serializable data class TokenValidResponseDTO(val valid: Boolean)
