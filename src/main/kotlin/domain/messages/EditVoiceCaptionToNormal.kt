package domain.messages

import core.Updating
import core.storage.Storages
import data.Voice
import executables.EditCaptionMessage
import executables.Executable
import helpers.ToMarkdownSupported
import helpers.convertToVertical
import keyboard_markup.InlineButton
import keyboard_markup.InlineKeyboardMarkup
import keyboard_markup.InlineModeQuery
import sDeleteLabel
import sShareVoices
import sVoiceListLabel
import translations.domain.ContextString

class EditVoiceCaptionToNormal(
    private val mKey: String,
    private val mUpdating: Updating
) : Voice.Mapper<Executable> {

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
    ): Executable {
        return EditCaptionMessage(
            mKey,
            "@${
                ToMarkdownSupported.Base(
                    Storages.Main.Provider().stConfig.configValueString("botName")
                ).convertedString()
            }",
            mMarkup = InlineKeyboardMarkup(
                listOf(
                    InlineButton(
                        ContextString.Base.Strings().string(sVoiceListLabel, mUpdating),
                        mInlineMode = InlineModeQuery.CurrentChat()
                    ),
                    InlineButton(
                        ContextString.Base.Strings().string(sShareVoices, mUpdating),
                        mInlineMode = InlineModeQuery.OtherChat()
                    ),
                    InlineButton(
                        ContextString.Base.Strings().string(sDeleteLabel, mUpdating),
                        mCallbackData = "deleteVoice=${id}"
                    )
                ).convertToVertical()
            )
        )
    }
}