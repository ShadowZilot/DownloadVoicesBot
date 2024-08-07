package features.download_voice.video_notes

import chain.Chain
import core.Updating
import data.VoiceStorage
import domain.VoiceFromVideoNote
import domain.converting.*
import domain.messages.ContactDevMessage
import executables.AnswerToCallback
import executables.DeleteMessage
import executables.Executable
import executables.SendMessage
import handlers.OnCallbackGotten
import helpers.FileDownload
import helpers.FileUrl
import helpers.convertToVertical
import keyboard_markup.InlineButton
import keyboard_markup.InlineKeyboardMarkup
import keyboard_markup.InlineModeQuery
import sErrorSkipLabel
import sNoAudioError
import sShareVoices
import sVoiceListLabel
import sVoiceSaved
import staging.safetyString
import translations.domain.ContextString
import updating.UserIdUpdating
import kotlin.math.abs
import kotlin.random.Random

class SkipVideoName : Chain(OnCallbackGotten("skipVideoName")) {

    private val deleteVideoVars: (updating: Updating) -> Unit = { updating ->
        mStates.state(updating).editor(mStates).apply {
            deleteValue("videoDuration")
            deleteValue("videoNoteId")
        }.commit()
    }

    override suspend fun executableChain(updating: Updating): List<Executable> {
        val videoNoteId = mStates.state(updating).safetyString("videoNoteId")
        return if (videoNoteId.isEmpty()) {
            return listOf(
                AnswerToCallback(
                    mKey,
                    ContextString.Base.Strings().string(sErrorSkipLabel, updating), true
                ),
                DeleteMessage(mKey, updating)
            )
        } else {
            val videoNoteDuration = mStates.state(updating).int("videoDuration")
            val randomId = abs(Random(System.currentTimeMillis()).nextInt())
            val onFileIdGotten: (fileId: String) -> Unit = { fileId ->
                val voice = VoiceFromVideoNote(
                    mKey,
                    fileId,
                    videoNoteDuration
                ).createVoiceEntity(updating)
                VoiceStorage.Base.Instance().insertVoice(voice)
                deleteVideoVars(updating)
            }
            try {
                listOf(
                    AnswerToCallback(mKey),
                    SendAudioCustom(
                        mKey,
                        "",
                        "opus",
                        ContextString.Base.Strings().string(sVoiceSaved, updating),
                        videoNoteDuration,
                        AudioConverter.Mp3ToOgaBytes(
                            randomId.toLong(),
                            VideoToMp3(
                                randomId.toString(),
                                FileDownload.Base(
                                    FileUrl.Base(mKey, videoNoteId).fileUrl()
                                ).download()
                            ).convertedBytes()
                        ).convertedBytes(),
                        mMarkup = InlineKeyboardMarkup(
                            listOf(
                                InlineButton(
                                    ContextString.Base.Strings().string(sVoiceListLabel, updating),
                                    mInlineMode = InlineModeQuery.CurrentChat()
                                ),
                                InlineButton(
                                    ContextString.Base.Strings().string(sShareVoices, updating),
                                    mInlineMode = InlineModeQuery.OtherChat()
                                )
                            ).convertToVertical()
                        ),
                        mChatId = updating.map(UserIdUpdating()),
                        mOnFileId = onFileIdGotten
                    )
                )
            } catch (e: NoAudioInMp4) {
                deleteVideoVars(updating)
                listOf(
                    SendMessage(
                        mKey,
                        ContextString.Base.Strings().string(sNoAudioError, updating)
                    )
                )
            } catch (e: AudioConvertingError) {
                deleteVideoVars(updating)
                listOf(ContactDevMessage(mKey, updating))
            }
        }
    }
}