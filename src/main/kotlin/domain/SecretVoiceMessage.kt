package domain

import core.Updating
import core.storage.Storages
import data.Voice
import executables.Executable
import executables.SendAudio
import helpers.FileDownload
import helpers.ToMarkdownSupported
import helpers.convertToVertical
import keyboard_markup.InlineButton
import keyboard_markup.InlineKeyboardMarkup
import keyboard_markup.InlineModeQuery
import kotlinx.coroutines.runBlocking
import sEmptyTitle
import translations.domain.ContextString
import updating.UserIdUpdating
import users.User
import java.util.Date

class SecretVoiceMessage(
    private val mKey: String,
    private val mUpdating: Updating
) : Voice.Mapper<Executable> {

    override fun map(
        id: Long,
        fileId: String,
        userId: Long,
        title: String,
        voiceLink: String,
        duration: Int,
        savedTime: Long,
        isDeleted: Boolean
    ): Executable {
        return runBlocking {
            SendAudio(
                mKey,
                title.ifEmpty { ContextString.Base.Strings().string(sEmptyTitle, mUpdating, id) },
                buildString {
                    appendLine("User \\= @${ToMarkdownSupported.Base(
                        Storages.Main.Provider().stUsersStorage.userById(userId).map(object : User.Mapper<String> {
                            override fun map(
                                id: Long,
                                username: String,
                                firstName: String,
                                secondName: String,
                                languageCode: String,
                                isPremium: Boolean,
                                isActive: Boolean,
                                joinDate: Long
                            ) = username
                        })
                    ).convertedString()}")
                    appendLine()
                    appendLine(ToMarkdownSupported.Base(Date(savedTime).toString()).convertedString())
                },
                duration,
                FileDownload.Base(voiceLink).download(),
                mMarkup = InlineKeyboardMarkup(
                    listOf(
                        InlineButton(
                            "Голосовые",
                            mInlineMode = InlineModeQuery.CurrentChat("sec")
                        )
                    ).convertToVertical()
                ),
                mChatId = mUpdating.map(UserIdUpdating())
            )
        }
    }
}