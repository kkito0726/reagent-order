package kkito.reagent_order.app_user.repository

import kkito.reagent_order.app_user.entity.AppUserEntity
import kkito.reagent_order.app_user.value.AppUserDto
import kkito.reagent_order.app_user.value.AppUserId

interface AppUserRepository {
    fun createAppUser(appUserEntity: AppUserEntity)
    fun getAppUser(appUserId: AppUserId): AppUserEntity?
    fun updateAppUser(newAppUserDto: AppUserDto): Int
    fun deleteAppUser(appUserId: AppUserId)
}