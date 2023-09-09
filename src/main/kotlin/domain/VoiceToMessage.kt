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
import sEmptyTitle
import sShareVoices
import sVoiceListLabel
import sVoiceSaved
import translations.domain.ContextString.Base.Strings
import updating.UserIdUpdating

class VoiceToMessage(
    private val mKey: String,
    private val mUpdating: Updating,
    private val mIsJustSaved: Boolean
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
        return SendAudio(
            mKey,
            title.ifEmpty { Strings().string(sEmptyTitle, mUpdating, id) },
            if (mIsJustSaved) Strings().string(sVoiceSaved, mUpdating)
            else "@${
                ToMarkdownSupported.Base(
                    Storages.Main.Provider().stConfig.configValueString("botName")
                ).convertedString()
            }",
            duration,
            FileDownload.Base(voiceLink).download(),
            mMarkup = InlineKeyboardMarkup(
                listOf(
                    InlineButton(
                        Strings().string(sVoiceListLabel, mUpdating),
                        mInlineMode = InlineModeQuery.CurrentChat()
                    ),
                    InlineButton(
                        Strings().string(sShareVoices, mUpdating),
                        mInlineMode = InlineModeQuery.OtherChat()
                    )
                ).convertToVertical()
            ),
            mChatId = mUpdating.map(UserIdUpdating())
        )
    }
}