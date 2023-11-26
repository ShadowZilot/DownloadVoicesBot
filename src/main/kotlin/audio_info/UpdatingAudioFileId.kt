package audio_info

import core.Updating
import org.json.JSONException
import org.json.JSONObject

class UpdatingAudioFileId : Updating.Mapper<String> {

    override fun map(updating: JSONObject): String {
        return try {
            updating.getJSONObject("message")
                .getJSONObject("audio")
                .getString("file_id")
        } catch (e: JSONException) {
            ""
        }
    }
}