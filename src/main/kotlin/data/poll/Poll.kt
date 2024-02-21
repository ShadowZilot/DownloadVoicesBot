package data.poll

import helpers.storage.Record
import helpers.storage.jdbc_wrapping.PackedStatementImpl
import java.sql.ResultSet
import java.util.*

data class Poll(
    private val mUserId: Long,
    private val mSuggestion: String,
    private val mCreatedDate: Date
) : Record() {

    constructor(item: ResultSet): this(
        item.getLong("user_id"),
        item.getString("suggestion"),
        item.getDate("created_at")
    )

    override fun deleteSQLQuery(tableName: String) = PackedStatementImpl(
        "DELETE FROM $tableName WHERE user_id = ?", mUserId
    )

    override fun insertSQLQuery(tableName: String) = PackedStatementImpl(
        "INSERT INTO $tableName (user_id, suggestion) VALUES(?, ?)",
        mUserId, mSuggestion
    )

    override fun updateSQLQuery(tableName: String) = PackedStatementImpl(
        "UPDATE $tableName SET suggestion = ? WHERE user_id = ?",
        mSuggestion, mUserId
    )
}