import api.voices.VoicesRoutes
import core.Bot
import core.BotProvider
import core.storage.Storages
import data.VoiceStorage
import data.poll.PollStorage
import features.ads.AdsFeature
import features.download_voice.DownloadVoiceFeature
import features.greeting.GreetingFunction
import features.moderator.ModeratorFeature
import features.poll.PollChains
import features.voice_list.VoiceListFeature
import helpers.storage.jdbc_wrapping.DatabaseHelper

lateinit var sBot: Bot
var sBasePath = "/Users/egorponomarev/IdeaProjects/DownloadVoicesBot/"

fun main(args: Array<String>) {
    sBasePath = if (args.isEmpty()) "/Users/egorponomarev/IdeaProjects/DownloadVoicesBot/" else args[0]
    val provider = BotProvider.Base(
        args,
        VoicesRoutes()
    )
    val db = DatabaseHelper.Base.Instance.provideInstance(
        Storages.Main.Provider().stConfig
    )
    VoiceStorage.Base.Instance.create("voices", db)
    db.createTable(VoiceStorage.Base.Instance().tableSchema())
    PollStorage.Base.Instance.create("polls", db)
    db.createTable(PollStorage.Base.Instance().tableSchema())
    provider.createdBot(
        VoiceListFeature(),
        GreetingFunction(),
        AdsFeature(),
        DownloadVoiceFeature(),
        PollChains(),
        ModeratorFeature()
    ) { bot ->
        sBot = bot
    }
}