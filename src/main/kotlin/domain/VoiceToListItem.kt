package domain

import core.Updating
import core.storage.Storages
import data.Voice
import data.VoiceStatus
import inline_query_result.InlineQueryResultArticle
import inline_query_result.content.InputTextContent
import sEmptyTitle
import translations.domain.ContextString

class VoiceToListItem(
    private val mDurationString: String,
    private val mUpdating: Updating
) : Voice.Mapper<InlineQueryResultArticle> {

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
    ): InlineQueryResultArticle {
        val minutes = duration / 60
        val seconds = if (duration - (minutes * 60) < 10) {
            "0${duration - (minutes * 60)}"
        } else {
            (duration - (minutes * 60)).toString()
        }
        return InlineQueryResultArticle(
            id.toInt(),
            "\uD83C\uDFB6 ${title.ifEmpty { ContextString.Base.Strings().string(sEmptyTitle, mUpdating, id) }}",
            InputTextContent("voice\\=$id"),
            Storages.Main.Provider().stConfig.configValueString("voicePreview"),
            "${mDurationString}%d:%s".format(minutes, seconds),
        )
    }
}