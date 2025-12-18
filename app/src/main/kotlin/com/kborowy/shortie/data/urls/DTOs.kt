package com.kborowy.shortie.data.urls

import com.kborowy.shortie.models.ShortieUrl
import kotlin.time.Instant

data class ShortieUrlPaginated(
    val data: List<ShortieUrl>,
    val hasNext: Boolean,
    val nextCursor: PageCursor?,
)

data class PageCursor(val shortCode: String, val createdAt: Instant)
