package kkito.reagent_order.error

import org.springframework.http.HttpStatus

data class ForbiddenException(
    override val errorCode: ErrorCode
) : HttpException(HttpStatus.FORBIDDEN, errorCode)