package com.kborowy.shortie.routes.urls

import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable
data class GenerateShortiePayloadDTO(
    val url: String,
    val expiryDate: Instant?,
    val password: String?,
    val alias: String?,
)

@Serializable data class GenerateShortieResponseDTO(val shortCode: String)

@Serializable
data class ShortieDTO(
    val shortCode: String,
    val originalUrl: String,
    val protected: Boolean,
    val expiryDate: Instant?,
)

@Serializable
data class PaginatedShortieResponseDTO(
    val data: List<ShortieDTO>,
    val hasNext: Boolean,
    val nextCursor: String?,
)
