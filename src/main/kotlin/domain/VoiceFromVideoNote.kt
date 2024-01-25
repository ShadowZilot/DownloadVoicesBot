package domain

import core.Updating
import data.Voice
import helpers.FileUrl
import updating.UserIdUpdating

class VoiceFromVideoNote(
    private val mKey: String,
    private val mOgaId: String,
    private val mDuration: Int,
    private val mTitle: String = ""
) : VoiceFactory {

    override fun createVoiceEntity(updating: Updating): Voice {
        val voiceLink = FileUrl.Base(mKey, mOgaId).fileUrl()
        return Voice(
            -1L,
            mOgaId,
            "",
            updating.map(UserIdUpdating()),
            mTitle,
            voiceLink,
            mDuration,
            System.currentTimeMillis(),
            false
        )
    }
}