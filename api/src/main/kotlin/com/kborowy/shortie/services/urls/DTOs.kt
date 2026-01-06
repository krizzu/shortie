package com.kborowy.shortie.services.urls

import com.kborowy.shortie.models.ShortieUrl

data class ShortieUrlPaginatedEncoded(
    val data: List<ShortieUrl>,
    val hasNext: Boolean,
    val nextCursor: String?,
)
