package domain

import core.Updating
import data.poll.PollStorage
import executables.Executable
import executables.SendMessage
import helpers.convertToVertical
import keyboard_markup.InlineButton
import keyboard_markup.InlineKeyboardMarkup
import sHideSuggestion
import sSuggestionButton
import sSuggestionMessage
import translations.domain.ContextString.Base.Strings
import updating.UserIdUpdating

interface SuggestionMessage {

    fun message(): Executable

    class Base(
        private val mKey: String,
        private val mUpdating: Updating
    ) : SuggestionMessage {
        override fun message(): Executable {
            val userId = mUpdating.map(UserIdUpdating())
            val isUserPolled = PollStorage.Base.Instance().isUserPolled(userId)
            return if (!isUserPolled) {
                SendMessage(
                    mKey,
                    Strings().string(sSuggestionMessage, mUpdating),
                    InlineKeyboardMarkup(
                        listOf(
                            InlineButton(
                                Strings().string(sSuggestionButton, mUpdating),
                                mCallbackData = "beginInputSuggestion"
                            ),
                            InlineButton(
                                Strings().string(sHideSuggestion, mUpdating),
                                mCallbackData = "hideSuggestion"
                            )
                        ).convertToVertical()
                    )
                )
            } else {
                Executable.Dummy()
            }
        }
    }
}