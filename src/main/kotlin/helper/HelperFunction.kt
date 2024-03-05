package helper

import io.ktor.server.application.*
import io.ktor.util.pipeline.*
suspend fun <T> handleQueryParam(
    param: String?,
    defaultParam: T,
    context: PipelineContext<Unit, ApplicationCall>,
    nullAction: suspend PipelineContext<Unit, ApplicationCall>.() -> Unit,
    converterFunction: suspend PipelineContext<Unit, ApplicationCall>.(param: String) -> T
): T {
    return if (param != null) converterFunction.invoke(context, param) else {
        nullAction.invoke(context)
        return defaultParam
    }
}