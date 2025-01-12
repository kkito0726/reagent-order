package kkito.reagent_order.order.value

import kkito.reagent_order.app_user.value.AppUserId

data class UserOrderRequest(
    val title: String?,
    val orderDetails: List<OrderDetailRequest>
)