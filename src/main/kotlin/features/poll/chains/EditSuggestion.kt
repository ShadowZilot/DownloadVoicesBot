package features.poll.chains

import chain.Chain
import core.Updating
import executables.AnswerToCallback
import executables.DeleteMessage
import executables.Executable
import executables.SendMessage
import handlers.OnCallbackGotten
import helpers.convertToVertical
import keyboard_markup.InlineButton
import keyboard_markup.InlineKeyboardMarkup
import sCancelLabel
import sSuggestionHelperText
import translations.domain.ContextString

class EditSuggestion : Chain(OnCallbackGotten("editSuggestion")) {

    override suspend fun executableChain(updating: Updating): List<Executable> {
        return listOf(
            AnswerToCallback(mKey),
            DeleteMessage(mKey, updating),
            SendMessage(
                mKey,
                ContextString.Base.Strings().string(sSuggestionHelperText, updating),
                InlineKeyboardMarkup(
                    listOf(
                        InlineButton(
                            ContextString.Base.Strings().string(sCancelLabel, updating),
                            mCallbackData = "cancelEditingSuggestion"
                        )
                    ).convertToVertical()
                )
            ) { messageId ->
                mStates.state(updating).editor(mStates).apply {
                    putBoolean("waitForSuggestion", true)
                    putInt("pollMessageId", messageId)
                }.commit()
            }
        )
    }
}