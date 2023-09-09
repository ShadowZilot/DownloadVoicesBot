package domain

import core.Updating
import data.Voice
import helpers.FileUrl
import updating.UpdatingVoiceDuration
import updating.UpdatingVoiceFileId
import updating.UserIdUpdating

interface VoiceFactory {

    fun createVoiceEntity(updating: Updating) : Voice

    class Base(
        private val mKey: String
    ) : VoiceFactory {

        override fun createVoiceEntity(updating: Updating): Voice {
            val fileId = updating.map(UpdatingVoiceFileId())
            val voiceLink = FileUrl.Base(mKey, fileId).fileUrl()
            return Voice(
                -1L,
                fileId,
                updating.map(UserIdUpdating()),
                "",
                voiceLink,
                updating.map(UpdatingVoiceDuration()),
                System.currentTimeMillis(),
                true
            )
        }
    }
}