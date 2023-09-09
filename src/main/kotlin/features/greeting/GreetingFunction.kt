package features.greeting

import core.BotChains
import features.greeting.chains.StartCommand

class GreetingFunction : BotChains {

    override fun chains() = listOf(
        StartCommand()
    )
}