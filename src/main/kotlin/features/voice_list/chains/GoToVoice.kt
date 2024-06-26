package features.voice_list.chains

import chain.Chain
import core.Updating
import data.VoiceStorage
import domain.logExceptionToAll
import domain.messages.ContactDevMessage
import domain.messages.VoiceToMessage
import executables.DeleteMessage
import executables.Executable
import executables.SendMessage
import handlers.OnTextViaBot
import helpers.FileDownloadException
import helpers.FileUrl
import logs.LogLevel
import logs.Logging
import org.json.JSONObject
import sBot
import sDownloadVoice
import translations.domain.ContextString
import updating.UpdatingItem
import updating.UpdatingMessage
import updating.UserIdUpdating

class GoToVoice : Chain(OnTextViaBot()) {

    override suspend fun executableChain(updating: Updating): List<Executable> {
        val text = updating.map(UpdatingMessage())
        return if (text.contains("voice=")) {
            val voiceId = text.split("=")[1].toInt()
            listOf(
                DeleteMessage(mKey, updating),
                SendMessage(
                    mKey,
                    ContextString.Base.Strings().string(sDownloadVoice, updating),
                ) {
                    try {
                        val deleteWaitMessageAction = DeleteMessage(
                            mKey,
                            updating.map(UserIdUpdating()).toString(), it.toLong()
                        )
                        val sendAudioAction = VoiceStorage.Base.Instance().voiceById(voiceId.toLong()).map(
                            VoiceToMessage(
                                mKey,
                                updating, mIsJustSaved = false, mIsAudio = true
                            ) { mp3Id ->
                                VoiceStorage.Base.Instance().voiceMp3IdUpdate(voiceId.toLong(), mp3Id)
                            }
                        )
                        sBot.implementRequest(
                            deleteWaitMessageAction.map(JSONObject()),
                            deleteWaitMessageAction
                        )
                        sBot.implementRequest(
                            sendAudioAction.map(updating.map(UpdatingItem())),
                            sendAudioAction
                        )
                    } catch (e: FileDownloadException) {
                        tryUpdateDownloadLink(voiceId.toLong(), updating, it.toLong())
                    } catch (e: IllegalArgumentException) {
                        tryUpdateDownloadLink(voiceId.toLong(), updating, it.toLong())
                    }
                }
            )
        } else {
            emptyList()
        }
    }

    private fun tryUpdateDownloadLink(voiceId: Long, updating: Updating, messageId: Long) {
        val deleteWaitMessageAction = DeleteMessage(
            mKey,
            updating.map(UserIdUpdating()).toString(), messageId
        )
        try {
            Logging.ConsoleLog.logToFile("Try update download link", LogLevel.Warning)
            VoiceStorage.Base.Instance().updateDownloadLink(
                voiceId,
                FileUrl.Base(
                    mKey, VoiceStorage.Base.Instance().voiceFileId(voiceId)
                ).fileUrl()
            )
            val sendAudioAction = VoiceStorage.Base.Instance().voiceById(voiceId).map(
                VoiceToMessage(
                    mKey,
                    updating, mIsJustSaved = false, mIsAudio = true
                ) { mp3Id ->
                    VoiceStorage.Base.Instance().voiceMp3IdUpdate(voiceId, mp3Id)
                }
            )
            sBot.implementRequest(
                deleteWaitMessageAction.map(JSONObject()),
                deleteWaitMessageAction
            )
            sBot.implementRequest(
                sendAudioAction.map(JSONObject()),
                sendAudioAction
            )
        } catch (e: Exception) {
            Logging.ConsoleLog.logExceptionToAll(e)
            sBot.implementRequest(
                deleteWaitMessageAction.map(JSONObject()),
                deleteWaitMessageAction
            )
            val contactDev = ContactDevMessage(mKey, updating)
            sBot.implementRequest(
                contactDev.map(updating.map(UpdatingItem())),
                contactDev
            )
        }
    }
}