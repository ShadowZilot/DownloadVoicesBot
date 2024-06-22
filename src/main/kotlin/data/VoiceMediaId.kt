package data

typealias VoiceMediaPair = Pair<String, String>

class VoiceMediaId : Voice.Mapper<VoiceMediaPair> {

    override fun map(
        id: Long,
        fileOgaId: String,
        fileMp3Id: String,
        userId: Long,
        title: String,
        voiceLink: String,
        duration: Int,
        savedTime: Long,
        isDeleted: Boolean
    ): VoiceMediaPair {
        return if (fileOgaId.isEmpty()) {
            Pair(fileMp3Id, "mp3")
        } else {
            Pair(fileOgaId, "opus")
        }
    }
}