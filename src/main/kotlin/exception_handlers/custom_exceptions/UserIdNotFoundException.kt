package exception_handlers.custom_exceptions

import io.ktor.http.*

class UserIdNotFoundException : ServerException(
    HttpStatusCode.BadRequest, "User id query param doesn't found"
)