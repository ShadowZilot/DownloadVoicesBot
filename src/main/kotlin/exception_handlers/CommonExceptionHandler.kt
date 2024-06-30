package exception_handlers

import core.ExceptionHandler
import domain.logExceptionToAll
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import logs.Logging

class CommonExceptionHandler : ExceptionHandler<Exception> {
    override suspend fun handleException(call: ApplicationCall, cause: Exception) {
        Logging.ConsoleLog.logExceptionToAll(cause)
        call.respond(
            HttpStatusCode.BadRequest,
            "Something went wrong. Try to change request and repeat"
        )
    }
}