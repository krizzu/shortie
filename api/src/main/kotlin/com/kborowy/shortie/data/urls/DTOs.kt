package com.kborowy.shortie.data.urls

import com.kborowy.shortie.models.ShortiePageCursor
import com.kborowy.shortie.models.ShortieUrl

data class ShortieUrlPaginated(
    val data: List<ShortieUrl>,
    val hasNext: Boolean,
    val nextCursor: ShortiePageCursor?,
)
