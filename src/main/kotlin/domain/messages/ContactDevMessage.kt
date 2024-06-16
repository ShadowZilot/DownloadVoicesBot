package domain.messages

import core.Updating
import executables.Executable
import executables.SendMessage
import helpers.convertToVertical
import keyboard_markup.InlineButton
import keyboard_markup.InlineKeyboardMarkup
import sConvertingErrorBtnLabel
import sConvertingErrorMessage
import translations.domain.ContextString

class ContactDevMessage(
    private val sendMessage: SendMessage
) : Executable by sendMessage {

    constructor(key: String, updating: Updating) : this(
        SendMessage(
            key,
            ContextString.Base.Strings.invoke().string(sConvertingErrorMessage, updating),
            InlineKeyboardMarkup(
                listOf(
                    InlineButton(
                        ContextString.Base.Strings.invoke().string(sConvertingErrorBtnLabel, updating),
                        "https://t.me/ShadowZilot"
                    )
                ).convertToVertical()
            )
        )
    )
}