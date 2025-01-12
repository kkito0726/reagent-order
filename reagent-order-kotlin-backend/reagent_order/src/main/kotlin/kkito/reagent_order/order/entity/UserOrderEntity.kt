package kkito.reagent_order.order.entity

import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "user_order")
data class UserOrderEntity(
    val id: Long,
    val appUserId: String,
    val title: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val deletedAt: LocalDateTime? = null
)