package api.voices

import api.utils.getUserId
import api.utils.getVoiceId
import core.storage.Storages
import data.CurrentVoiceJsonModel
import data.VoiceStatus
import data.VoiceStorage
import data.VoiceUserId
import exception_handlers.custom_exceptions.VoiceAccessForbiddenException
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.currentVoice() {
    handle {
        val voiceId = call.getVoiceId()
        val userId = call.getUserId()
        val voice = VoiceStorage.Base.Instance().voiceById(voiceId, VoiceStatus.CREATING)
        if (voice.map(VoiceUserId()) == userId) {
            call.respond(
                voice.map(
                    CurrentVoiceJsonModel(
                        Storages.Main.Provider().stConfig.configValueString("selfUrl")
                    )
                ).toString(2)
            )
        } else {
            throw VoiceAccessForbiddenException(userId, voiceId)
        }
    }
}