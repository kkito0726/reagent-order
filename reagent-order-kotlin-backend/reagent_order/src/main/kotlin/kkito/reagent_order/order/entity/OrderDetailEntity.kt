package kkito.reagent_order.order.entity

import kkito.reagent_order.order.value.OrderStatus
import kkito.reagent_order.order.value.ReagentCount
import kkito.reagent_order.order.value.ReagentName
import java.time.LocalDateTime

data class OrderDetailEntity(
    val id: Long,
    val reagentName: ReagentName,
    val url: String?,
    val count: ReagentCount,
    val status: OrderStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
)