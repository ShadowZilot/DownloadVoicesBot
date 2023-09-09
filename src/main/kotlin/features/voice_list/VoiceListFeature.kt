package features.voice_list

import core.BotChains
import features.voice_list.chains.*

class VoiceListFeature : BotChains {

    override fun chains() = listOf(
        SearchVoices(),
        SearchVoicesOtherChat(),
        NoVoicesDetail(),
        NotFoundVoicesDetail(),
        GoToVoice()
    )
}