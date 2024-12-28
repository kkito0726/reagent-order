package kkito.reagent_order.error

import org.springframework.http.HttpStatus

data class ConflictException(
    override val httpStatus: HttpStatus = HttpStatus.CONFLICT,
    override val errorCode: ErrorCode
) : HttpException(httpStatus, errorCode)