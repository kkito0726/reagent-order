package kkito.reagent_order.error

import org.springframework.http.HttpStatus

data class NotFoundException (
    override val errorCode: ErrorCode
) : HttpException(HttpStatus.NOT_FOUND, errorCode)