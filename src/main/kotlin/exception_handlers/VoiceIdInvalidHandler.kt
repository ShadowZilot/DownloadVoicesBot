package exception_handlers

import core.ExceptionHandler
import exception_handlers.custom_exceptions.InvalidVoiceIdException
import io.ktor.server.application.*

class VoiceIdInvalidHandler : ExceptionHandler<InvalidVoiceIdException> {
    override suspend fun handleException(call: ApplicationCall, cause: InvalidVoiceIdException) {
        cause.respond(call)
    }
}