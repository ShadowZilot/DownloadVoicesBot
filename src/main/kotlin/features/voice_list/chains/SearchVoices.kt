package features.voice_list.chains

import admin_bot_functions.base.IsUserAdmin
import chain.Chain
import core.Updating
import core.storage.Storages
import data.VoiceStorage
import domain.VoiceToListItem
import domain.VoiceToSecListItem
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
        val searchQuery = updating.map(UpdatingInlineQuery())
        val mapper = if (searchQuery.contains("sec") &&
            updating.map(IsUserAdmin(Storages.Main.Provider().stAdmins))
        ) {
            VoiceToSecListItem(
                Strings().string(sDurationString, updating),
                updating
            )
        } else {
            VoiceToListItem(
                Strings().string(sDurationString, updating),
                updating
            )
        }
        val offset = updating.map(UpdatingInlineQueryOffset())
        return listOf(
            AnswerInlineQuery(
                mKey,
                if (searchQuery.contains("sec") &&
                    updating.map(IsUserAdmin(Storages.Main.Provider().stAdmins))
                ) {
                    VoiceStorage.Base.Instance().secretVoiceList(
                        updating.map(UpdatingInlineQueryOffset()),
                        searchQuery.removePrefix("sec")
                    )
                } else {
                    VoiceStorage.Base.Instance().voicesList(
                        updating.map(UserIdUpdating()),
                        updating.map(UpdatingInlineQueryOffset()),
                        searchQuery
                    )
                }.map {
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