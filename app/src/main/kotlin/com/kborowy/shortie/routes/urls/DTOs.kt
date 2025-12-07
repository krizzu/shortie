package com.kborowy.shortie.routes.urls

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable data class ShortiePasswordDTO(val password: String)

@Serializable
data class GenerateShortieDTO(
    val url: String,
    val expiryDate: LocalDateTime?,
    val password: String?,
    val alias: String?,
)

@Serializable data class GenerateShortieResponseDTO(val shortCode: String)
