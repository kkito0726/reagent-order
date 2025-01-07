package kkito.reagent_order.error

import org.springframework.http.HttpStatus

data class InternalServerError(
    override val errorCode: ErrorCode
) : HttpException(HttpStatus.INTERNAL_SERVER_ERROR, errorCode)