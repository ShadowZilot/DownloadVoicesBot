package features.editing_voices.delete_voice

import chain.Chain
import core.Updating
import data.VoiceNotFound
import data.VoiceStorage
import domain.messages.EditVoiceCaptionToNormal
import domain.messages.MessageMenu
import executables.AnswerToCallback
import executables.DeleteMessage
import executables.Executable
import handlers.OnCallbackDataGotten
import sErrorSkipLabel
import translations.domain.ContextString.Base.Strings
import updating.UpdatingCallbackInt

class CancelVoiceDeletion : Chain(OnCallbackDataGotten("cancelDeleteVoice")) {

    override suspend fun executableChain(updating: Updating): List<Executable> {
        val voiceId = updating.map(UpdatingCallbackInt("cancelDeleteVoice"))
        return try {
            val voice = VoiceStorage.Base.Instance().voiceById(voiceId.toLong())
            listOf(
                AnswerToCallback(mKey),
                voice.map(EditVoiceCaptionToNormal(mKey, updating))
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