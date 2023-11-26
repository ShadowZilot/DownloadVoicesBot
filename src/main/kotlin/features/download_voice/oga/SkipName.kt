package features.download_voice.oga

import chain.Chain
import core.Updating
import data.VoiceNotFound
import data.VoiceStorage
import domain.VoiceToMessage
import executables.AnswerToCallback
import executables.DeleteMessage
import executables.Executable
import handlers.OnCallbackDataGotten
import helpers.FileUrl
import sVoiceAlreadySaved
import translations.domain.ContextString
import updating.UpdatingCallbackInt

class SkipName : Chain(OnCallbackDataGotten("skipName")) {


    override suspend fun executableChain(updating: Updating): List<Executable> {
        val voiceId = updating.map(UpdatingCallbackInt("skipName")).toLong()
        val isAudio = mStates.state(updating).boolean("isAudio")
        return try {
            VoiceStorage.Base.Instance().voiceById(voiceId)
            listOf(
                AnswerToCallback(
                    mKey,
                    ContextString.Base.Strings().string(sVoiceAlreadySaved, updating),
                    true
                )
            )
        } catch (e: VoiceNotFound) {
            VoiceStorage.Base.Instance().updateVoiceDeletion(voiceId, false)
            mStates.state(updating).editor(mStates).apply {
                deleteValue("waitForTitle")
                deleteValue("isAudio")
            }.commit()
            listOf(
                DeleteMessage(mKey, updating),
                VoiceStorage.Base.Instance().voiceById(voiceId).map(
                    VoiceToMessage(mKey, updating, true, isAudio) { fileId ->
                        if (isAudio) {
                            VoiceStorage.Base.Instance().voiceOgaUpdateFileIdAndLink(
                                voiceId, fileId,
                                FileUrl.Base(mKey, fileId).fileUrl()
                            )
                        } else {
                            VoiceStorage.Base.Instance().voiceMp3UpdateFileIdAndLink(
                                voiceId, fileId,
                                FileUrl.Base(mKey, fileId).fileUrl()
                            )
                        }
                    }
                )
            )
        }
    }
}