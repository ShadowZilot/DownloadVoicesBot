package exception_handlers.custom_exceptions

import io.ktor.http.*

class VoiceAccessForbiddenException(
    val userId: Long,
    val voiceId: Long
) : ServerException(
    HttpStatusCode.Forbidden,
    "You don't have access to this voice"
)