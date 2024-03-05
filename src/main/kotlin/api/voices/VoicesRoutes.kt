package api.voices

import api.RouteWrapper
import api.RoutesFeature
import io.ktor.http.*
import io.ktor.server.routing.*

class VoicesRoutes : RoutesFeature {

    override fun routes() = listOf(
        RouteWrapper("/voices", HttpMethod.Get, Route::allVoices),
        RouteWrapper("/download", HttpMethod.Get, Route::downloadVoice)
    )
}