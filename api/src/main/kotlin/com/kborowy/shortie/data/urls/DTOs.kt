/*
 * Copyright 2026 Krzysztof Borowy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kborowy.shortie.data.urls

import com.kborowy.shortie.models.ShortiePageCursor
import com.kborowy.shortie.models.ShortieUrl

data class ShortieUrlPaginated(
    val data: List<ShortieUrl>,
    val hasNext: Boolean,
    val nextCursor: ShortiePageCursor?,
)

data class ShortieUrlTotals(val expired: Long, val active: Long, val total: Long)
