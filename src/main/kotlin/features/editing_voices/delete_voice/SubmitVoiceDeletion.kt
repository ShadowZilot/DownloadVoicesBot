package features.editing_voices.delete_voice

import chain.Chain
import core.Updating
import data.VoiceNotFound
import data.VoiceStorage
import domain.messages.MessageMenu
import executables.AnswerToCallback
import executables.DeleteMessage
import executables.Executable
import handlers.OnCallbackDataGotten
import sErrorSkipLabel
import sVoiceRemovedLabel
import translations.domain.ContextString.Base.Strings
import updating.UpdatingCallbackInt

class SubmitVoiceDeletion : Chain(OnCallbackDataGotten("submitDeleteVoice")) {
    override suspend fun executableChain(updating: Updating): List<Executable> {
        val voiceId = updating.map(UpdatingCallbackInt("submitDeleteVoice"))
        return try {
            VoiceStorage.Base.Instance().voiceById(voiceId.toLong())
            VoiceStorage.Base.Instance().deleteVoice(voiceId.toLong())
            listOf(
                AnswerToCallback(mKey, Strings().string(sVoiceRemovedLabel, updating), true),
                DeleteMessage(mKey, updating),
                MessageMenu.Base(mKey, updating).message()
            )
        } catch (e: VoiceNotFound) {
            listOf(
                AnswerToCallback(mKey, Strings().string(sErrorSkipLabel, updating), true),
                DeleteMessage(mKey, updating),
                MessageMenu.Base(mKey, updating).message()
            )
        }
    }
}