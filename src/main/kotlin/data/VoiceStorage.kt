package data

import helpers.storage.StorageShell
import helpers.storage.jdbc_wrapping.DatabaseHelper
import java.sql.ResultSet
import java.sql.SQLException

interface VoiceStorage : StorageShell {

    fun insertVoice(voice: Voice)

    fun updateVoiceTitle(id: Long, title: String)

    fun updateVoiceDeletion(id: Long, voiceStatus: VoiceStatus)

    fun updateVoiceName(id: Long, name: String)

    fun updateDownloadLink(id: Long, voiceLink: String)

    fun voiceById(id: Long, withStatus: VoiceStatus = VoiceStatus.NORMAL): Voice

    fun voiceByIdInAnyStatus(id: Long, exceptionStatus: VoiceStatus): Voice

    fun deleteVoice(id: Long)

    fun voiceFileId(id: Long): String

    fun voiceFileIdMp3(id: Long): String

    fun secretVoiceById(id: Long): Voice

    fun voiceOgaUpdateFileIdAndLink(id: Long, fileId: String, downloadLink: String)

    fun voiceMp3UpdateFileIdAndLink(id: Long, fileId: String, downloadLink: String)

    fun voiceMp3IdUpdate(id: Long, fileId: String)

    fun secretVoiceFileId(id: Long): String

    fun voicesList(userId: Long, offset: Int, search: String): List<Voice>

