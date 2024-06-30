package data

enum class VoiceStatus(val statusCode: Int) {
    NORMAL(0),
    DELETED(1),
    CREATING(2)
}