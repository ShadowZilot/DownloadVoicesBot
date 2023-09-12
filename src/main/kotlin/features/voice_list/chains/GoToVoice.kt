package features.voice_list.chains

import chain.Chain
import core.Updating
import data.VoiceStorage
import domain.VoiceToMessage
import executables.DeleteMessage
import executables.Executable
import executables.SendMessage
import handlers.OnTextViaBot
import helpers.FileDownloadException
import helpers.FileUrl
import logs.Logging
import org.json.JSONObject
import sBot
import sDownloadVoice
import translations.domain.ContextString
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
                                updating, false
                            )
                        )
                        sBot.implementRequest(
                            deleteWaitMessageAction.map(JSONObject()),
                            deleteWaitMessageAction
                        )
                        sBot.implementRequest(
                            sendAudioAction.map(JSONObject()),
                            sendAudioAction
                        )
                    } catch (e: FileDownloadException) {
                        Logging.ConsoleLog.log("Try update download link")
                        VoiceStorage.Base.Instance().updateDownloadLink(
                            voiceId.toLong(),
                            FileUrl.Base(
                                mKey, VoiceStorage.Base.Instance().voiceFileId(voiceId.toLong())
                            ).fileUrl()
                        )
                        val deleteWaitMessageAction = DeleteMessage(
                            mKey,
                            updating.map(UserIdUpdating()).toString(), it.toLong()
                        )
                        val sendAudioAction = VoiceStorage.Base.Instance().voiceById(voiceId.toLong()).map(
                            VoiceToMessage(
                                mKey,
                                updating, false
                            )
                        )
                        sBot.implementRequest(
                            deleteWaitMessageAction.map(JSONObject()),
                            deleteWaitMessageAction
                        )
                        sBot.implementRequest(
                            sendAudioAction.map(JSONObject()),
                            sendAudioAction
                        )
                    }
                }
            )
        } else {
            emptyList()
        }
    }
}