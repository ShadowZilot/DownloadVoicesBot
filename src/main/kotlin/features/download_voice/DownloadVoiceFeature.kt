package features.download_voice

import core.BotChains
import features.download_voice.mp3.CatchAudio
import features.download_voice.mp3.LeaveAudioName
import features.download_voice.oga.CancelSaving
import features.download_voice.oga.CatchTitle
import features.download_voice.oga.CatchVoice
import features.download_voice.oga.SkipName
import features.download_voice.video_notes.CatchVideoName
import features.download_voice.video_notes.CatchVideoNote
import features.download_voice.video_notes.SkipVideoName

class DownloadVoiceFeature : BotChains {

    override fun chains() = listOf(
        CatchVoice(),
        CatchTitle(),
        SkipName(),
        CancelSaving(),
        CatchAudio(),
        LeaveAudioName(),
        CatchVideoNote(),
        CatchVideoName(),
        SkipVideoName()
    )
}