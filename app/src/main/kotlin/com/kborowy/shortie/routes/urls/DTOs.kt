package com.kborowy.shortie.routes.urls

import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class GenerateShortieDTO(
    val url: String,
    val expiryDate: Instant?,
    val password: String?,
    val alias: String?,
)

@Serializable data class GenerateShortieResponseDTO(val shortCode: String)
