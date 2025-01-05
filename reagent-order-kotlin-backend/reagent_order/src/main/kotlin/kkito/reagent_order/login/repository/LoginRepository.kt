package kkito.reagent_order.login.repository

import kkito.reagent_order.app_user.entity.AppUserEntity
import kkito.reagent_order.app_user.value.AppUserId
import kkito.reagent_order.app_user.value.Email
import kkito.reagent_order.app_user.value.Password

interface LoginRepository {
    fun getAppUser(email: Email, password: Password): AppUserEntity
    fun getAppUserByEmail(email: Email): AppUserEntity
    fun getAppUserByAppUserId(appUserId: AppUserId): AppUserEntity
}