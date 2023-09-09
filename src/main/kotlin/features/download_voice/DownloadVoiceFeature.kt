package features.download_voice

import core.BotChains
import features.download_voice.chains.CancelSaving
import features.download_voice.chains.CatchTitle
import features.download_voice.chains.CatchVoice
import features.download_voice.chains.SkipName

class DownloadVoiceFeature : BotChains {

    override fun chains() = listOf(
        CatchVoice(),
        CatchTitle(),
        SkipName(),
        CancelSaving()
    )
}