package exception_handlers

import core.ExceptionHandler
import exception_handlers.custom_exceptions.UserIdNotFoundException
import io.ktor.server.application.*

class UserIdInvalidHandler : ExceptionHandler<UserIdNotFoundException> {

    override suspend fun handleException(call: ApplicationCall, cause: UserIdNotFoundException) {
        cause.respond(call)
    }
}