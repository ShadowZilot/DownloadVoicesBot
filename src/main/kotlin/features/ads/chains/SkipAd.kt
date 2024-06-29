package features.ads.chains

import chain.Chain
import core.Updating
import executables.AnswerToCallback
import executables.Executable
import executables.SendMessage
import handlers.OnCallbackDataGotten
import helpers.convertToVertical
import keyboard_markup.InlineButton
import keyboard_markup.InlineKeyboardMarkup
import sCancelLabel
import sSkipTitleLabel
import sTitleSuggestion
import translations.domain.ContextString
import updating.UpdatingCallbackInt

class SkipAd : Chain(OnCallbackDataGotten("skipAd")) {

    override suspend fun executableChain(updating: Updating): List<Executable> {
        val voiceId = updating.map(UpdatingCallbackInt("skipAd"))
        return listOf(
            AnswerToCallback(mKey),
            SendMessage(
                mKey,
                ContextString.Base.Strings().string(sTitleSuggestion, updating),
                InlineKeyboardMarkup(
                    listOf(
                        InlineButton(
                            ContextString.Base.Strings().string(sSkipTitleLabel, updating),
                            mCallbackData = "skipName=$voiceId"
                        ),
                        InlineButton(
                            ContextString.Base.Strings().string(sCancelLabel, updating),
                            mCallbackData = "cancelSaving=$voiceId"
                        ),
                    ).convertToVertical()
                )
            ) {
                mStates.state(updating).editor(mStates).apply {
                    putInt("waitForTitle", voiceId)
                    deleteValue("waitForNewVoiceName")
                    deleteValue("nameEditingMessage")
                }.commit()
            }
        )
    }
}