package features.editing_voices.edit_voice

import chain.Chain
import core.Updating
import data.VoiceNotFound
import data.VoiceStorage
import domain.messages.MessageMenu
import domain.messages.VoiceSubmitRenameMessage
import executables.AnswerToCallback
import executables.DeleteMessage
import executables.Executable
import handlers.OnCallbackDataGotten
import sErrorSkipLabel
import translations.domain.ContextString
import updating.UpdatingCallbackInt
import updating.UpdatingMessageId

class RenameVoice : Chain(OnCallbackDataGotten("renameVoice")) {
    override suspend fun executableChain(updating: Updating): List<Executable> {
        val voiceId = updating.map(UpdatingCallbackInt("renameVoice"))
        return try {
            val voice = VoiceStorage.Base.Instance().voiceById(voiceId.toLong())
            mStates.state(updating).editor(mStates).apply {
                putInt("waitForNewVoiceName", voiceId)
                putInt("nameEditingMessage", updating.map(UpdatingMessageId()).toInt())
            }.commit()
            listOf(
                AnswerToCallback(mKey),
                voice.map(VoiceSubmitRenameMessage(mKey, updating))
            )
        } catch (e: VoiceNotFound) {
            listOf(
                AnswerToCallback(
                    mKey, ContextString.Base.Strings().string(sErrorSkipLabel, updating),
                    true
                ),
                DeleteMessage(mKey, updating),
                MessageMenu.Base(mKey, updating).message()
            )
        }
    }
}