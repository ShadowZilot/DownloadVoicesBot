import core.Bot
import core.BotProvider
import core.storage.Storages
import data.VoiceStorage
import features.ads.AdsFeature
import features.download_voice.DownloadVoiceFeature
import features.greeting.GreetingFunction
import features.voice_list.VoiceListFeature
import helpers.storage.jdbc_wrapping.DatabaseHelper

lateinit var sBot: Bot

fun main(args: Array<String>) {
    val provider = BotProvider.Base(args)
    val db = DatabaseHelper.Base.Instance.provideInstance(
        Storages.Main.Provider().stConfig
    )
    VoiceStorage.Base.Instance.create("voices", db)
    db.createTable(VoiceStorage.Base.Instance().tableSchema())
    sBot = provider.createdBot(
        VoiceListFeature(),
        GreetingFunction(),
        DownloadVoiceFeature(),
        AdsFeature()
    )
}