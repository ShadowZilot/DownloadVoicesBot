package features.poll.chains

import chain.Chain
import core.Updating
import executables.AnswerToCallback
import executables.DeleteMessage
import executables.Executable
import handlers.OnCallbackGotten

class HideSuggestionMessage : Chain(OnCallbackGotten("hideSuggestion")) {

    override suspend fun executableChain(updating: Updating): List<Executable> {
        mStates.state(updating).editor(mStates).apply {
            deleteValue("waitForSuggestion")
            deleteValue("pollMessageId")
            deleteValue("suggestionText")
        }.commit()
        return listOf(
            AnswerToCallback(mKey),
            DeleteMessage(mKey, updating)
        )
    }
}