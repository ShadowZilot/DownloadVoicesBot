package features.voice_list.chains

import chain.Chain
import core.Updating
import core.storage.Storages
import data.VoiceStorage
import domain.VoiceToItemOtherChat
import executables.AnswerInlineQuery
import executables.Executable
import handlers.OnOtherChatInlineQuery
import inline_query_result.InlineQueryResultArticle
import inline_query_result.content.InputTextContent
import sEmptyVoicesDescription
import sEmptyVoicesTitle
import sVoicesNotFoundDescription
import sVoicesNotFoundTitle
import translations.domain.ContextString
import updating.UpdatingInlineQuery
import updating.UpdatingInlineQueryOffset
import updating.UserIdUpdating

class SearchVoicesOtherChat : Chain(OnOtherChatInlineQuery()) {

    override suspend fun executableChain(updating: Updating): List<Executable> {
        val mapper = VoiceToItemOtherChat(
            updating,
            Storages.Main.Provider().stConfig.configValueString("botName")
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
                                    ContextString.Base.Strings().string(sEmptyVoicesTitle, updating),
                                    InputTextContent("noVoices"),
                                    "",
                                    ContextString.Base.Strings().string(sEmptyVoicesDescription, updating)
                                )
                            } else {
                                InlineQueryResultArticle(
                                    0,
                                    ContextString.Base.Strings().string(sVoicesNotFoundTitle, updating),
                                    InputTextContent("voicesNotFound"),
                                    "",
                                    ContextString.Base.Strings().string(sVoicesNotFoundDescription, updating)
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