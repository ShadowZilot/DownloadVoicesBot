package exception_handlers.custom_exceptions

import io.ktor.http.*

class InvalidUserIdException(
    userIdParam: String
) : ServerException(HttpStatusCode.BadRequest, "Passed incorrect value = $userIdParam into user_id query param")