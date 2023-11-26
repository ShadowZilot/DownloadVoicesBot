package features.download_voice.oga

import chain.Chain
import core.Updating
import data.VoiceNotFound
import data.VoiceStorage
import domain.MessageMenu
import executables.AnswerToCallback
import executables.DeleteMessage
import executables.EditTextMessage
import executables.Executable
import handlers.OnCallbackDataGotten
import sVoiceAlreadySaved
import sVoiceSavingCanceled
import translations.domain.ContextString
import updating.UpdatingCallbackInt

class CancelSaving : Chain(OnCallbackDataGotten("cancelSaving")) {

    override suspend fun executableChain(updating: Updating): List<Executable> {
        val voiceId = updating.map(UpdatingCallbackInt("cancelSaving"))
        return try {
            VoiceStorage.Base.Instance().voiceById(voiceId.toLong())
            listOf(
                AnswerToCallback(
                    mKey,
                    ContextString.Base.Strings().string(sVoiceAlreadySaved, updating),
                    true
                )
            )
        } catch (e: VoiceNotFound) {
            mStates.state(updating).editor(mStates).apply {
                deleteValue("waitForTitle")
                deleteValue("isAudio")
            }.commit()
            listOf(
                AnswerToCallback(
                    mKey,
                    ContextString.Base.Strings().string(sVoiceSavingCanceled, updating),
                    true
                ),
                DeleteMessage(mKey, updating),
                MessageMenu.Base(mKey, updating).message()
            )
        }
    }
}