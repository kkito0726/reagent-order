package kkito.reagent_order.order.value

enum class OrderStatus(val value: String, val description: String) {
    PENDING("pending", "承認待ち"),
    COMPLETED("completed", "発注完了"),
    CANCELED("canceled", "キャンセル"),
}