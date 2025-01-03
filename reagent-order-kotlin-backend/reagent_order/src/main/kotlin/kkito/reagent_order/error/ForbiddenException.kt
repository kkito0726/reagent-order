package kkito.reagent_order.error

import org.springframework.http.HttpStatus

data class ForbiddenException(
    override val httpStatus: HttpStatus = HttpStatus.NOT_FOUND,
    override val errorCode: ErrorCode
) : HttpException(httpStatus, errorCode)