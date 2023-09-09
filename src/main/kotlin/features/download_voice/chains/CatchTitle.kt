package features.download_voice.chains

import chain.Chain
import core.Updating
import data.VoiceStorage
import domain.VoiceToMessage
import executables.Executable
import executables.SendMessage
import handlers.OnTextGotten
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
            val voiceId = mStates.state(updating).int("waitForTitle")
            val newTitle = updating.map(UpdatingMessage())
            if (newTitle.length <= 128) {
                VoiceStorage.Base.Instance().updateVoiceTitle(voiceId.toLong(), newTitle)
                VoiceStorage.Base.Instance().updateVoiceDeletion(voiceId.toLong(), false)
                mStates.state(updating).editor(mStates).apply {
                    deleteValue("waitForTitle")
                }.commit()
                listOf(
                    VoiceStorage.Base.Instance().voiceById(voiceId.toLong()).map(
                        VoiceToMessage(mKey, updating, true)
                    )
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