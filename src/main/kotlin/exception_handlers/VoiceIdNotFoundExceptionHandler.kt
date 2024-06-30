package exception_handlers

import core.ExceptionHandler
import exception_handlers.custom_exceptions.VoiceIdNotFoundException
import io.ktor.server.application.*

class VoiceIdNotFoundExceptionHandler : ExceptionHandler<VoiceIdNotFoundException> {
    override suspend fun handleException(call: ApplicationCall, cause: VoiceIdNotFoundException) {
        cause.respond(call)
    }
}