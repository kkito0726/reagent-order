package kkito.reagent_order.order.value

import java.time.LocalDateTime

data class OrderDetailResponse(
    val orderDetailId: Long,
    val reagentName: String,
    val url: String?,
    val count: Int,
    val status: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
)