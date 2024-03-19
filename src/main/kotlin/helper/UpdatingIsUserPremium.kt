package helper

import core.Updating
import data.premium.PremiumUserStorage
import org.json.JSONObject
import updating.UserIdUpdating

class UpdatingIsUserPremium : Updating.Mapper<Boolean> {

    override fun map(updating: JSONObject): Boolean {
        val sourceUpdating = Updating(updating)
        return PremiumUserStorage.Base.Instance().isUserPremium(sourceUpdating.map(UserIdUpdating()))
    }
}