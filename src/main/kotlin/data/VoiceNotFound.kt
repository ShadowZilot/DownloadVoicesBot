package data

class VoiceNotFound(
    val id: Long
) : Exception("Voice with id = $id doesn't not found!")