package kkito.reagent_order.app_user.value

import java.time.LocalDateTime
import java.util.UUID

data class AppUserResponse(
    val id: String,
    val appUserName: String,
    val email: String,
    val password: String,
    val createdAt: LocalDateTime,
    var deletedAt: LocalDateTime? = null
)