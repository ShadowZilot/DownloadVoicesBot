package domain

import core.Updating
import core.storage.Storages
import data.Voice
import domain.converting.AudioConverter
import domain.converting.AudioConvertingError
import domain.converting.SendAudioCustom
import executables.Executable
import executables.SendMessage
import helpers.FileDownload
import helpers.FileDownloadException
import helpers.ToMarkdownSupported
import helpers.convertToVertical
import keyboard_markup.InlineButton
import keyboard_markup.InlineKeyboardMarkup
import keyboard_markup.InlineModeQuery
import logs.LogLevel
import logs.Logging
import sConvertingErrorBtnLabel
import sConvertingErrorMessage
import sEmptyTitle
import sShareVoices
import sVoiceListLabel
import sVoiceSaved
import translations.domain.ContextString.Base.Strings
import updating.UserIdUpdating

class VoiceToMessage(
    private val mKey: String,
    private val mUpdating: Updating,
    private val mIsJustSaved: Boolean,
    private val mIsAudio: Boolean,
    private val mOnFileId: (fileId: String) -> Unit = {}
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
        return try {
            if (mIsJustSaved) {
                SendAudioCustom(
                    mKey,
                    title.ifEmpty { Strings().string(sEmptyTitle, mUpdating, id) },
                    if (mIsAudio) "opus" else "mp3",
                    Strings().string(sVoiceSaved, mUpdating),
                    duration,
                    if (mIsAudio) {
                        AudioConverter.Mp3ToOgaBytes(
                            id,
                            FileDownload.Base(voiceLink).download()
                        ).convertedBytes()
                    } else {
                        AudioConverter.OgaToMp3Bytes(
                            id,
                            FileDownload.Base(voiceLink).download()
                        ).convertedBytes()
                    },
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
                    mChatId = mUpdating.map(UserIdUpdating()),
                    mOnFileId = mOnFileId
                )
            } else {
                if (fileMp3Id.isEmpty()) {
                    SendAudioCustom(
                        mKey,
                        title.ifEmpty { Strings().string(sEmptyTitle, mUpdating, id) },
                        "mp3",
                        "@${
                            ToMarkdownSupported.Base(
                                Storages.Main.Provider().stConfig.configValueString("botName")
                            ).convertedString()
                        }",
                        duration,
                        AudioConverter.OgaToMp3Bytes(
                            id,
                            FileDownload.Base(voiceLink).download()
                        ).convertedBytes(),
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
                        mChatId = mUpdating.map(UserIdUpdating()),
                        mOnFileId = mOnFileId
                    )
                } else {
                    SendAudioCustom(
                        mKey,
                        title.ifEmpty { Strings().string(sEmptyTitle, mUpdating, id) },
                        "mp3",
                        "@${
                            ToMarkdownSupported.Base(
                                Storages.Main.Provider().stConfig.configValueString("botName")
                            ).convertedString()
                        }",
                        duration,
                        mFileId = fileMp3Id,
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
                        mChatId = mUpdating.map(UserIdUpdating()),
                        mOnFileId = mOnFileId
                    )
                }
            }
        } catch (e: AudioConvertingError) {
            SendMessage(
                mKey,
                Strings.invoke().string(sConvertingErrorMessage, mUpdating),
                InlineKeyboardMarkup(
                    listOf(
                        InlineButton(
                            Strings.invoke().string(sConvertingErrorBtnLabel, mUpdating),
                            "https://t.me/ShadowZilot"
                        )
                    ).convertToVertical()
                )
            )
        } catch (e: IllegalArgumentException) {
            throw FileDownloadException(voiceLink)
        } catch (e: Exception) {
            Logging.ConsoleLog.logToFile(e.message ?: "", LogLevel.Exception)
            Logging.ConsoleLog.logToChat(e.message ?: "", LogLevel.Exception)
            Executable.Dummy()
        }
    }
}
