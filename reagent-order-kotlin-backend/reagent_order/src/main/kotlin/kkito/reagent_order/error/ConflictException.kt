package kkito.reagent_order.error

import org.springframework.http.HttpStatus

data class ConflictException(
    override val errorCode: ErrorCode
) : HttpException(HttpStatus.CONFLICT, errorCode)