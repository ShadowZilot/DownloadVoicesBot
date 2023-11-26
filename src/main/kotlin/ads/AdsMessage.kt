package ads

import broadcast.BroadcastHandling
import core.Updating
import executables.Executable
import executables.SendAnimation
import executables.SendMessage
import helpers.convertToVertical
import keyboard_markup.InlineButton
import keyboard_markup.InlineKeyboardMarkup
import sDonateLabel
import sDonateMessage
import sSkipTitleLabel
import translations.domain.ContextString
import translations.domain.ContextString.Base.Strings

interface AdsMessage {

    fun message() : Executable

    class Base(
        private val mKey: String,
        private val mVoiceId: Int,
        private val mUpdating: Updating
    ) : AdsMessage {

        override fun message(): Executable {
            return SendMessage(
                mKey,
                Strings().string(sDonateMessage, mUpdating),
                InlineKeyboardMarkup(
                    listOf(
                        InlineButton(
                            Strings().string(sDonateLabel, mUpdating),
                            "https://www.tinkoff.ru/rm/ponomarev.egor224/iy7cZ40310"
                        ),
                        InlineButton(
                            Strings().string(sSkipTitleLabel, mUpdating),
                            mCallbackData = "skipAd=$mVoiceId"
                        )
                    ).convertToVertical()
                )
            )
        }
    }
}