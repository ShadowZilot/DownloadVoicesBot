package features.donations.chains

import chain.Chain
import core.Updating
import executables.Executable
import executables.SendMessage
import handlers.CommandEvent
import helpers.convertToVertical
import keyboard_markup.InlineButton
import keyboard_markup.InlineKeyboardMarkup
import sDonateLabel
import sDonateMessage
import translations.domain.ContextString
import updating.UpdatingLanguageCode

class DonationCommand : Chain(CommandEvent("/donate")) {

    override suspend fun executableChain(updating: Updating): List<Executable> {
        return listOf(
            if (updating.map(UpdatingLanguageCode()) == "ru") {
                SendMessage(
                    mKey,
                    ContextString.Base.Strings().string(sDonateMessage, updating),
                    InlineKeyboardMarkup(
                        listOf(
                            InlineButton(
                                ContextString.Base.Strings().string(sDonateLabel, updating),
                                "https://www.tinkoff.ru/rm/ponomarev.egor224/iy7cZ40310"
                            )
                        ).convertToVertical()
                    )
                )
            } else {
                SendMessage(
                    mKey,
                    ContextString.Base.Strings().string(sDonateMessage, updating)
                )
            }
        )
    }
}