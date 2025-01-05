package kkito.reagent_order.login.value

import kkito.reagent_order.app_user.entity.AppUserEntity
import kkito.reagent_order.app_user.value.AppUserResponse

data class LoginResponse(
    val appUserEntity: AppUserResponse,
    val loginId: String,
)