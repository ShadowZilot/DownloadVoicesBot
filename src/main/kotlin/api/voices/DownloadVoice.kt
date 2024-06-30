package api.voices

import core.storage.Storages
import data.VoiceNotFound
import data.VoiceStatus
import data.VoiceStorage
import data.VoiceUserId
import domain.converting.AudioConverter
import exception_handlers.custom_exceptions.VoiceAccessForbiddenException
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
        val userId = handleQueryParam("user_id",
            defaultParam = 0L,
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
            "voice_id", defaultParam = -1L,
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
            val voice = VoiceStorage.Base.Instance().voiceByIdInAnyStatus(voiceId, VoiceStatus.DELETED)
            if (voice.map(VoiceUserId()) == userId) {
                val voiceMp3FileId = VoiceStorage.Base.Instance().voiceFileIdMp3(voiceId)
                val voiceMp3File = File(sBasePath, "${voiceId}.mp3")
                val audioBytes = if (voiceMp3FileId.isEmpty()) {
                    val ogaFileId = VoiceStorage.Base.Instance().voiceFileId(voiceId)
                    val ogaBytes = FileDownload.Base(
                        FileUrl.Base(
                            key, ogaFileId
                        ).fileUrl()
                    ).download()
                    if (voiceMp3File.exists()) voiceMp3File.delete()
                    AudioConverter.OgaToMp3Bytes(voiceId, ogaBytes).convertedBytes()
                } else {
                    val downloadLink = FileUrl.Base(
                        key, voiceMp3FileId
                    ).fileUrl()
                    VoiceStorage.Base.Instance().updateDownloadLink(voiceId, downloadLink)
                    FileDownload.Base(downloadLink).download()
                }
                if (!voiceMp3File.exists()) voiceMp3File.createNewFile()
                voiceMp3File.writeBytes(audioBytes)
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
                throw VoiceAccessForbiddenException(userId, voiceId)
            }
        } catch (e: VoiceNotFound) {
            call.respond(HttpStatusCode.NotFound, "Voice not found")
        }
    }
}