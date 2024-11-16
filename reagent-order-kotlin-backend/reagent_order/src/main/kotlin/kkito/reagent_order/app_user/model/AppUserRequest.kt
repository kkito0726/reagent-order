package kkito.reagent_order.app_user.model

data class AppUserRequest(
    val appUserName: String,
    val email: String,
    val password: String
)