package kkito.reagent_order.app_user.value

import kkito.reagent_order.error.BusinessLogicException
import kkito.reagent_order.error.ErrorCode
import org.springframework.http.HttpStatus

data class AppUserName(val value: String) {
    init {
        if (value.length !in 3..15) {
            throw BusinessLogicException(HttpStatus.BAD_REQUEST, ErrorCode.E0001)
        }
    }

    companion object {
        fun of(value: String): AppUserName {
            return AppUserName(value)
        }
    }

    override fun toString(): String {
        return value
    }
}