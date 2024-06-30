package exception_handlers.custom_exceptions

import io.ktor.http.*

class VoiceIdNotFoundException : ServerException(
    HttpStatusCode.BadRequest,
    "Voice id doesn't found in query params"
)