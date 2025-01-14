package kkito.reagent_order.order.value

import kkito.reagent_order.error.BusinessLogicException
import kkito.reagent_order.error.ErrorCode
import org.springframework.http.HttpStatus

data class ReagentCount(val value: Int) {
    init {
        if (value <= 0) {
            throw BusinessLogicException(HttpStatus.BAD_REQUEST, ErrorCode.E0011)
        }
    }

    override fun toString(): String {
        return value.toString()
    }
}
