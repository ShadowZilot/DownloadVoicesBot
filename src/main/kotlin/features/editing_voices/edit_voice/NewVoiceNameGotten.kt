package features.editing_voices.edit_voice

import chain.Chain
import core.Updating
import core.storage.Storages
import data.VoiceMediaId
import data.VoiceNotFound
import data.VoiceStorage
import domain.messages.MessageMenu
import domain.messages.NewNameVoiceMessage
import domain.messages.VoiceTooLongNewNameMessage
import executables.DeleteMessage
import executables.Executable
import handlers.OnTextGotten
import helpers.FileUrl
import staging.safetyInt
import updating.UpdatingMessage
import updating.UserIdUpdating

class NewVoiceNameGotten : Chain(OnTextGotten()) {
    override suspend fun executableChain(updating: Updating): List<Executable> {
        val voiceId = mStates.state(updating).safetyInt("waitForNewVoiceName")
        return if (voiceId != -1) {
            val messageId = mStates.state(updating).safetyInt("nameEditingMessage")
            try {
                val voice = VoiceStorage.Base.Instance().voiceById(voiceId.toLong())
                val newName = updating.map(UpdatingMessage())
                if (newName.length <= Storages.Main.Provider().stConfig.configValueLong("maxVoiceNameLen")) {
                    mStates.state(updating).editor(mStates).apply {
                        deleteValue("waitForNewVoiceName")
                        deleteValue("nameEditingMessage")
                    }.commit()
                    VoiceStorage.Base.Instance().updateVoiceName(voiceId.toLong(), newName)
                    val voiceWithNewName = VoiceStorage.Base.Instance().voiceById(voiceId.toLong())
                    val mediaPair = voiceWithNewName.map(VoiceMediaId())
                    listOf(
                        DeleteMessage(mKey, updating),
                        DeleteMessage(mKey, updating.map(UserIdUpdating()).toString(), messageId.toLong()),
                        voiceWithNewName.map(NewNameVoiceMessage(mKey, updating, mediaPair) { fileId ->
                            VoiceStorage.Base.Instance().voiceMp3IdUpdate(voiceId.toLong(), fileId)
                        })
                    )
                } else {
                    listOf(
                        DeleteMessage(mKey, updating),
                        voice.map(VoiceTooLongNewNameMessage(mKey, updating, messageId))
                    )
                }
            } catch (e: VoiceNotFound) {
                mStates.state(updating).editor(mStates).apply {
                    deleteValue("waitForNewVoiceName")
                    deleteValue("nameEditingMessage")
                }.commit()
                listOf(
                    DeleteMessage(mKey, updating),
                    MessageMenu.Base(mKey, updating).message()
                )
            }
        } else {
            emptyList()
        }
    }
}