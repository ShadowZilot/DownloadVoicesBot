import api.voices.VoicesRoutes
import core.Bot
import core.BotProvider
import core.ExceptionHandlers
import core.storage.Storages
import data.VoiceStorage
import data.poll.PollStorage
import data.premium.PremiumUserStorage
import exception_handlers.*
import features.ads.AdsFeature
import features.download_voice.DownloadVoiceFeature
import features.editing_voices.EditingVoicesFeature
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
        ExceptionHandlers(
            VoiceIdInvalidHandler(),
            UserIdInvalidHandler(),
            VoiceIdNotFoundExceptionHandler(),
            UserIdNotFoundExceptionHandler(),
            CommonExceptionHandler(),
            VoiceAccessForbiddenHandler(),
        ),
        VoicesRoutes()
    )
    val db = DatabaseHelper.Base.Instance.provideInstance(
        Storages.Main.Provider().stConfig
    )
    VoiceStorage.Base.Instance.create("voices", db)
    db.createTable(VoiceStorage.Base.Instance().tableSchema())
    PollStorage.Base.Instance.create("polls", db)
    db.createTable(PollStorage.Base.Instance().tableSchema())
    PremiumUserStorage.Base.Instance.create("premiums", db)
    db.createTable(PremiumUserStorage.Base.Instance().tableSchema())
    provider.createdBot(
        VoiceListFeature(),
        GreetingFunction(),
        AdsFeature(),
        DownloadVoiceFeature(),
        EditingVoicesFeature(),
        PollChains(),
        ModeratorFeature()
    ) { bot ->
        sBot = bot
    }
}