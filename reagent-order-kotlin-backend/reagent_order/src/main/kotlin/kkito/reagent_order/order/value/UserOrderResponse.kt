package kkito.reagent_order.order.value

import java.time.LocalDateTime

data class UserOrderResponse(
    val id: Long,
    val title: String?,
    val createdAt: LocalDateTime,
    val orderDetails: List<OrderDetailResponse>
)