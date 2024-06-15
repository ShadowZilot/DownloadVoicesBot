package domain

import logs.LogLevel
import logs.Logging

fun Logging.ConsoleLog.logExceptionToAll(throwable: Throwable) {
    logToAll(buildString {
        appendLine(throwable.message)
        appendLine(throwable.stackTraceToString())
    }, LogLevel.Exception)
}