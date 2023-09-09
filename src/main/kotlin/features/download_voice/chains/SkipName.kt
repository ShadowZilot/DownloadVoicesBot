package features.download_voice.chains

import chain.Chain
import core.Updating
import data.VoiceNotFound
import data.VoiceStorage
import domain.VoiceToMessage
import executables.AnswerToCallback
import executables.DeleteMessage
import executables.Executable
import handlers.OnCallbackDataGotten
import sVoiceAlreadySaved
import translations.domain.ContextString
import updating.UpdatingCallbackInt

class SkipName : Chain(OnCallbackDataGotten("skipName")) {


    override suspend fun executableChain(updating: Updating): List<Executable> {
        val voiceId = updating.map(UpdatingCallbackInt("skipName"))
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
            VoiceStorage.Base.Instance().updateVoiceDeletion(voiceId.toLong(), false)
            mStates.state(updating).editor(mStates).apply {
                deleteValue("waitForTitle")
            }.commit()
            listOf(
                DeleteMessage(mKey, updating),
                VoiceStorage.Base.Instance().voiceById(voiceId.toLong()).map(
                    VoiceToMessage(mKey, updating, true)
                )
            )
        }
    }
}