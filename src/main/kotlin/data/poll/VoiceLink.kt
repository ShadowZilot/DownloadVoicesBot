package data.poll

import data.Voice
import data.VoiceStatus

class VoiceLink : Voice.Mapper<String> {
    override fun map(
        id: Long,
        fileOgaId: String,
        fileMp3Id: String,
        userId: Long,
        title: String,
        voiceLink: String,
        duration: Int,
        savedTime: Long,
        voiceStatus: VoiceStatus
    ) = voiceLink
}