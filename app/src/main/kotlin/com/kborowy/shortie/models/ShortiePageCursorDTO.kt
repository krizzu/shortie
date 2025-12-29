package com.kborowy.shortie.models

import kotlin.time.Instant
import kotlinx.serialization.Serializable

@Serializable data class ShortiePageCursorDTO(val id: Long, val createdAt: Instant)
