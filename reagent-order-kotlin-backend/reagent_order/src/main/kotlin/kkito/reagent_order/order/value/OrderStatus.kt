package kkito.reagent_order.order.value

import kkito.reagent_order.error.ErrorCode
import kkito.reagent_order.error.InternalServerError

enum class OrderStatus(val value: String, val description: String) {
    PENDING("pending", "承認待ち"),
    COMPLETED("completed", "発注完了"),
    CANCELED("canceled", "キャンセル");

    companion object {
        fun fromValue(value: String): OrderStatus {
            return entries.find { it.value == value }
                ?: throw InternalServerError(ErrorCode.E0012)
        }
    }
}