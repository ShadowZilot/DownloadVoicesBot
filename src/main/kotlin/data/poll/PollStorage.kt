package data.poll

import helpers.storage.StorageShell
import helpers.storage.jdbc_wrapping.DatabaseHelper
import helpers.storage.jdbc_wrapping.PackedStatementImpl
import java.util.*

interface PollStorage : StorageShell {

    fun isUserPolled(userId: Long): Boolean

    fun insertSuggestion(userId: Long, suggestion: String)

    class Base private constructor(
        private val mTableName: String,
        private val mDatabase: DatabaseHelper
    ) : PollStorage {
        override fun isUserPolled(userId: Long): Boolean {
            var isUserPolled = false
            mDatabase.executeQuery(
                PackedStatementImpl(
                    "SELECT COUNT(user_id) as users_count FROM $mTableName WHERE user_id = ?",
                    userId
                )
            ) { item, _ ->
                isUserPolled = item.getInt("users_count") != 0
            }
            return isUserPolled
        }

        override fun insertSuggestion(userId: Long, suggestion: String) {
            mDatabase.executeQueryWithoutResult(Poll(userId, suggestion, Date()).insertSQLQuery(mTableName))
        }

        override fun tableName() = mTableName
        override fun tableSchema() = "CREATE TABLE $mTableName(" +
                "user_id bigint," +
                "suggestion text," +
                "created_at datetime(4) NOT NULL DEFAULT CURRENT_TIMESTAMP(4)" +
                ");"

        object Instance {
            private var mInstance: PollStorage? = null

            fun create(tableName: String, databaseHelper: DatabaseHelper) {
                if (mInstance == null) {
                    mInstance = PollStorage.Base(tableName, databaseHelper)
                }
            }

            operator fun invoke(): PollStorage {
                return mInstance ?: throw Exception()
            }
        }
    }
}