package kkito.reagent_order.order.entity

import kkito.reagent_order.app_user.value.AppUserName
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "user_order")
data class OrderEntity(
    val id: Long,
    val appUserName: AppUserName,
    val title: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val orderDetailEntities: List<OrderDetailEntity>
)