package domain.converting

import logs.LogLevel
import logs.Logging
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
            val errorFile = File(sBasePath, "error-$mId.txt")
            if (!errorFile.exists()) errorFile.createNewFile()
            processBuilder.directory(File(sBasePath))
            processBuilder.redirectError(errorFile)
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
                    throw AudioConvertingError(errorFile.readText())
                }.also {
                    Logging.ConsoleLog.logToFile("End convert file id = $mId", LogLevel.Info)
                }
            } catch (e: Exception) {
                inputFile.delete()
                Logging.ConsoleLog.logToFile(e.message ?: "", LogLevel.Exception)
                if (e is AudioConvertingError) {
                    Logging.ConsoleLog.logToChat(
                        "Error while converting audio, see more in file",
                        LogLevel.Exception
                    )
                } else {
                    Logging.ConsoleLog.logToChat(e.message ?: "", LogLevel.Exception)
                }
                throw AudioConvertingError(e.message)
            } finally {
                errorFile.delete()
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
            val errorFile = File(sBasePath, "error-$mId.txt")
            if (!errorFile.exists()) errorFile.createNewFile()
            val processBuilder = ProcessBuilder("ffmpeg", "-i", "$mId.oga", "$mId.mp3")
            processBuilder.directory(File(sBasePath))
            processBuilder.redirectError(errorFile)
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
                    throw AudioConvertingError(errorFile.readText())
                }.also {
                    Logging.ConsoleLog.logToFile("End convert file id = $mId", LogLevel.Info)
                }
            } catch (e: Exception) {
                inputFile.delete()
                Logging.ConsoleLog.logToFile(e.message ?: "", LogLevel.Exception)
                if (e is AudioConvertingError) {
                    Logging.ConsoleLog.logToChat(
                        "Error while converting audio, see more in file",
                        LogLevel.Exception
                    )
                } else {
                    Logging.ConsoleLog.logToChat(e.message ?: "", LogLevel.Exception)
                }
                throw AudioConvertingError(e.message)
            } finally {
                errorFile.delete()
            }
        }
    }
}