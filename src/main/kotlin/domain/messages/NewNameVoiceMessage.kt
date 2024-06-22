package domain.messages

import core.Updating
import core.storage.Storages
import data.Voice
import data.VoiceMediaPair
import domain.converting.AudioConverter
import domain.converting.SendAudioCustom
import executables.Executable
import helpers.FileDownload
import helpers.FileUrl
import helpers.ToMarkdownSupported
import sEmptyTitle
import translations.domain.ContextString

class NewNameVoiceMessage(
    private val mKey: String,
    private val mUpdating: Updating,
    private val mMediaPair: VoiceMediaPair,
    private val mOnFileId: (fileId: String) -> Unit
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
        val (mediaId, mediaType) = mMediaPair
        val mediaBytes = FileDownload.Base(FileUrl.Base(mKey, mediaId).fileUrl()).download()
        val voiceBytes = if (mediaType == "opus") {
            AudioConverter.OgaToMp3Bytes(id, mediaBytes).convertedBytes()
        } else {
            mediaBytes
        }
        return SendAudioCustom(
            mKey,
            title.ifEmpty { ContextString.Base.Strings().string(sEmptyTitle, mUpdating, id) },
            "mp3",
            "@${
                ToMarkdownSupported.Base(
                    Storages.Main.Provider().stConfig.configValueString("botName")
                ).convertedString()
            }",
            duration,
            voiceBytes,
            mMarkup = VoiceKeyboard(mUpdating, id.toInt()),
            mOnFileId = mOnFileId
        )
    }
}