package features.poll.chains

import chain.Chain
import core.Updating
import executables.DeleteMessage
import executables.Executable
import executables.SendMessage
import handlers.OnCallbackGotten
import helpers.ToMarkdownSupported
import helpers.convertToVertical
import keyboard_markup.InlineButton
import keyboard_markup.InlineKeyboardMarkup
import sCancelLabel
import sEditSuggestion
import sSubmitSuggestion
import translations.domain.ContextString
import updating.UserIdUpdating

class CancelSuggestionEditing : Chain(OnCallbackGotten("cancelEditingSuggestion")) {

    override suspend fun executableChain(updating: Updating): List<Executable> {
        mStates.state(updating).editor(mStates).apply {
            deleteValue("waitForSuggestion")
        }.commit()
        val suggestion = mStates.state(updating).string("suggestionText")
        return listOf(
            DeleteMessage(mKey, updating),
            SendMessage(
                mKey,
                ToMarkdownSupported.Base(suggestion).convertedString(),
                InlineKeyboardMarkup(
                    listOf(
                        InlineButton(
                            ContextString.Base.Strings().string(sSubmitSuggestion, updating),
                            mCallbackData = "submitSuggestion"
                        ),
                        InlineButton(
                            ContextString.Base.Strings().string(sEditSuggestion, updating),
                            mCallbackData = "editSuggestion"
                        ),
                        InlineButton(
                            ContextString.Base.Strings().string(sCancelLabel, updating),
                            mCallbackData = "hideSuggestion"
                        )
                    ).convertToVertical()
                )
            ) { messageId ->
                mStates.state(updating).editor(mStates).apply {
                    deleteValue("waitForSuggestion")
                    putInt("pollMessageId", messageId)
                    putString("suggestionText", suggestion)
                }.commit()
            }
        )
    }
}