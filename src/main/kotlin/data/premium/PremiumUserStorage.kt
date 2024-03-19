package data.premium

import helpers.storage.StorageShell
import helpers.storage.jdbc_wrapping.DatabaseHelper
import java.sql.SQLException

interface PremiumUserStorage : StorageShell {

    fun isUserPremium(userId: Long): Boolean

    class Base private constructor(
        private val mTableName: String,
        private val mDatabase: DatabaseHelper
    ) : PremiumUserStorage {

        override fun isUserPremium(userId: Long): Boolean {
            var result = false
            mDatabase.executeQuery(
                "SELECT COUNT(id) as is_premium FROM $mTableName WHERE user_id = ?",
                { item, _ ->
                    result = try {
                        item.getInt("is_premium") == 1
                    } catch (e: SQLException) {
                        false
                    }
                },
                userId
            )
            return result
        }

        override fun tableName() = mTableName

        override fun tableSchema() = "CREATE TABLE $mTableName(" +
                "id int auto_increment," +
                "user_id bigint," +
                "is_premium_now bool," +
                "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "PRIMARY KEY (id, user_id)," +
                "FOREIGN KEY (user_id) REFERENCES users (id)" +
                ");"

        object Instance {
            private var mInstance: PremiumUserStorage? = null

            fun create(tableName: String, databaseHelper: DatabaseHelper) {
                if (mInstance == null) {
                    mInstance = Base(tableName, databaseHelper)
                }
            }

            operator fun invoke(): PremiumUserStorage {
                return mInstance ?: throw Exception()
            }
        }
    }
}