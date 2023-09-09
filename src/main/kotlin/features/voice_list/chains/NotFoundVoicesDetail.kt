package features.voice_list.chains

import chain.Chain
import core.Updating
import executables.DeleteMessage
import executables.Executable
import executables.SendMessage
import handlers.OnTextViaBot
import helpers.convertToVertical
import keyboard_markup.InlineButton
import keyboard_markup.InlineKeyboardMarkup
import keyboard_markup.InlineModeQuery
import sVoiceListLabel
import sVoicesNotFoundDescription
import sVoicesNotFoundTitle
import translations.domain.ContextString.Base.Strings

class NotFoundVoicesDetail : Chain(OnTextViaBot("voicesNotFound")) {

    override suspend fun executableChain(updating: Updating): List<Executable> {
        return listOf(
            DeleteMessage(mKey, updating),
            SendMessage(
                mKey,
                buildString {
                    appendLine("*${Strings().string(sVoicesNotFoundTitle, updating)}*")
                    appendLine()
                    appendLine(Strings().string(sVoicesNotFoundDescription, updating))
                },
                InlineKeyboardMarkup(
                    listOf(
                        InlineButton(
                            Strings().string(sVoiceListLabel, updating),
                            mInlineMode = InlineModeQuery.CurrentChat()
                        )
                    ).convertToVertical()
                )
            )
        )
    }
}