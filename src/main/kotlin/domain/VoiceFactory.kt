package domain

import audio_info.UpdatingAudioDuration
import audio_info.UpdatingAudioFileId
import audio_info.UpdatingAudioTitle
import core.Updating
import data.Voice
import helpers.FileUrl
import updating.UpdatingVoiceDuration
import updating.UpdatingVoiceFileId
import updating.UserIdUpdating

interface VoiceFactory {

    fun createVoiceEntity(updating: Updating): Voice

    class Base(
        private val mKey: String,
        private val mIsAudio: Boolean
    ) : VoiceFactory {

        override fun createVoiceEntity(updating: Updating): Voice {
            val fileId = if (mIsAudio) updating.map(UpdatingAudioFileId())
            else updating.map(UpdatingVoiceFileId())
            val voiceLink = FileUrl.Base(mKey, fileId).fileUrl()
            return Voice(
                -1L,
                if (mIsAudio) "" else fileId,
                if (mIsAudio) fileId else "",
                updating.map(UserIdUpdating()),
                if (mIsAudio) updating.map(UpdatingAudioTitle()) else "",
                voiceLink,
                if (mIsAudio) updating.map(UpdatingAudioDuration()) else updating.map(UpdatingVoiceDuration()),
                System.currentTimeMillis(),
                true
            )
        }
    }
}