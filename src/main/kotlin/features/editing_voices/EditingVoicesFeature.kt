package features.editing_voices

import core.BotChains
import features.editing_voices.delete_voice.CancelVoiceDeletion
import features.editing_voices.delete_voice.DeleteVoice
import features.editing_voices.delete_voice.SubmitVoiceDeletion
import features.editing_voices.edit_voice.CancelRenameVoice
import features.editing_voices.edit_voice.NewVoiceNameGotten
import features.editing_voices.edit_voice.RenameVoice

class EditingVoicesFeature : BotChains {
    override fun chains() = listOf(
        DeleteVoice(),
        CancelVoiceDeletion(),
        SubmitVoiceDeletion(),
        RenameVoice(),
        CancelRenameVoice(),
        NewVoiceNameGotten()
    )
}