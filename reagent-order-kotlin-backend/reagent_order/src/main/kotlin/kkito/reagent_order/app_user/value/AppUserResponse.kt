package kkito.reagent_order.app_user.value

import java.time.LocalDateTime

data class AppUserResponse(
    val id: String,
    val appUserName: String,
    val email: String,
    val createdAt: LocalDateTime?,
)