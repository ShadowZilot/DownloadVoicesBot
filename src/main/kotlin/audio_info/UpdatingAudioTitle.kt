package audio_info

import core.Updating
import org.json.JSONException
import org.json.JSONObject

class UpdatingAudioTitle : Updating.Mapper<String> {

    override fun map(updating: JSONObject): String {
        return try {
            val audio = updating.getJSONObject("message")
                .getJSONObject("audio")
            if (audio.has("title")) {
                audio.getString("title")
            } else {
                audio.getString("file_name")
            }
        } catch (e: JSONException) {
            ""
        }
    }
}