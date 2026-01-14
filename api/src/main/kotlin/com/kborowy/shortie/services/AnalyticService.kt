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
package com.kborowy.shortie.services

import com.kborowy.shortie.data.urls.UrlsRepository
import com.kborowy.shortie.models.ShortieUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface AnalyticService {

    /** Increments total clicks and last redirected date */
    fun incrementClick(shortie: ShortieUrl)
}

fun AnalyticService(
    urlRepo: UrlsRepository,
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
    log: Logger = LoggerFactory.getLogger("AnalyticService"),
): AnalyticService = RealAnalyticService(urlRepo, scope, log)

private class RealAnalyticService(
    private val urlRepo: UrlsRepository,
    private val scope: CoroutineScope,
    private val log: Logger,
) : AnalyticService {
    override fun incrementClick(shortie: ShortieUrl) {
        scope.launch(Dispatchers.IO) {
            urlRepo.incrementClickCount(shortie.shortCode)
            log.info("${shortie.shortCode} click bump")
        }
    }
}
