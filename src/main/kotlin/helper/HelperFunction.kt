package helper

import io.ktor.server.application.*
import io.ktor.util.pipeline.*

suspend fun <T> PipelineContext<Unit, ApplicationCall>.handleQueryParam(
    paramKey: String,
    defaultParam: T,
    nullAction: suspend PipelineContext<Unit, ApplicationCall>.() -> Unit,
    converterFunction: suspend PipelineContext<Unit, ApplicationCall>.(param: String) -> T
): T {
    val param = call.parameters[paramKey]
    return if (param != null) converterFunction.invoke(this, param) else {
        nullAction.invoke(this)
        return defaultParam
    }
}