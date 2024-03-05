package data

import org.json.JSONObject

class VoiceToJsonResponse : Voice.Mapper<JSONObject> {
    override fun map(
        id: Long,
        fileOgaId: String,
        fileMp3Id: String,
        userId: Long,
        title: String,
        voiceLink: String,
        duration: Int,
        savedTime: Long,
        isDeleted: Boolean
    ) = JSONObject().apply {
        put("id", id)
        put("title", title)
        put("duration", duration)
        put("saved_time", savedTime)
    }
}