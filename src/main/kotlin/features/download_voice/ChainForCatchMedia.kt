package features.download_voice

import chain.Chain
import core.FileIdFailed
import core.Updating
import domain.logExceptionToAll
import domain.messages.ContactDevMessage
import executables.Executable
import handlers.BotRecognizerEvent
import handlers.UnhandledEvent
import helpers.FileDownloadException
import logs.Logging
import org.json.JSONObject

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
            Logging.ConsoleLog.logExceptionToAll(e)
            listOf(ContactDevMessage(mKey, updating))
        } catch (e: FileDownloadException) {
            Logging.ConsoleLog.logExceptionToAll(e)
            listOf(ContactDevMessage(mKey, updating))
        }
    }
}