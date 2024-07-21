package features.download_voice

import chain.Chain
import core.FileIdFailed
import core.Updating
import domain.logExceptionToAll
import domain.messages.ContactDevMessage
import executables.Executable
import executables.SendMessage
import handlers.BotRecognizerEvent
import handlers.UnhandledEvent
import helpers.FileDownloadException
import logs.Logging
import org.json.JSONObject
import sVoiceIsTooBig
import translations.domain.ContextString

class ChainForCatchMedia(
    private val sourceChain: Chain
) : Chain(object : BotRecognizerEvent {
    override fun map(updating: JSONObject): JSONObject {
        val result = sourceChain.checkEvent(Updating(updating))
        return if (result) {
            updating
        } else {
            throw UnhandledEvent()
        }
    }
}) {

    override suspend fun executableChain(updating: Updating): List<Executable> {
        return try {
            sourceChain.executableChain(updating)
        } catch (e: FileIdFailed) {
            if (e.errorMessage == "Bad Request: file is too big") {
                listOf(
                    SendMessage(
                        mKey,
                        ContextString.Base.Strings().string(sVoiceIsTooBig, updating)
                    )
                )
            } else {
                Logging.ConsoleLog.logExceptionToAll(e)
                listOf(ContactDevMessage(mKey, updating))
            }
        } catch (e: FileDownloadException) {
            Logging.ConsoleLog.logExceptionToAll(e)
            listOf(ContactDevMessage(mKey, updating))
        }
    }
}