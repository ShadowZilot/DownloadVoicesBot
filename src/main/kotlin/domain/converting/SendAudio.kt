package domain.converting

import core.Updating
import executables.Executable
import keyboard_markup.KeyboardMarkup
import logs.Logging
import okhttp3.Call
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import org.json.JSONObject
import updating.UpdatingChatId

class SendAudioCustom(
    private val mKey: String,
    private val mTrackName: String,
    private val mExtension: String,
    private val mCaption: String,
    private val mDuration: Int,
    private val mFile: ByteArray? = null,
    private val mFileId: String = "",
    private val mMarkup: KeyboardMarkup = KeyboardMarkup.Dummy(),
    private val mChatId: Long = -1,
    private val mOnFileId: (fileId: String) -> Unit = {}
) : Executable {

    override fun onFailure(call: Call, e: IOException) {
        Logging.ConsoleLog.log("Error while sending voice, message = ${e.message}")
    }

    override fun onResponse(call: Call, response: Response) {
        val body = JSONObject(response.body?.string())
        if (!response.isSuccessful) {
            Logging.ConsoleLog.log(body.toString(2))
        } else {
            val resultBody = body.getJSONObject("result")
            val fileId = if (resultBody.has("audio")) {
                resultBody.getJSONObject("audio")
                    .getString("file_id")
            } else if (resultBody.has("voice")) {
                resultBody.getJSONObject("voice")
                    .getString("file_id")
            } else {
                Logging.ConsoleLog.log("Unknown send voice result body!")
                ""
            }
            mOnFileId.invoke(fileId)
        }
        response.close()
    }

    override fun map(updating: JSONObject): Request {
        val endpoint = if (mExtension == "opus") "sendVoice" else "sendAudio"
        val valueName = if (mExtension == "opus") "voice" else "audio"
        return Request.Builder()
            .post(
                MultipartBody.Builder().apply {
                    setType(MultipartBody.FORM)
                    addFormDataPart(
                        "chat_id", if (mChatId == -1L) {
                            Updating(updating).map(
                                UpdatingChatId()
                            ).second.toString()
                        } else {
                            mChatId.toString()
                        }
                    )
                    if (mFile != null) {
                        addFormDataPart(
                            valueName, if (mExtension == "opus") mTrackName else "$mTrackName.$mExtension",
                            mFile.toRequestBody(null)
                        )
                    } else {
                        addFormDataPart("audio", mFileId)
                    }
                    addFormDataPart("duration", mDuration.toString())
                    addFormDataPart("title", mTrackName)
                    addFormDataPart("caption", mCaption)
                    addFormDataPart("parse_mode", "MarkdownV2")
                    addFormDataPart("reply_markup", mMarkup.filed().toString(2))
                }.build()
            )
            .url("https://api.telegram.org/bot${mKey}/$endpoint")
            .build()
    }
}