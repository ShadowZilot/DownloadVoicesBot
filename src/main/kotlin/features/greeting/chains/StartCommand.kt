package features.greeting.chains

import chain.Chain
import core.Updating
import domain.messages.MessageMenu
import executables.Executable
import handlers.CommandEvent

class StartCommand : Chain(CommandEvent("/start")) {

    override suspend fun executableChain(updating: Updating): List<Executable> {
        return listOf(MessageMenu.Base(mKey, updating).message())
    }
}