package data.premium

import helpers.storage.Record
import helpers.storage.jdbc_wrapping.PackedStatementImpl
import java.sql.ResultSet
import java.util.*

data class PremiumUser(
    private val mId: Int,
    private val mUserId: Long,
    private val mIsPremiumNow: Boolean,
    private val mUpdatedAt: Date,
    private val mCreatedAt: Date
) : Record() {

    constructor(item: ResultSet) : this(
        item.getInt("id"),
        item.getLong("user_id"),
        item.getBoolean("is_premium_now"),
        item.getDate("updated_at"),
        item.getDate("created_at")
    )

    fun <T> map(mapper: Mapper<T>) = mapper.map(
        mId,
        mUserId,
        mIsPremiumNow,
        mUpdatedAt,
        mCreatedAt
    )

    override fun deleteSQLQuery(tableName: String) = PackedStatementImpl(
        "DELETE FROM $tableName WHERE id = ?;", mId
    )

    override fun insertSQLQuery(tableName: String) = PackedStatementImpl(
        "INSERT INTO $tableName (id, user_id, is_premium_now," +
                " updated_at, created_at) VALUES(?, ?, ?, ?, ?);", mId, mUserId,
        mIsPremiumNow, mUpdatedAt, mCreatedAt
    )

    override fun updateSQLQuery(tableName: String) = PackedStatementImpl(
        "UPDATE $tableName SET is_premium_now = ? WHERE id = ? AND user_id = ?",
        mIsPremiumNow, mId, mUserId
    )

    interface Mapper<T> {

        fun map(
            id: Int,
            userId: Long,
            isPremiumNow: Boolean,
            updatedAt: Date,
            createdAt: Date
        ) : T
    }
}