    fun secretVoiceList(offset: Int, search: String): List<Voice>

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
                "UPDATE $mTableName SET `title` = ? WHERE `id` = ?",
                title, id
            )
        }

        override fun updateVoiceDeletion(id: Long, voiceStatus: VoiceStatus) {
            mDatabase.executeQueryWithoutResult(
                "UPDATE $mTableName SET `voice_status` = ? WHERE `id` = ?",
                voiceStatus.statusCode, id
            )
        }

        override fun updateVoiceName(id: Long, name: String) {
            mDatabase.executeQueryWithoutResult(
                "UPDATE $mTableName SET `title` = ? WHERE `id` = ?",
                name, id
            )
        }

        override fun updateDownloadLink(id: Long, voiceLink: String) {
            mDatabase.executeQueryWithoutResult(
                "UPDATE $mTableName SET `voice_link` = ? WHERE `id` = ?",
                voiceLink, id
            )
        }

        override fun voiceById(id: Long, withStatus: VoiceStatus): Voice {
            var voice: Voice? = null
            mDatabase.executeQuery(
                "SELECT * FROM $mTableName WHERE `id` = ? AND `voice_status` = ?",
                { item, _ ->
                    voice = try {
                        Voice(item)
                    } catch (e: SQLException) {
                        null
                    }
                },
                id, withStatus.statusCode
            )
            return voice ?: throw VoiceNotFound(id)
        }

        override fun voiceByIdInAnyStatus(id: Long, exceptionStatus: VoiceStatus): Voice {
            var voice: Voice? = null
            mDatabase.executeQuery(
                "SELECT * FROM $mTableName WHERE `id` = ? AND `voice_status` != ?",
                { item, _ ->
                    voice = try {
                        Voice(item)
                    } catch (e: SQLException) {
                        null
                    }
                },
                id, exceptionStatus.statusCode
            )
            return voice ?: throw VoiceNotFound(id)
        }

        override fun voiceFileIdMp3(id: Long): String {
            var voiceFileId: String? = null
            mDatabase.executeQuery(
                "SELECT file_mp3_id FROM $mTableName WHERE `id` = ?" +
                        " AND (`voice_status` = ? OR `voice_status` = ?)",
                { item, _ ->
                    voiceFileId = try {
                        item.getString("file_mp3_id")
                    } catch (e: SQLException) {
                        null
                    }
                },
                id, VoiceStatus.NORMAL.statusCode, VoiceStatus.CREATING.statusCode
            )
            return voiceFileId ?: throw VoiceNotFound(id)
        }

        override fun deleteVoice(id: Long) {
            mDatabase.executeQueryWithoutResult(
                "UPDATE $mTableName SET `voice_status` = ? WHERE `id` = ?", id, VoiceStatus.DELETED.statusCode
            )
        }

        override fun voiceFileId(id: Long): String {
            var voiceFileId: String? = null
            mDatabase.executeQuery(
                "SELECT file_oga_id FROM $mTableName WHERE `id` = ? " +
                        "AND (`voice_status` = ? OR `voice_status` = ?)",
                { item, _ ->
                    voiceFileId = try {
                        item.getString("file_oga_id")
                    } catch (e: SQLException) {
                        null
                    }
                },
                id, VoiceStatus.NORMAL.statusCode, VoiceStatus.CREATING.statusCode
            )
            return voiceFileId ?: throw VoiceNotFound(id)
        }

        override fun secretVoiceById(id: Long): Voice {
            var voice: Voice? = null
            mDatabase.executeQuery(
                "SELECT * FROM $mTableName WHERE `id` = ?",
                { item, _ ->
                    voice = try {
                        Voice(item)
                    } catch (e: SQLException) {
                        null
                    }
                },
                id
            )
            return voice ?: throw VoiceNotFound(id)
        }

        override fun voiceOgaUpdateFileIdAndLink(id: Long, fileId: String, downloadLink: String) {
            mDatabase.executeQueryWithoutResult(
                "UPDATE $mTableName SET `file_oga_id` = ?, `voice_link` = ? WHERE `id` = ?",
                fileId, downloadLink, id
            )
        }

        override fun voiceMp3UpdateFileIdAndLink(id: Long, fileId: String, downloadLink: String) {
            mDatabase.executeQueryWithoutResult(
                "UPDATE $mTableName SET `file_mp3_id` = ?, `voice_link` = ? WHERE `id` = ?",
                fileId, downloadLink, id
            )
        }

        override fun voiceMp3IdUpdate(id: Long, fileId: String) {
            mDatabase.executeQueryWithoutResult(
                "UPDATE $mTableName SET `file_mp3_id` = ? WHERE `id` = ?",
                fileId, id
            )
        }

        override fun secretVoiceFileId(id: Long): String {
            var voiceFileId: String? = null
            mDatabase.executeQuery(
                "SELECT file_oga_id FROM $mTableName WHERE `id` = ?",
                { item, _ ->
                    voiceFileId = try {
                        item.getString("file_oga_id")
                    } catch (e: SQLException) {
                        null
                    }
                },
                id
            )
            return voiceFileId ?: throw VoiceNotFound(id)
        }

        override fun voicesList(userId: Long, offset: Int, search: String): List<Voice> {
            val voices = mutableListOf<Voice>()
            val handlingAction: (item: ResultSet, isNext: Boolean) -> Unit = { item, isNext ->
                var next = isNext
                while (next) {
                    voices.add(Voice(item))
                    next = item.next()
                }
            }
            if (search.isNotEmpty()) {
                mDatabase.executeQuery(
                    "SELECT * FROM $mTableName WHERE `user_id` = ? AND" +
                            " `voice_status` = ? AND INSTR(`title`, ?) > 0 ORDER BY `saved_time` DESC LIMIT 50 OFFSET ?",
                    handlingAction,
                    userId, VoiceStatus.NORMAL.statusCode, search, offset
                )
            } else {
                mDatabase.executeQuery(
                    "SELECT * FROM $mTableName WHERE `user_id` = ? AND" +
                            " `voice_status` = ? ORDER BY `saved_time` DESC LIMIT 50 OFFSET ?",
                    handlingAction,
                    userId, VoiceStatus.NORMAL.statusCode, offset
                )
            }
            return voices
        }

        override fun secretVoiceList(offset: Int, search: String): List<Voice> {
            val voices = mutableListOf<Voice>()
            val handlingAction: (item: ResultSet, isNext: Boolean) -> Unit = { item, isNext ->
                var next = isNext
                while (next) {
                    voices.add(Voice(item))
                    next = item.next()
                }
            }
            if (search.isNotEmpty()) {
                mDatabase.executeQuery(
                    "SELECT * FROM $mTableName WHERE INSTR(`title`, ?) > 0 ORDER BY" +
                            " `saved_time` DESC LIMIT 50 OFFSET ?",
                    handlingAction,
                    search, offset
                )
            } else {
                mDatabase.executeQuery(
                    "SELECT * FROM $mTableName ORDER BY" +
                            " `saved_time` DESC LIMIT 50 OFFSET ?",
                    handlingAction, offset
                )
            }
            return voices
        }

        override fun lastVoiceId(userId: Long): Long {
            var lastId = -1L
            mDatabase.executeQuery(
                "SELECT id FROM $mTableName WHERE `user_id` = ? ORDER BY `saved_time` DESC LIMIT 1",
                { item, _ ->
                    lastId = try {
                        item.getLong("id")
                    } catch (e: SQLException) {
                        -1
                    }
                },
                userId
            )
            return lastId
        }


        override fun tableName() = mTableName

        override fun tableSchema() = "CREATE TABLE $mTableName(" +
                "id bigint primary key auto_increment," +
                "file_oga_id varchar(256)," +
                "file_mp3_id varchar(256)," +
                "user_id bigint," +
                "title varchar(128)," +
                "voice_link varchar(256)," +
                "duration int," +
                "saved_time bigint," +
                "voice_status int" +
                ");"

        object Instance {
            private var mInstance: VoiceStorage? = null

            fun create(tableName: String, databaseHelper: DatabaseHelper) {
                if (mInstance == null) {
                    mInstance = Base(tableName, databaseHelper)
                }
            }

            operator fun invoke(): VoiceStorage {
                return mInstance ?: throw Exception()
            }
        }
    }
}