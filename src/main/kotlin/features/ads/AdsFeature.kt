package features.ads

import core.BotChains
import features.ads.chains.SkipAd

class AdsFeature : BotChains {

    override fun chains() = listOf(
        SkipAd()
    )
}