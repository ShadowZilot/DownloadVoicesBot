package helper

import core.Updating
import core.storage.Storages
import keyboard_markup.InlineButton
import sEditAudioWebMenuLabel
import translations.domain.ContextString

interface VoiceEditingButton {

    fun button(): InlineButton

    class Base(
        private val mVoiceId: Long,
        private val mUpdating: Updating
    ) : VoiceEditingButton {

        override fun button(): InlineButton {
            val webUrl = Storages.Main.Provider().stConfig.configValueString("webUrl")
            return InlineButton(
                ContextString.Base.Strings().string(sEditAudioWebMenuLabel, mUpdating),
                mWebAppUrl = "${webUrl}/editor?voice=$mVoiceId"
            )
        }
    }
}