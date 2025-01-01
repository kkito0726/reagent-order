package kkito.reagent_order.login.value

import kkito.reagent_order.app_user.entity.AppUserEntity

data class LoginResponse(
    val appUserEntity: AppUserEntity,
    val loginId: String,
)