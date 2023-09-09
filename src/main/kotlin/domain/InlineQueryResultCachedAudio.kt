package domain

import inline_query_result.InlineQueryResult
import inline_query_result.content.InputMessageContent
import keyboard_markup.KeyboardMarkup
import org.json.JSONObject

class InlineQueryResultCachedAudio(
    private val mId: Int,
    private val mAudioId: String,
    private val mTitle: String,
    private val mCaption: String = "",
    private val mContent: InputMessageContent = InputMessageContent.Dummy(),
    private val mMarkup: KeyboardMarkup = KeyboardMarkup.Dummy()
) : InlineQueryResult {

    override fun serialized() = JSONObject().apply {
        put("type", "voice")
        put("id", mId)
        put("voice_file_id", mAudioId)
        put("title", mTitle)
        put("caption", mCaption)
        put("parse_mode", "MarkdownV2")
        put("reply_markup", mMarkup.filed())
        put("input_message_content", mContent.serialized())
    }
}