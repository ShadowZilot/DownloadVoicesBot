package exception_handlers

import core.ExceptionHandler
import exception_handlers.custom_exceptions.VoiceAccessForbiddenException
import io.ktor.server.application.*
import logs.LogLevel
import logs.Logging

class VoiceAccessForbiddenHandler : ExceptionHandler<VoiceAccessForbiddenException> {

    override suspend fun handleException(call: ApplicationCall, cause: VoiceAccessForbiddenException) {
        val logText = "User with id = ${cause.userId} tried to get access to voice with id = ${cause.voiceId}"
        Logging.ConsoleLog.logToFile(logText, LogLevel.Warning)
        Logging.ConsoleLog.logToChat(logText, LogLevel.Warning)
        cause.respond(call)
    }
}