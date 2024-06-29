package domain.messages

import core.Updating
import data.Voice
import executables.EditCaptionMessage
import executables.Executable
import helpers.convertToVertical
import keyboard_markup.InlineButton
import keyboard_markup.InlineKeyboardMarkup
import sCancelLabel
import sSubmitDeleteVoice
import sSubmitLabel
import translations.domain.ContextString

class VoiceSubmitDeletionMessage(
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
            ContextString.Base.Strings().string(sSubmitDeleteVoice, mUpdating),
            mMarkup = InlineKeyboardMarkup(
                listOf(
                    InlineButton(
                        ContextString.Base.Strings().string(sSubmitLabel, mUpdating),
                        mCallbackData = "submitDeleteVoice=${id}"
                    ),
                    InlineButton(
                        ContextString.Base.Strings().string(sCancelLabel, mUpdating),
                        mCallbackData = "cancelDeleteVoice=${id}"
                    )
                ).convertToVertical()
            )
        )
    }
}