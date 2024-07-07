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
            "ffmpeg",
            "-i", "$mId.mp4",
            "-vn", "-acodec", "libmp3lame", "-ab", "128k",
            "$mId.mp3"
        )
        val errorFile = File(sBasePath, "error-$mId.txt")
        val outputFile = File(sBasePath, "$mId.mp3")
        if (!errorFile.exists()) errorFile.createNewFile()
        processBuilder.directory(File(sBasePath))
        processBuilder.redirectError(errorFile)
        try {
            val process = processBuilder.start()
            val result = process.waitFor()
            return if (result == 0) {
                outputFile.readBytes()
            } else {
                val errorText = errorFile.readText()
                if (errorText.contains("Output file does not contain any stream")) {
                    throw NoAudioInMp4()
                } else {
                    throw AudioConvertingError(errorText)
                }
            }
        } catch (e: Exception) {
            Logging.ConsoleLog.logToFile(e.message ?: "", LogLevel.Exception)
            if (e is AudioConvertingError) {
                Logging.ConsoleLog.logToChat(
                    "Error while converting audio, see more in file",
                    LogLevel.Exception
                )
            } else {
                Logging.ConsoleLog.logToChat(e.message ?: "", LogLevel.Exception)
            }
            throw e
        } finally {
            outputFile.delete()
            inputFile.delete()
            errorFile.delete()
        }
    }
}