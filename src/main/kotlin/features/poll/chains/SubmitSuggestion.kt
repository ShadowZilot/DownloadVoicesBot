package features.poll.chains

import chain.Chain
import core.Updating
import core.storage.Storages
import data.poll.PollStorage
import executables.AnswerToCallback
import executables.DeleteMessage
import executables.Executable
import executables.SendMessage
import handlers.OnCallbackGotten
import helpers.ToMarkdownSupported
import org.json.JSONObject
import sBot
import sFinalSubmitLabel
import translations.domain.ContextString
import updating.UserIdUpdating

class SubmitSuggestion : Chain(OnCallbackGotten("submitSuggestion")) {

    override suspend fun executableChain(updating: Updating): List<Executable> {
        val suggestionGroupId = Storages.Main.Provider().stConfig.configValueLong("feedbackGroup")
        val userId = updating.map(UserIdUpdating())
        val suggestionText = mStates.state(updating).string("suggestionText")
        mStates.state(updating).editor(mStates).apply {
            deleteValue("waitForSuggestion")
            deleteValue("pollMessageId")
            deleteValue("suggestionText")
        }.commit()
        val suggestionAction = SendMessage(
            mKey,
            buildString {
                appendLine("UserId: `$userId`")
                appendLine()
                appendLine(ToMarkdownSupported.Base(suggestionText).convertedString())
            },
            mChatId = suggestionGroupId
        ) { _ ->
            PollStorage.Base.Instance().insertSuggestion(userId, suggestionText)
        }
        sBot.implementRequest(suggestionAction.map(JSONObject()), suggestionAction)
        return listOf(
            AnswerToCallback(
                mKey,
                ContextString.Base.Strings().string(sFinalSubmitLabel, updating), true
            ),
            DeleteMessage(mKey, updating)
        )
    }
}