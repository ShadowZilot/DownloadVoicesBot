package api.utils

import exception_handlers.custom_exceptions.InvalidUserIdException
import exception_handlers.custom_exceptions.UserIdNotFoundException
import exception_handlers.custom_exceptions.VoiceIdNotFoundException
import io.ktor.server.application.*

fun ApplicationCall.getUserId(): Long {
    val userId = parameters["user_id"]
    return if (userId == null) {
        throw UserIdNotFoundException()
    } else {
        try {
            userId.toLong()
        } catch (e: NumberFormatException) {
            throw InvalidUserIdException(userId)
        }
    }
}

fun ApplicationCall.getVoiceId(): Long {
    val userId = parameters["voice_id"]
    return if (userId == null) {
        throw VoiceIdNotFoundException()
    } else {
        try {
            userId.toLong()
        } catch (e: NumberFormatException) {
            throw InvalidUserIdException(userId)
        }
    }
}