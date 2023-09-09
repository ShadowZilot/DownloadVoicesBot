package features.voice_list

import core.BotChains
import features.voice_list.chains.GoToVoice
import features.voice_list.chains.NoVoicesDetail
import features.voice_list.chains.NotFoundVoicesDetail
import features.voice_list.chains.SearchVoices

class VoiceListFeature : BotChains {

    override fun chains() = listOf(
        SearchVoices(),
        NoVoicesDetail(),
        NotFoundVoicesDetail(),
        GoToVoice()
    )
}