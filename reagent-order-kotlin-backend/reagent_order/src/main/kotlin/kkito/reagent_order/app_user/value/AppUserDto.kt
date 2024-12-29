package kkito.reagent_order.app_user.value

data class AppUserDto (
    val id: AppUserId,
    val appUserName: AppUserName,
    val email: Email,
    val password: Password,
)