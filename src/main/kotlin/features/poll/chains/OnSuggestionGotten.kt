package features.poll.chains

import chain.Chain
import core.Updating
import executables.DeleteMessage
import executables.Executable
import executables.SendMessage
import handlers.OnTextGotten
import helpers.ToMarkdownSupported
import helpers.convertToVertical
import keyboard_markup.InlineButton
import keyboard_markup.InlineKeyboardMarkup
import sCancelLabel
import sEditSuggestion
import sSubmitSuggestion
import staging.safetyBoolean
import translations.domain.ContextString
import updating.UpdatingMessage
import updating.UserIdUpdating

class OnSuggestionGotten : Chain(OnTextGotten()) {

    override suspend fun executableChain(updating: Updating): List<Executable> {
        return if (mStates.state(updating).safetyBoolean("waitForSuggestion")) {
            val pollMessageId = mStates.state(updating).int("pollMessageId")
            val suggestion = updating.map(UpdatingMessage())
            listOf(
                DeleteMessage(mKey, updating),
                DeleteMessage(mKey, updating.map(UserIdUpdating()).toString(), pollMessageId.toLong()),
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
        } else {
            emptyList()
        }
    }
}