package kkito.reagent_order.app_user.model

import java.time.LocalDateTime

data class AppUserEntity(
    val id: AppUserId,
    val appUserName: AppUserName,
    val email: Email,
    val password: Password,
    val createdAt: LocalDateTime,
    var deletedAt: LocalDateTime? = null
)