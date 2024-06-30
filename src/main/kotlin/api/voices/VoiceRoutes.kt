package api.voices

import api.utils.getUserId
import data.VoiceStorage
import data.VoiceToJsonResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.json.JSONArray

fun Route.allVoices() {
    handle {
        val userId = call.getUserId()
        val page = call.parameters["page"]?.toInt() ?: 0
        val searchQuery = call.parameters["query"] ?: ""
        val mapper = VoiceToJsonResponse()
        val voices = VoiceStorage.Base.Instance().voicesList(
            userId,
            page * 50,
            searchQuery
        )
        call.respond(HttpStatusCode.OK, JSONArray().apply {
            for (i in voices.indices) {
                put(voices[i].map(mapper))
            }
        }.toString(2))
    }
}