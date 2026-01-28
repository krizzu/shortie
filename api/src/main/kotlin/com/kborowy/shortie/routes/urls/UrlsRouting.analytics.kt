package com.kborowy.shortie.routes.urls

import com.kborowy.shortie.errors.BadRequestError
import com.kborowy.shortie.errors.NotFoundHttpError
import com.kborowy.shortie.extensions.asInstantUTC
import com.kborowy.shortie.extensions.today
import com.kborowy.shortie.services.analytics.AnalyticService
import com.kborowy.shortie.services.urls.UrlsService
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import org.koin.ktor.ext.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Route.urlsAnalyticRouting() {

    val log = LoggerFactory.getLogger("AuthRoute")
    val urls by inject<UrlsService>()
    val analytics by inject<AnalyticService>()

    get("/links") {
        val limit = call.parameters["limit"]?.toIntOrNull() ?: 20
        val page = call.parameters["page"]?.toIntOrNull()
        val result = analytics.getLinksPaginated(limit, page)
        call.respond(
            PaginatedOffsetShortieResponseDTO(
                hasNext = result.hasNext,
                nextPage = result.nextPage,
                data =
                    result.links.map {
                        ShortieAnalyticsDTO(
                            shortCode = it.shortCode.value,
                            totalClicks = it.totalClicks,
                            lastClick = it.lastClick?.asInstantUTC,
                        )
                    },
            )
        )
    }

    get("/weekly") {
        val startDate = LocalDate.today.minus(DatePeriod(days = 7))
        val endDate = LocalDate.today
        val details = analytics.getDetails(startDate, endDate)
        call.respond(
            AnalyticPeriodDTO(
                totalClicksInPeriod = details.totalClicksInPeriod,
                clicksPerDate = details.clicksPerDay,
            )
        )
    }

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

        val details =
            analytics.getDetailsForCode(shortie, startDate, endDate)
                ?: return@get call.respondText(
                    "null",
                    ContentType.Application.Json,
                    HttpStatusCode.OK,
                )
        call.respond<ShortieAnalyticsDetailsDTO>(
            ShortieAnalyticsDetailsDTO(
                info =
                    ShortieAnalyticsDTO(
                        shortCode = shortie.shortCode.value,
                        totalClicks = shortie.totalClicks,
                        lastClick = shortie.lastRedirect?.asInstantUTC,
                    ),
                details =
                    AnalyticPeriodDTO(
                        totalClicksInPeriod = details.details.totalClicksInPeriod,
                        clicksPerDate = details.details.clicksPerDay,
                    ),
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
