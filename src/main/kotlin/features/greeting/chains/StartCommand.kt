package features.greeting.chains

import chain.Chain
import core.Updating
import domain.MessageMenu
import executables.Executable
import handlers.CommandEvent
import logs.Logging

class StartCommand : Chain(CommandEvent("/start")) {

    override suspend fun executableChain(updating: Updating): List<Executable> {
        return try {
            listOf(MessageMenu.Base(mKey, updating).message())
        } catch (e: Exception) {
            Logging.ConsoleLog.log(e.message ?: "Some error")
            emptyList()
        }
    }
}