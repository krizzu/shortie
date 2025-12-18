package com.kborowy.shortie.data.urls

import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable data class PageCursor(val shortCode: String, val createdAt: Instant)
