package kkito.reagent_order.order.value

import kkito.reagent_order.app_user.value.AppUserId
import java.time.LocalDateTime

data class OrderDto(
    val appUserId: AppUserId,
    val title: String?,
    val orderDetailDtoList: List<OrderDetailDto>,
    val createdAt: LocalDateTime = LocalDateTime.now()
)