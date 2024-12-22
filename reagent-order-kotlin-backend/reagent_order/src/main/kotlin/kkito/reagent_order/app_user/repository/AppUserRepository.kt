package kkito.reagent_order.app_user.repository

import kkito.reagent_order.app_user.entity.AppUserEntity

interface AppUserRepository {
    fun createAppUser(appUserEntity: AppUserEntity)
}