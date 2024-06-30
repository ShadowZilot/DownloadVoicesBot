package exception_handlers.custom_exceptions

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

abstract class ServerException(
    private val statusCode: HttpStatusCode,
    private val messageStr: String
) : Throwable(messageStr) {

    suspend fun respond(call: ApplicationCall) {
        call.respond(statusCode, messageStr)
    }
}