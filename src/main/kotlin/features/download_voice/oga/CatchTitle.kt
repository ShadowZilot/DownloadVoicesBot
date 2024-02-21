package features.download_voice.oga

import chain.Chain
import core.Updating
import data.VoiceStorage
import domain.SuggestionMessage
import domain.VoiceToMessage
import executables.Executable
import executables.SendMessage
import handlers.OnTextGotten
import helpers.FileUrl
import helpers.convertToVertical
import keyboard_markup.InlineButton
import keyboard_markup.InlineKeyboardMarkup
import sCancelLabel
import sSkipTitleLabel
import sTooLongName
import staging.NotFoundStateValue
import translations.domain.ContextString.Base.Strings
import updating.UpdatingMessage

class CatchTitle : Chain(OnTextGotten()) {

    override suspend fun executableChain(updating: Updating): List<Executable> {
        return try {
            val voiceId = mStates.state(updating).int("waitForTitle").toLong()
            val newTitle = updating.map(UpdatingMessage())
            val isAudio = mStates.state(updating).boolean("isAudio")
            if (newTitle.length <= 128) {
                VoiceStorage.Base.Instance().updateVoiceTitle(voiceId, newTitle)
                VoiceStorage.Base.Instance().updateVoiceDeletion(voiceId, false)
                mStates.state(updating).editor(mStates).apply {
                    deleteValue("waitForTitle")
                    deleteValue("isAudio")
                }.commit()
                listOf(
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
                    ),
                    SuggestionMessage.Base(mKey, updating).message()
                )
            } else {
                listOf(
                    SendMessage(
                        mKey,
                        Strings().string(sTooLongName, updating),
                        InlineKeyboardMarkup(
                            listOf(
                                InlineButton(
                                    Strings().string(sSkipTitleLabel, updating),
                                    mCallbackData = "skipName=$voiceId"
                                ),
                                InlineButton(
                                    Strings().string(sCancelLabel, updating),
                                    mCallbackData = "cancelSaving=$voiceId"
                                ),
                            ).convertToVertical()
                        )
                    )
                )
            }
        } catch (e: NotFoundStateValue) {
            emptyList()
        }
    }
}