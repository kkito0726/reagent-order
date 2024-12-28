package kkito.reagent_order.app_user.value

import kkito.reagent_order.error.BusinessLogicException
import kkito.reagent_order.error.ErrorCode
import org.springframework.http.HttpStatus

@JvmInline
value class Password(val value: String) {
    init {
        if (value.length <= 5) {
            throw BusinessLogicException(HttpStatus.BAD_REQUEST, ErrorCode.E0003)
        }
    }
}