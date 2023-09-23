package features.moderator

import admin_bot_functions.base.IsUserAdmin
import chain.blocking.ChainBlocking
import core.BotChains
import core.storage.Storages
import features.moderator.chains.GoToVoiceSecret
import features.moderator.chains.ModeratorMenu

class ModeratorFeature : BotChains {

    override fun chains() = listOf(
        ModeratorMenu(),
        GoToVoiceSecret()
    ).map {
        ChainBlocking(
            it,
            IsUserAdmin(Storages.Main.Provider().stAdmins)
        )
    }
}