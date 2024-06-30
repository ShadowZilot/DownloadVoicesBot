package exception_handlers.custom_exceptions

import io.ktor.http.*

class InvalidVoiceIdException(
    voiceIdParam: String
) : ServerException(HttpStatusCode.BadRequest, "Passed incorrect value = $voiceIdParam into voice_id query param")