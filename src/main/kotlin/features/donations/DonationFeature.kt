package features.donations

import core.BotChains
import features.donations.chains.DonationCommand

class DonationFeature : BotChains {

    override fun chains() = listOf(DonationCommand())
}