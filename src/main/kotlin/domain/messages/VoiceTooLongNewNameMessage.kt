package domain.messages

import core.Updating
import data.Voice
import data.VoiceStatus
import executables.EditCaptionMessage
import executables.Executable
import helpers.convertToVertical
import keyboard_markup.InlineButton
import keyboard_markup.InlineKeyboardMarkup
import sCancelLabel
import sTooLongName
import translations.domain.ContextString

class VoiceTooLongNewNameMessage(
    private val mKey: String,
    private val mUpdating: Updating,
    private val mMessageId: Int
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
            ContextString.Base.Strings().string(sTooLongName, mUpdating),
            mEditingMessageId = mMessageId.toLong(),
            mMarkup = InlineKeyboardMarkup(
                listOf(
                    InlineButton(
                        ContextString.Base.Strings().string(sCancelLabel, mUpdating),
                        mCallbackData = "cancelRenameVoice=${id}"
                    )
                ).convertToVertical()
            )
        )
    }
}