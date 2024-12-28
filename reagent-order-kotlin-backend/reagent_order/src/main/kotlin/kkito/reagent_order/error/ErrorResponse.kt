package kkito.reagent_order.error

import org.springframework.http.HttpStatus

data class ErrorResponse(
    val errorCode: String,
    val message: String,
    val httpStatus: HttpStatus
)