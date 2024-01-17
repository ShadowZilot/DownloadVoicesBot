package domain.converting

import logs.LogLevel
import logs.Logging
import okio.ByteString.Companion.toByteString
import sBasePath
import java.io.File


interface AudioConverter {

    fun convertedBytes(): ByteArray

    class Mp3ToOgaBytes(
        private val mId: Long,
        private val mInputBytes: ByteArray
    ) : AudioConverter {
        override fun convertedBytes(): ByteArray {
            Logging.ConsoleLog.logToFile("Begin convert file id = $mId", LogLevel.Info)
            val inputFile = File(sBasePath, "$mId.mp3")
            if (!inputFile.exists()) inputFile.createNewFile()
            inputFile.writeBytes(mInputBytes)
            val processBuilder = ProcessBuilder(
                "ffmpeg", "-i", "$mId.mp3",
                "-c:a", "libopus", "$mId.opus"
            )
            processBuilder.directory(File(sBasePath))
            processBuilder.redirectErrorStream(true)
            try {
                val process = processBuilder.start()
                val result = process.waitFor()
                return if (result == 0) {
                    val outputFile = File(sBasePath, "$mId.opus")
                    val outputBytes = outputFile.readBytes()
                    outputFile.delete()
                    inputFile.delete()
                    outputBytes
                } else {
                    inputFile.delete()
                    throw AudioConvertingError()
                }.also {
                    Logging.ConsoleLog.logToFile("End convert file id = $mId", LogLevel.Info)
                }
            } catch (e: Exception) {
                inputFile.delete()
                Logging.ConsoleLog.logToFile(e.message ?: "", LogLevel.Exception)
                Logging.ConsoleLog.logToChat(e.message ?: "", LogLevel.Exception)
                throw AudioConvertingError()
            }
        }
    }

    class OgaToMp3Bytes(
        private val mId: Long,
        private val mInputBytes: ByteArray
    ) : AudioConverter {

        override fun convertedBytes(): ByteArray {
            Logging.ConsoleLog.logToFile("Begin convert file id = $mId", LogLevel.Info)
            val inputFile = File(sBasePath, "$mId.oga")
            if (!inputFile.exists()) inputFile.createNewFile()
            inputFile.writeBytes(mInputBytes)
            val processBuilder = ProcessBuilder("ffmpeg", "-i", "$mId.oga", "$mId.mp3")
            processBuilder.directory(File(sBasePath))
            processBuilder.redirectErrorStream(true)
            try {
                val process = processBuilder.start()
                val result = process.waitFor()
                return if (result == 0) {
                    val outputFile = File(sBasePath, "$mId.mp3")
                    val outputBytes = outputFile.readBytes()
                    outputFile.delete()
                    inputFile.delete()
                    outputBytes
                } else {
                    inputFile.delete()
                    throw AudioConvertingError()
                }.also {
                    Logging.ConsoleLog.logToFile("End convert file id = $mId", LogLevel.Info)
                }
            } catch (e: Exception) {
                inputFile.delete()
                Logging.ConsoleLog.logToFile(e.message ?: "", LogLevel.Exception)
                Logging.ConsoleLog.logToChat(e.message ?: "", LogLevel.Exception)
                throw AudioConvertingError()
            }
        }
    }
}