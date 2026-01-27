package com.kborowy.shortie.routes.urls

import com.kborowy.shortie.errors.BadRequestError
import com.kborowy.shortie.errors.NotFoundHttpError
import com.kborowy.shortie.extensions.asInstantUTC
import com.kborowy.shortie.services.analytics.AnalyticService
import com.kborowy.shortie.services.urls.UrlsService
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import kotlinx.datetime.LocalDate
import org.koin.ktor.ext.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Route.urlsAnalyticRouting() {

    val log = LoggerFactory.getLogger("AuthRoute")
    val urls by inject<UrlsService>()
    val analytics by inject<AnalyticService>()

    get("/overview") {
        val overview = analytics.totalOverview()

        call.respond(
            TotalOverviewResponseDTO(
                totalLinks = overview.totalLinks,
                activeLinks = overview.activeLinks,
                expiredLinks = overview.expiredLinks,
                totalClicks = overview.totalClicks,
            )
        )
    }

    get("/{shortCode}") {
        val startDate =
            parseDateParam("startDate", log)
                ?: throw BadRequestError("missing start date in request")
        val endDate =
            parseDateParam("endDate", log) ?: throw BadRequestError("missing end date in request")
        val shortCode =
            call.parameters["shortCode"] ?: throw BadRequestError("missing shortcode in request")
        val shortie =
            urls.resolveShortCode(shortCode) ?: throw NotFoundHttpError("shortie not found")

        val details = analytics.getDetails(shortie, startDate, endDate)
        call.respond<ShortieAnalyticsDetailsDTO>(
            ShortieAnalyticsDetailsDTO(
                info =
                    ShortieAnalyticsDTO(
                        shortCode = shortie.shortCode.value,
                        originalUrl = shortie.originalUrl.value,
                        protected = shortie.protected,
                        expiryDate = shortie.expiryDate?.asInstantUTC,
                        totalClicks = shortie.totalClicks,
                        lastClick = shortie.lastRedirect?.asInstantUTC,
                    ),
                details = details?.clicksOverTime ?: mapOf(),
            )
        )
    }
}

private fun RoutingContext.parseDateParam(paramName: String, log: Logger): LocalDate? {
    val dateString = call.parameters[paramName]

    if (dateString == null) {
        log.warn("$paramName parameter not provided")
        throw BadRequestError("missing 'paramName' parameter")
    }
    return try {
        LocalDate.parse(dateString, format = LocalDate.Formats.ISO)
    } catch (e: Exception) {
        log.error("failed to parse date (date=${dateString})", e)
        null
    }
}
