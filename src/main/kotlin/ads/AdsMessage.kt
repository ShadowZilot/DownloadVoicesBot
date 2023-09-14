package ads

import executables.Executable
import executables.SendAnimation
import executables.SendMessage
import helpers.convertToVertical
import keyboard_markup.InlineButton
import keyboard_markup.InlineKeyboardMarkup

interface AdsMessage {

    fun message() : Executable

    class Base(
        private val mKey: String,
        private val mVoiceId: Int,
        private val mAnimationId: String
    ) : AdsMessage {

        override fun message(): Executable {
            return SendAnimation(
                mKey,
                mAnimationId,
                buildString {
                    appendLine("*Телеграм уже давно перестал быть просто мессенджером\\.*")
                    appendLine()
                    appendLine("Тут вам и новости, и развлечения, даже есть [каталог ботов](https://t.me/TgCloudStoreBot?start=gp0mou)\\!")
                    appendLine()
                    appendLine("*Здесь вы можете:*")
                    appendLine("– Искать нужных ботов по категориям")
                    appendLine("– Опубликовать своего бота")
                    appendLine("– Повлиять на рейтинг ботов")
                    appendLine()
                    appendLine("А главное: всё бесплатно\\. Как для пользователей, так и для разработчиков\\.")
                    appendLine()
                    appendLine("Пока что, аналогов нет, попробуйте сами:")
                    appendLine("*[@TgCloudStoreBot](https://t.me/TgCloudStoreBot?start=gp0mou)*")
                },
                InlineKeyboardMarkup(
                    listOf(
                        InlineButton(
                            "\uD83D\uDD0D Найти бота",
                            "https://t.me/TgCloudStoreBot?start=gp0mou"
                        ),
                        InlineButton(
                            "Пропустить",
                            mCallbackData = "skipAd=$mVoiceId"
                        )
                    ).convertToVertical()
                )
            )
        }
    }
}