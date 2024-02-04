package features.moderator.chains

import broadcast.BroadcastHandling
import broadcast.BroadcastMessageType
import broadcast.RawBroadcast
import chain.Chain
import core.Updating
import core.storage.Storages
import executables.EditTextMessage
import executables.Executable
import handlers.OnCallbackGotten
import keyboard_markup.InlineKeyboardMarkup
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import sBroadcastMessage
import translations.domain.ContextString
import users.User

class BeginBroadcast : Chain(OnCallbackGotten("beginBroadcast")) {

    override suspend fun executableChain(updating: Updating): List<Executable> {
        val broadcast = BroadcastHandling.Base.Instance()
        broadcast.setupBroadcast({ userId ->
            return@setupBroadcast runBlocking {
                inflateBroadcast(userId)
            }
        }, "")
        broadcast.launchBroadcast()
        return listOf(
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
    }

    private suspend fun inflateBroadcast(userId: Long): RawBroadcast {
        val updating = Updating(JSONObject().apply {
            put("message", JSONObject().apply {
                put("from", JSONObject().apply {
                    put(
                        "language_code",
                        Storages.Main.Provider().stUsersStorage.userById(userId)
                            .map(object : User.Mapper<String> {
                                override fun map(
                                    id: Long,
                                    username: String,
                                    firstName: String,
                                    secondName: String,
                                    languageCode: String,
                                    isPremium: Boolean,
                                    isActive: Boolean,
                                    joinDate: Long
                                ) = languageCode
                            })
                    )
                })
            })
        })
        return RawBroadcast(
            ContextString.Base.Strings().string(
                sBroadcastMessage,
                updating
            ),
            emptyList(),
            InlineKeyboardMarkup(
                listOf()
            ),
            "",
            BroadcastMessageType.Text
        )
    }
}