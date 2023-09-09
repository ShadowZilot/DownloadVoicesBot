package features.voice_list.chains

import chain.Chain
import core.Updating
import data.VoiceStorage
import domain.VoiceToListItem
import executables.AnswerInlineQuery
import executables.Executable
import handlers.OnInlineQuerySenderChat
import inline_query_result.InlineQueryResultArticle
import inline_query_result.content.InputTextContent
import sDurationString
import sEmptyVoicesDescription
import sEmptyVoicesTitle
import sVoicesNotFoundDescription
import sVoicesNotFoundTitle
import translations.domain.ContextString.Base.Strings
import updating.UpdatingInlineQuery
import updating.UpdatingInlineQueryOffset
import updating.UserIdUpdating

class SearchVoices : Chain(OnInlineQuerySenderChat()) {

    override suspend fun executableChain(updating: Updating): List<Executable> {
        val mapper = VoiceToListItem(
            Strings().string(sDurationString, updating),
            updating
        )
        val searchQuery = updating.map(UpdatingInlineQuery())
        val offset = updating.map(UpdatingInlineQueryOffset())
        return listOf(
            AnswerInlineQuery(
                mKey,
                VoiceStorage.Base.Instance().voicesList(
                    updating.map(UserIdUpdating()),
                    updating.map(UpdatingInlineQueryOffset()),
                    searchQuery
                ).map {
                    it.map(mapper)
                }.ifEmpty {
                    if (offset == 0) {
                        listOf(
                            if (searchQuery.isEmpty()) {
                                InlineQueryResultArticle(
                                    0,
                                    Strings().string(sEmptyVoicesTitle, updating),
                                    InputTextContent("noVoices"),
                                    "",
                                    Strings().string(sEmptyVoicesDescription, updating)
                                )
                            } else {
                                InlineQueryResultArticle(
                                    0,
                                    Strings().string(sVoicesNotFoundTitle, updating),
                                    InputTextContent("voicesNotFound"),
                                    "",
                                    Strings().string(sVoicesNotFoundDescription, updating)
                                )
                            }
                        )
                    } else {
                        emptyList()
                    }
                },
                offset + 50,
                0
            )
        )
    }
}