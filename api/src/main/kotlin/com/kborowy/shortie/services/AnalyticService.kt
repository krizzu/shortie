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

/**
 * todo:
 * - create analytics repo to track daily clicks of links
 * - call analyticsRepo to update daily count
 */
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
