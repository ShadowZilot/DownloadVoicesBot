package api.voices

import core.storage.Storages
import data.VoiceNotFound
import data.VoiceStorage
import data.VoiceUserId
import data.poll.VoiceLink
import helper.handleQueryParam
import helpers.FileDownload
import helpers.FileUrl
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import logs.LogLevel
import logs.Logging
import sBasePath
import java.io.File

fun Route.downloadVoice() {
    handle {
        val key = Storages.Main.Provider().stConfig.configValueString("botKey")
        val userId = handleQueryParam(call.parameters["user_id"],
            defaultParam = 0L,
            context = this,
            nullAction = {
                call.respond(HttpStatusCode.BadRequest, "User id query param doesn't found")
            }, converterFunction = { param ->
                try {
                    param.toLong()
                } catch (e: NumberFormatException) {
                    call.respond(HttpStatusCode.BadRequest, "User id must be number")
                    0L
                }
            })
        val voiceId = handleQueryParam(
            call.parameters["voice_id"], defaultParam = -1L,
            context = this,
            nullAction = {
                call.respond(HttpStatusCode.BadRequest, "Voice id query param doesn't found")
            },
            converterFunction = { param ->
                try {
                    param.toLong()
                } catch (e: NumberFormatException) {
                    call.respond(HttpStatusCode.BadRequest, "Voice id must be number")
                    0L
                }
            }
        )
        try {
            var voice = VoiceStorage.Base.Instance().voiceById(voiceId)
            if (voice.map(VoiceUserId()) == userId) {
                VoiceStorage.Base.Instance().updateDownloadLink(
                    voiceId,
                    FileUrl.Base(
                        key, VoiceStorage.Base.Instance().voiceFileIdMp3(voiceId)
                    ).fileUrl()
                )
                voice = VoiceStorage.Base.Instance().voiceById(voiceId)
                val voiceMp3File = File(sBasePath, "${voiceId}.mp3")
                if (!voiceMp3File.exists()) voiceMp3File.createNewFile()
                val downloadLink = voice.map(VoiceLink())
                voiceMp3File.writeBytes(FileDownload.Base(downloadLink).download())
                call.response.header(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition.Attachment.withParameter(
                        ContentDisposition.Parameters.FileName,
                        "${voiceId}.mp3"
                    ).toString()
                )
                call.respondFile(voiceMp3File) {
                    Logging.ConsoleLog.log(this.status?.value.toString(), LogLevel.Info)
                }
            } else {
                call.respond(HttpStatusCode.Forbidden, "You don't have access to this voice")
            }
        } catch (e: VoiceNotFound) {
            call.respond(HttpStatusCode.NotFound, "Voice not found")
        }
    }
}