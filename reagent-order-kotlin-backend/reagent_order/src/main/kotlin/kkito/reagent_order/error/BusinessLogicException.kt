package kkito.reagent_order.error

import org.springframework.http.HttpStatus

data class BusinessLogicException(
    override val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    override val errorCode: ErrorCode
) : HttpException(httpStatus, errorCode)