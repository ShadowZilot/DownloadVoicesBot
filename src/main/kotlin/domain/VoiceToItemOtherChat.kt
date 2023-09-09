package domain

import core.Updating
import data.Voice
import sDownloadVoiceLink
import sEmptyTitle
import translations.domain.ContextString.Base.Strings

class VoiceToItemOtherChat(
    private val mUpdating: Updating,
    private val mBotName: String
) : Voice.Mapper<InlineQueryResultCachedAudio> {

    override fun map(
        id: Long,
        fileId: String,
        userId: Long,
        title: String,
        voiceLink: String,
        duration: Int,
        savedTime: Long,
        isDeleted: Boolean
    ): InlineQueryResultCachedAudio {
        return InlineQueryResultCachedAudio(
            id.toInt(),
            fileId,
            "\uD83C\uDFB6 ${title.ifEmpty { Strings().string(sEmptyTitle, mUpdating, id) }}",
            "[${Strings().string(sDownloadVoiceLink, mUpdating)}](https://t.me/$mBotName)"
        )
    }
}