package data

import helpers.storage.Record
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
    private val mIsDeleted: Boolean
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
        item.getBoolean("is_deleted")
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
        mIsDeleted
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
            isDeleted: Boolean
        ): T
    }

    override fun deleteSQLQuery(tableName: String) = "DELETE FROM $tableName WHERE `id` = $mId"

    override fun insertSQLQuery(tableName: String) = "INSERT INTO $tableName (`file_oga_id`, `file_mp3_id`," +
            " `user_id`," +
            " `title`, `voice_link`, `duration`, `saved_time`) VALUES('$mFileOgaId', '$mFileMp3Id'" +
            ", $mUserId, '$mTitle'," +
            " '$mVoiceLink', $mDuration, $mSavedTime)"

    override fun updateSQLQuery(tableName: String) = "UPDATE $tableName SET `file_oga_id` = '$mFileOgaId'," +
            " `file_mp3_id` = '$mFileMp3Id'," +
            " `user_id` = $mUserId, `title` = '$mTitle', `voice_link` = '$mVoiceLink'," +
            " `duration` = $mDuration, `saved_time` = $mSavedTime WHERE `id` = $mId"
}