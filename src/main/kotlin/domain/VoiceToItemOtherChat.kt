package domain

import core.Updating
import data.Voice
import data.VoiceStatus
import sDownloadVoiceLink
import sEmptyTitle
import translations.domain.ContextString.Base.Strings

class VoiceToItemOtherChat(
    private val mUpdating: Updating,
    private val mBotName: String
) : Voice.Mapper<InlineQueryResultCachedAudio> {

    override fun map(
        id: Long,
        fileOgaId: String,
        fileMp3Id: String,
        userId: Long,
        title: String,
        voiceLink: String,
        duration: Int,
        savedTime: Long,
        voiceStatus: VoiceStatus
    ): InlineQueryResultCachedAudio {
        return InlineQueryResultCachedAudio(
            id.toInt(),
            fileOgaId,
            "\uD83C\uDFB6 ${title.ifEmpty { Strings().string(sEmptyTitle, mUpdating, id) }}",
            "[${Strings().string(sDownloadVoiceLink, mUpdating)}](https://t.me/$mBotName?start=xj5mrl)"
        )
    }
}