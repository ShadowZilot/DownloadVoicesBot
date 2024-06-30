package domain.messages

import core.Updating
import core.storage.Storages
import data.Voice
import data.VoiceStatus
import executables.EditCaptionMessage
import executables.Executable
import helpers.ToMarkdownSupported
import keyboard_markup.InlineButton
import keyboard_markup.InlineKeyboardMarkup
import keyboard_markup.InlineModeQuery
import sDeleteLabel
import sEditVoiceLabel
import sShareVoices
import sVoiceListLabel
import translations.domain.ContextString

class EditVoiceCaptionToNormal(
    private val mKey: String,
    private val mUpdating: Updating,
    private val mMessageId: Int = -1
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
        voiceStatus: VoiceStatus
    ): Executable {
        return EditCaptionMessage(
            mKey,
            "@${
                ToMarkdownSupported.Base(
                    Storages.Main.Provider().stConfig.configValueString("botName")
                ).convertedString()
            }",
            mEditingMessageId = mMessageId.toLong(),
            mMarkup = VoiceKeyboard.invoke(mUpdating, id.toInt())
        )
    }
}

object VoiceKeyboard {

    operator fun invoke(updating: Updating, voiceId: Int) = InlineKeyboardMarkup(
        listOf(
            listOf(
                InlineButton(
                    ContextString.Base.Strings().string(sDeleteLabel, updating),
                    mCallbackData = "deleteVoice=${voiceId}"
                ),
                InlineButton(
                    ContextString.Base.Strings().string(sEditVoiceLabel, updating),
                    mCallbackData = "renameVoice=${voiceId}"
                )
            ),
            listOf(
                InlineButton(
                    ContextString.Base.Strings().string(sVoiceListLabel, updating),
                    mInlineMode = InlineModeQuery.CurrentChat()
                )
            ),
            listOf(
                InlineButton(
                    ContextString.Base.Strings().string(sShareVoices, updating),
                    mInlineMode = InlineModeQuery.OtherChat()
                )
            ),
        )
    )
}