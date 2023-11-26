package features.moderator.chains

import broadcast.BroadcastHandling
import chain.Chain
import core.Updating
import executables.EditTextMessage
import executables.Executable
import executables.SendMessage
import handlers.OnCallbackGotten
import helpers.convertToVertical
import keyboard_markup.InlineButton
import keyboard_markup.InlineKeyboardMarkup

class InitBroadcast : Chain(OnCallbackGotten("initBroadcast")) {

    override suspend fun executableChain(updating: Updating): List<Executable> {
        val broadcast = BroadcastHandling.Base.Instance()
        return if (broadcast.isBroadcastRunning()) {
            listOf(
                EditTextMessage(
                    mKey,
                    buildString {
                        appendLine("*Статистика рассылки*")
                        appendLine()
                        if (broadcast.isBroadcastRunning()) {
                            appendLine()
                            appendLine("*Информация о рассылке*")
                            appendLine()
                            val info = broadcast.broadcastProgress()
                            appendLine("Ожидаемое количество: *${info.first}*")
                            appendLine("Отправлено сообщений: *${info.third}*")
                            appendLine("Доставлено сообщений: *${info.second}*")
                        }
                    }
                )
            )
        } else {
            listOf(
                EditTextMessage(
                    mKey,
                    buildString {
                        appendLine("Подтвердите начало рассылки")
                    },
                    -1,
                    InlineKeyboardMarkup(
                        listOf(
                            InlineButton("Начать", mCallbackData = "beginBroadcast")
                        ).convertToVertical()
                    )
                )
            )
        }
    }
}