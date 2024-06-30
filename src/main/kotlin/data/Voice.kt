package data

import helpers.storage.Record
import helpers.storage.jdbc_wrapping.PackedStatementImpl
import java.sql.ResultSet

data class Voice(
    private val mId: Long,
    private val mFileOgaId: String,
    private val mFileMp3Id: String,
    private val mUserId: Long,
    private val mTitle: String,
    private val mVoiceLink: String,
    private val mDuration: Int,
    private val mSavedTime: Long,
    private val mVoiceStatus: VoiceStatus
) : Record() {
    constructor(item: ResultSet) : this(
        item.getLong("id"),
        item.getString("file_oga_id"),
        item.getString("file_mp3_id"),
        item.getLong("user_id"),
        item.getString("title"),
        item.getString("voice_link"),
        item.getInt("duration"),
        item.getLong("saved_time"),
        when (item.getInt("voice_status")) {
            VoiceStatus.NORMAL.statusCode -> VoiceStatus.NORMAL
            VoiceStatus.DELETED.statusCode -> VoiceStatus.DELETED
            VoiceStatus.CREATING.statusCode -> VoiceStatus.CREATING
            else -> throw IllegalStateException("Unknown voice status = ${item.getInt("is_deleted")}")
        }
    )

    fun <T> map(mapper: Mapper<T>) = mapper.map(
        mId,
        mFileOgaId,
        mFileMp3Id,
        mUserId,
        mTitle,
        mVoiceLink,
        mDuration,
        mSavedTime,
        mVoiceStatus
    )

    interface Mapper<T> {

        fun map(
            id: Long,
            fileOgaId: String,
            fileMp3Id: String,
            userId: Long,
            title: String,
            voiceLink: String,
            duration: Int,
            savedTime: Long,
            voiceStatus: VoiceStatus
        ): T
    }

    override fun deleteSQLQuery(tableName: String) = PackedStatementImpl(
        "DELETE FROM $tableName WHERE `id` = ?",
        mId
    )

    override fun insertSQLQuery(tableName: String) = PackedStatementImpl(
        "INSERT INTO $tableName (`file_oga_id`, `file_mp3_id`," +
                " `user_id`," +
                " `title`, `voice_link`, `duration`, `saved_time`, `voice_status`) VALUES(?, ?" +
                ", ?, ?," +
                " ?, ?, ?, ?)",
        mFileOgaId, mFileMp3Id, mUserId, mTitle, mVoiceLink, mDuration, mSavedTime, mVoiceStatus.statusCode
    )

    override fun updateSQLQuery(tableName: String) = PackedStatementImpl(
        "UPDATE $tableName SET `file_oga_id` = '$mFileOgaId'," +
                " `file_mp3_id` = ?," +
                " `user_id` = ?, `title` = ?, `voice_link` = ?," +
                " `duration` = ?, `saved_time` = ? WHERE `id` = ?",
        mFileOgaId, mFileMp3Id, mUserId, mTitle, mVoiceLink, mDuration, mSavedTime, mId
    )
}