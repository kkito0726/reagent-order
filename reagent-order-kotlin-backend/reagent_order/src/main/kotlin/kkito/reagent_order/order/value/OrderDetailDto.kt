package kkito.reagent_order.order.value

import java.time.LocalDateTime

data class OrderDetailDto(
    val reagentName: ReagentName,
    val url: String?,
    val count: Int,
    val status: OrderStatus,
    val createdAt: LocalDateTime = LocalDateTime.now()
)