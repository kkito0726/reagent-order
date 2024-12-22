package kkito.reagent_order.app_user.entity

import kkito.reagent_order.app_user.value.AppUserId
import kkito.reagent_order.app_user.value.AppUserName
import kkito.reagent_order.app_user.value.Email
import kkito.reagent_order.app_user.value.Password
import java.time.LocalDateTime

data class AppUserEntity(
    val id: AppUserId,
    val appUserName: AppUserName,
    val email: Email,
    val password: Password,
    val createdAt: LocalDateTime,
    var deletedAt: LocalDateTime? = null
)