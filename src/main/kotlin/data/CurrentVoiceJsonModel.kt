package data

import org.json.JSONObject

class CurrentVoiceJsonModel(
    private val mBaseUrl: String
) : Voice.Mapper<JSONObject> {
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
    ) = JSONObject().apply {
        put("id", id)
        put("download_link", "${mBaseUrl}/api/download?user_id=${userId}&voice_id=${id}")
        put("title", title)
        put("duration", duration)
        put("saved_time", savedTime)
    }
}