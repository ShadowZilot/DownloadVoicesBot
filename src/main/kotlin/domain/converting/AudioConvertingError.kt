package domain.converting

class AudioConvertingError(
    errorMessage: String?
) : RuntimeException(buildString {
    appendLine("Error while converting audio")
    appendLine(errorMessage)
})