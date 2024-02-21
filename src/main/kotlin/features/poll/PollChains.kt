package features.poll

import core.BotChains
import features.poll.chains.*

class PollChains : BotChains {

    override fun chains() = listOf(
        HideSuggestionMessage(),
        BeginInputSuggestion(),
        OnSuggestionGotten(),
        EditSuggestion(),
        CancelSuggestionEditing(),
        SubmitSuggestion()
    )
}