package api.voices

import data.VoiceStorage
import data.VoiceToJsonResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.json.JSONArray

fun Route.allVoices() {
    handle {
        val userId = call.parameters["user_id"]
        if (userId == null) {
            call.respond(
                HttpStatusCode.BadRequest,
                "User id query param doesn't found"
            )
        }
        val page = call.parameters["page"]?.toInt() ?: 0
        val searchQuery = call.parameters["query"] ?: ""
        try {
            val mapper = VoiceToJsonResponse()
            val voices = VoiceStorage.Base.Instance().voicesList(
                userId?.toLong() ?: throw NumberFormatException(),
                page * 50,
                searchQuery
            )
            call.respond(HttpStatusCode.OK, JSONArray().apply {
                for (i in voices.indices) {
                    put(voices[i].map(mapper))
                }
            }.toString(2))
        } catch (e: NumberFormatException) {
            call.respond(
                HttpStatusCode.BadRequest,
                "User id query param doesn't found"
            )
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.BadRequest,
                "Something went wrong. Try to change request and repeat"
            )
        }
    }
}