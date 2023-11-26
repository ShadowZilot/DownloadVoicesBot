import core.Bot
import core.BotProvider
import core.storage.Storages
import data.VoiceStorage
import features.ads.AdsFeature
import features.download_voice.DownloadVoiceFeature
import features.greeting.GreetingFunction
import features.moderator.ModeratorFeature
import features.moderator.chains.ModeratorMenu
import features.voice_list.VoiceListFeature
import helpers.storage.jdbc_wrapping.DatabaseHelper

lateinit var sBot: Bot
var sBasePath = "/Users/egorponomarev/IdeaProjects/DownloadVoicesBot/"
val sOgaGroup by lazy {
    Storages.Main.Provider().stConfig.configValueLong("oga_group")
}

fun main(args: Array<String>) {
    sBasePath = if (args.isEmpty()) "/Users/egorponomarev/IdeaProjects/DownloadVoicesBot/" else args[0]
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
        AdsFeature(),
        ModeratorFeature()
    )
}