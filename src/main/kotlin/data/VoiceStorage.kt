package data

import helpers.storage.StorageShell
import helpers.storage.jdbc_wrapping.DatabaseHelper
import java.sql.SQLException

interface VoiceStorage : StorageShell {

    fun insertVoice(voice: Voice)

    fun updateVoiceTitle(id: Long, title: String)

    fun updateVoiceDeletion(id: Long, isDeleted: Boolean)

    fun voiceById(id: Long): Voice

    fun voicesList(userId: Long, offset: Int, search: String) : List<Voice>

    fun lastVoiceId(userId: Long): Long

    class Base private constructor(
        private val mTableName: String,
        private val mDatabase: DatabaseHelper
    ) : VoiceStorage {

        override fun insertVoice(voice: Voice) {
            mDatabase.executeQueryWithoutResult(voice.insertSQLQuery(mTableName))
        }

        override fun updateVoiceTitle(id: Long, title: String) {
            mDatabase.executeQueryWithoutResult(
                "UPDATE $mTableName SET `title` = '$title' WHERE `id` = $id"
            )
        }

        override fun updateVoiceDeletion(id: Long, isDeleted: Boolean) {
            mDatabase.executeQueryWithoutResult(
                "UPDATE $mTableName SET `is_deleted` = $isDeleted WHERE `id` = $id"
            )
        }

        override fun voiceById(id: Long): Voice {
            var voice: Voice? = null
            mDatabase.executeQuery(
                "SELECT * FROM $mTableName WHERE `id` = $id AND `is_deleted` = 0"
            ) { item, _ ->
                voice = try {
                    Voice(item)
                } catch (e: SQLException) {
                    null
                }
            }
            return voice ?: throw VoiceNotFound(id)
        }

        override fun voicesList(userId: Long, offset: Int, search: String): List<Voice> {
            val voices = mutableListOf<Voice>()
            val searchQuery = if (search.isNotEmpty()) "AND INSTR(`title`, '$search') > 0" else ""
            mDatabase.executeQuery(
                "SELECT * FROM $mTableName WHERE `user_id` = $userId AND" +
                        " `is_deleted` = 0 $searchQuery ORDER BY `saved_time` DESC LIMIT 50 OFFSET $offset"
            ) { item, isNext ->
                var next = isNext
                while (next) {
                    voices.add(Voice(item))
                    next = item.next()
                }
            }
            return voices
        }

        override fun lastVoiceId(userId: Long): Long {
            var lastId = -1L
            mDatabase.executeQuery(
                "SELECT id FROM $mTableName WHERE `user_id` = $userId ORDER BY `saved_time` DESC LIMIT 1"
            ) { item, _ ->
                lastId = try {
                    item.getLong("id")
                } catch (e: SQLException) {
                    -1
                }
            }
            return lastId
        }


        override fun tableName() = mTableName

        override fun tableSchema() = "CREATE TABLE $mTableName(" +
                "id bigint primary key auto_increment," +
                "file_id varchar(256)," +
                "user_id bigint," +
                "title varchar(128)," +
                "voice_link varchar(256)," +
                "duration int," +
                "saved_time bigint," +
                "is_deleted bool" +
                ");"

        object Instance {
            private var mInstance: VoiceStorage? = null

            fun create(tableName: String, databaseHelper: DatabaseHelper) {
                if (mInstance == null) {
                    mInstance = Base(tableName, databaseHelper)
                }
            }

            operator fun invoke() : VoiceStorage {
                return mInstance ?: throw Exception()
            }
        }
    }
}