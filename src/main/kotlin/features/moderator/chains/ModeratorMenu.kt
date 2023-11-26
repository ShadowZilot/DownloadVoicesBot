package features.moderator.chains

import chain.Chain
import core.Updating
import executables.DeleteMessage
import executables.Executable
import executables.SendMessage
import handlers.CommandEvent
import helpers.convertToVertical
import keyboard_markup.InlineButton
import keyboard_markup.InlineKeyboardMarkup
import keyboard_markup.InlineModeQuery

class ModeratorMenu : Chain(CommandEvent("/secret")) {

    override suspend fun executableChain(updating: Updating): List<Executable> {
        return listOf(
            DeleteMessage(mKey, updating),
            SendMessage(
                mKey,
                buildString {
                    appendLine("Меню из ада")
                },
                InlineKeyboardMarkup(
                    listOf(
                        InlineButton(
                            "Голосовые",
                            mInlineMode = InlineModeQuery.CurrentChat("sec")
                        ),
                        InlineButton("Начать рассылку", mCallbackData = "initBroadcast")
                    ).convertToVertical()
                )
            )
        )
    }
}