package features.greeting.chains

import chain.Chain
import core.Updating
import executables.Executable
import executables.SendMessage
import handlers.CommandEvent
import sBroadcastMessage
import translations.domain.ContextString

class BroadcastTest : Chain(CommandEvent("/test")) {

    override suspend fun executableChain(updating: Updating): List<Executable> {
        return listOf(
            SendMessage(
                mKey,
                ContextString.Base.Strings().string(sBroadcastMessage, updating)
            )
        )
    }
}