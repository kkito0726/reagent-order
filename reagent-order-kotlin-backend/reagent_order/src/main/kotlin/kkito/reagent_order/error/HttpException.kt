package kkito.reagent_order.error

import org.springframework.http.HttpStatus

open class HttpException : RuntimeException {

    open val httpStatus: HttpStatus
    open val errorCode: ErrorCode

    constructor(httpStatus: HttpStatus, errorCode: ErrorCode) : super() {
        this.httpStatus = httpStatus
        this.errorCode = errorCode
    }


    constructor(message: String, httpStatus: HttpStatus, errorCode: ErrorCode) : super(message) {
        this.httpStatus = httpStatus
        this.errorCode = errorCode
    }

    constructor(
        message: String,
        cause: Throwable,
        enableSuppression: Boolean,
        writableStackTrace: Boolean,
        httpStatus: HttpStatus,
        errorCode: ErrorCode
    ) : super(message, cause, enableSuppression, writableStackTrace) {
        this.httpStatus = httpStatus
        this.errorCode = errorCode
    }
}

