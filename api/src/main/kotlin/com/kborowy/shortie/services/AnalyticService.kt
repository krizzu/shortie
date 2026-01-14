package com.kborowy.shortie.services

import com.kborowy.shortie.data.urls.UrlsRepository
import com.kborowy.shortie.models.ShortieUrl

interface AnalyticService {

    /**
     * Increments
     */
    fun incrementClick(shortie: ShortieUrl)

}

/**
 * todo:
 *  - own analytics coroutine scope linked to application scope for cancellation
 *  - create analytics repo to track daily clicks of links
 *  - call analyticsRepo to update daily count
 */
fun AnalyticService(urlRepo: UrlsRepository): AnalyticService = RealAnalyticService(urlRepo)


private class RealAnalyticService(urlRepo: UrlsRepository) : AnalyticService {
    override fun incrementClick(shortie: ShortieUrl) {
        TODO("Not yet implemented")
    }
}