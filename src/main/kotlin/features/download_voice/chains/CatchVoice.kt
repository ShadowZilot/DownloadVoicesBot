package features.download_voice.chains

import ads.AdsMessage
import ads.AdsRandomizer
import chain.Chain
import core.Updating
import core.storage.Storages
import data.VoiceStorage
import domain.VoiceFactory
import executables.Executable
import executables.SendMessage
import handlers.OnVoiceGotten
import helpers.convertToVertical
import keyboard_markup.InlineButton
import keyboard_markup.InlineKeyboardMarkup
import sCancelLabel
import sSkipTitleLabel
import sTitleSuggestion
import translations.domain.ContextString.Base.Strings
import updating.UserIdUpdating

class CatchVoice : Chain(OnVoiceGotten()) {

    override suspend fun executableChain(updating: Updating): List<Executable> {
        val voice = VoiceFactory.Base(mKey).createVoiceEntity(updating)
        VoiceStorage.Base.Instance().insertVoice(voice)
        val lastVoiceId = VoiceStorage.Base.Instance().lastVoiceId(updating.map(UserIdUpdating()))
        return AdsRandomizer.Base(
            updating,
            mStates,
            listOf(
                AdsMessage.Base(mKey, lastVoiceId.toInt()).message()
            ),
            listOf(
                SendMessage(
                    mKey,
                    Strings().string(sTitleSuggestion, updating),
                    InlineKeyboardMarkup(
                        listOf(
                            InlineButton(
                                Strings().string(sSkipTitleLabel, updating),
                                mCallbackData = "skipName=$lastVoiceId"
                            ),
                            InlineButton(
                                Strings().string(sCancelLabel, updating),
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