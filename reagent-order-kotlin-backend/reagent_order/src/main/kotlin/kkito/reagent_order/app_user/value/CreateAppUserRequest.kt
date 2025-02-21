package kkito.reagent_order.app_user.value

data class CreateAppUserRequest (
    val appUserName: String,
    val email: String,
    val password: String,
)