package features.download_voice.oga

import chain.Chain
import core.Updating
import data.VoiceStorage
import domain.VoiceFactory
import executables.Executable
import executables.SendMessage
import handlers.OnVoiceGotten
import helper.UpdatingIsUserPremium
import helper.VoiceEditingButton
import helpers.convertToVertical
import keyboard_markup.InlineButton
import keyboard_markup.InlineKeyboardMarkup
import keyboard_markup.KeyboardButton
import sCancelLabel
import sSkipTitleLabel
import sTitleSuggestion
import translations.domain.ContextString.Base.Strings
import updating.UserIdUpdating

class CatchVoice : Chain(OnVoiceGotten()) {

    override suspend fun executableChain(updating: Updating): List<Executable> {
        val voice = VoiceFactory.Base(mKey, false).createVoiceEntity(updating)
        VoiceStorage.Base.Instance().insertVoice(voice)
        val lastVoiceId = VoiceStorage.Base.Instance().lastVoiceId(updating.map(UserIdUpdating()))
        mStates.state(updating).editor(mStates).apply {
            putBoolean("isAudio", false)
        }.commit()
        return listOf(
            SendMessage(
                mKey,
                Strings().string(sTitleSuggestion, updating),
                InlineKeyboardMarkup(
                    mutableListOf<KeyboardButton>().apply {
                        if (updating.map(UpdatingIsUserPremium())) {
                            add(
                                VoiceEditingButton.Base(lastVoiceId, updating).button()
                            )
                        }
                        add(
                            InlineButton(
                                Strings().string(sSkipTitleLabel, updating),
                                mCallbackData = "skipName=$lastVoiceId"
                            )
                        )
                        add(
                            InlineButton(
                                Strings().string(sCancelLabel, updating),
                                mCallbackData = "cancelSaving=$lastVoiceId"
                            )
                        )
                    }.convertToVertical()
                )
            ) {
                mStates.state(updating).editor(mStates).apply {
                    putInt("waitForTitle", lastVoiceId.toInt())
                    deleteValue("waitForNewVoiceName")
                    deleteValue("nameEditingMessage")
                }.commit()
            }
        )
    }
}