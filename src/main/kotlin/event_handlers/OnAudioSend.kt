package event_handlers

import handlers.BotRecognizerEvent
import handlers.UnhandledEvent
import org.json.JSONException
import org.json.JSONObject

class OnAudioSend : BotRecognizerEvent {

    override fun map(updating: JSONObject): JSONObject {
        return try {
            updating.getJSONObject("message").getJSONObject("audio")
            updating
        } catch (e: JSONException) {
            throw UnhandledEvent()
        }
    }
}