package features.editing_voices

import core.BotChains
import features.editing_voices.delete_voice.CancelVoiceDeletion
import features.editing_voices.delete_voice.DeleteVoice
import features.editing_voices.delete_voice.SubmitVoiceDeletion

class EditingVoicesFeature : BotChains {
    override fun chains() = listOf(
        DeleteVoice(),
        CancelVoiceDeletion(),
        SubmitVoiceDeletion()
    )
}