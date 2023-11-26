package features.download_voice.mp3

import ads.AdsMessage
import ads.AdsRandomizer
import chain.Chain
import core.Updating
import core.storage.Storages
import data.VoiceStorage
import domain.VoiceFactory
import event_handlers.OnAudioSend
import executables.Executable
import executables.SendMessage
import helpers.convertToVertical
import keyboard_markup.InlineButton
import keyboard_markup.InlineKeyboardMarkup
import sCancelLabel
import sSaveAudioNameLabel
import sTitleSuggestion
import translations.domain.ContextString
import updating.UserIdUpdating

class CatchAudio : Chain(OnAudioSend()) {

    override suspend fun executableChain(updating: Updating): List<Executable> {
        val voice = VoiceFactory.Base(mKey, true).createVoiceEntity(updating)
        VoiceStorage.Base.Instance().insertVoice(voice)
        val lastVoiceId = VoiceStorage.Base.Instance().lastVoiceId(updating.map(UserIdUpdating()))
        mStates.state(updating).editor(mStates).apply {
            putBoolean("isAudio", true)
        }.commit()
        return AdsRandomizer.Base(
            updating,
            mStates,
            listOf(
                AdsMessage.Base(
                    mKey, lastVoiceId.toInt(),
                    updating
                ).message()
            ),
            listOf(
                SendMessage(
                    mKey,
                    ContextString.Base.Strings().string(sTitleSuggestion, updating),
                    InlineKeyboardMarkup(
                        listOf(
                            InlineButton(
                                ContextString.Base.Strings().string(sSaveAudioNameLabel, updating),
                                mCallbackData = "leftFileName=$lastVoiceId"
                            ),
                            InlineButton(
                                ContextString.Base.Strings().string(sCancelLabel, updating),
                                mCallbackData = "cancelSaving=$lastVoiceId"
                            ),
                        ).convertToVertical()
                    )
                ) {
                    mStates.state(updating).editor(mStates).apply {
                        putInt("waitForTitle", lastVoiceId.toInt())
                    }.commit()
                }
            ),
            Storages.Main.Provider().stConfig.configValueLong("adTimeout")
        ).executableList()

    }
}