package kkito.reagent_order.app_user.value

data class UpdateAppUserRequest(
    val appUserName: String,
    val email: String,
    val password: String,
    val role: String?,
)