package domain

import core.Updating
import executables.Executable
import executables.SendMessage
import helpers.convertToVertical
import keyboard_markup.InlineButton
import keyboard_markup.InlineKeyboardMarkup
import keyboard_markup.InlineModeQuery
import sDonateLabel
import sShareVoices
import sStartMessage
import sVoiceListLabel
import translations.domain.ContextString
import updating.UpdatingLanguageCode

interface MessageMenu {

    fun message(): Executable

    class Base(
        private val mKey: String,
        private val mUpdating: Updating
    ) : MessageMenu {

        override fun message(): Executable {
            return SendMessage(
                mKey,
                ContextString.Base.Strings().string(sStartMessage, mUpdating),
                InlineKeyboardMarkup(
                    listOf(
                        InlineButton(
                            ContextString.Base.Strings().string(sVoiceListLabel, mUpdating),
                            mInlineMode = InlineModeQuery.CurrentChat()
                        ),
                        InlineButton(
                            ContextString.Base.Strings().string(sShareVoices, mUpdating),
                            mInlineMode = InlineModeQuery.OtherChat()
                        )
                    ).convertToVertical()
                )
            )
        }
    }
}