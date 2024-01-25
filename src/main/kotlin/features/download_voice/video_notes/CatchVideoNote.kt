package features.download_voice.video_notes

import chain.Chain
import core.Updating
import domain.converting.AudioConverter
import domain.converting.SendAudioCustom
import domain.converting.VideoToMp3
import executables.Executable
import executables.SendAudio
import executables.SendMessage
import handlers.OnVideoNoteGotten
import helpers.FileDownload
import helpers.FileUrl
import helpers.convertToVertical
import keyboard_markup.InlineButton
import keyboard_markup.InlineKeyboardMarkup
import sCancelLabel
import sSkipTitleLabel
import sTitleSuggestion
import translations.domain.ContextString
import updating.UpdatingVideoNoteDuration
import updating.UpdatingVideoNoteFileId
import java.util.UUID

class CatchVideoNote : Chain(OnVideoNoteGotten()) {

    override suspend fun executableChain(updating: Updating): List<Executable> {
        val fileId = updating.map(UpdatingVideoNoteFileId())
        val duration = updating.map(UpdatingVideoNoteDuration())
        mStates.state(updating).editor(mStates).apply {
            putString("videoNoteId", fileId)
            putInt("videoDuration", duration)
        }.commit()
        return listOf(
            SendMessage(
                mKey,
                ContextString.Base.Strings().string(sTitleSuggestion, updating),
                InlineKeyboardMarkup(
                    listOf(
                        InlineButton(
                            ContextString.Base.Strings().string(sSkipTitleLabel, updating),
                            mCallbackData = "skipVideoName"
                        ),
                        InlineButton(
                            ContextString.Base.Strings().string(sCancelLabel, updating),
                            mCallbackData = "cancelSaving=-1"
                        ),
                    ).convertToVertical()
                )
            )
        )
    }
}