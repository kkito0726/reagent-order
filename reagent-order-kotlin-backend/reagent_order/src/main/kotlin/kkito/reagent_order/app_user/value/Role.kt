package kkito.reagent_order.app_user.value

import kkito.reagent_order.error.ErrorCode
import kkito.reagent_order.error.InternalServerError
import kkito.reagent_order.order.value.OrderStatus

enum class Role(val value: String, val description: String) {
    USER("USER", "アプリユーザー"),
    ADMIN("ADMIN", "管理者"),
    SYSTEM("SYSTEM", "システム管理者");

    companion object {
        fun fromValue(value: String): Role {
            return entries.find { it.value == value }
                ?: throw InternalServerError(ErrorCode.E0012)
        }
    }
}
