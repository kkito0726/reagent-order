package kkito.reagent_order.app_user.value

import kkito.reagent_order.error.BusinessLogicException
import kkito.reagent_order.error.ErrorCode
import org.springframework.http.HttpStatus

data class Email(val value: String) {
    init {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
        if (!emailRegex.matches(value)) {
            throw BusinessLogicException(HttpStatus.BAD_REQUEST, ErrorCode.E0002)
        }
    }

    override fun toString(): String = value
}