package audio_info

import core.Updating
import org.json.JSONException
import org.json.JSONObject

class UpdatingAudioDuration : Updating.Mapper<Int> {

    override fun map(updating: JSONObject): Int {
        return try {
            updating.getJSONObject("message")
                .getJSONObject("audio")
                .getInt("duration")
        } catch (e: JSONException) {
            0
        }
    }
}