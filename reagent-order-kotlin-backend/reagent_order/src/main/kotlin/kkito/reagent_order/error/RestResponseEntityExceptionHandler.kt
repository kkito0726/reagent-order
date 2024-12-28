package kkito.reagent_order.error

import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(HttpException::class)
    fun handleHttpException(exception: HttpException, request: WebRequest) : ResponseEntity<Any>? {
        val errorResponse = ErrorResponse(
            exception.errorCode.code,
            exception.errorCode.message,
        )
        println("""
            Error Code: ${errorResponse.errorCode}
            Error Message: ${errorResponse.message}
            """.trimIndent())
        return this.handleExceptionInternal(
            exception,
            errorResponse,
            HttpHeaders(),
            exception.httpStatus,
            request
        )
    }
}