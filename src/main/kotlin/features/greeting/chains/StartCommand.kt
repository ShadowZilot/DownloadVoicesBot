package features.greeting.chains

import chain.Chain
import core.Updating
import domain.MessageMenu
import executables.Executable
import executables.SendMessage
import handlers.CommandEvent
import helpers.ToMarkdownSupported
import logs.LogLevel
import logs.Logging
import updating.UpdatingChatId

class StartCommand : Chain(CommandEvent("/start")) {

    override suspend fun executableChain(updating: Updating): List<Executable> {
        return listOf(MessageMenu.Base(mKey, updating).message())
    }
}