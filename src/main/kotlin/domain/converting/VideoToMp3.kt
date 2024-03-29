package domain.converting

import logs.LogLevel
import logs.Logging
import sBasePath
import java.io.File

class VideoToMp3(
    private val mId: String,
    private val mInputBytes: ByteArray
) : AudioConverter {

    override fun convertedBytes(): ByteArray {
        Logging.ConsoleLog.logToFile("Extract sound from video file id = $mId", LogLevel.Info)
        val inputFile = File(sBasePath, "$mId.mp4")
        if (!inputFile.exists()) inputFile.createNewFile()
        inputFile.writeBytes(mInputBytes)
        val processBuilder = ProcessBuilder(
            "ffmpeg", "-i", "$mId.mp4",
            "-q:a", "0", "-map", "a", "$mId.mp3"
        )
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
                Logging.ConsoleLog.logToFile("End extract sound from video file id = $mId", LogLevel.Info)
            }
        } catch (e: Exception) {
            inputFile.delete()
            Logging.ConsoleLog.logToFile(e.message ?: "", LogLevel.Exception)
            Logging.ConsoleLog.logToChat(e.message ?: "", LogLevel.Exception)
            throw AudioConvertingError()
        }
    }
}