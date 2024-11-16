package kkito.reagent_order.app_user.repository

import kkito.reagent_order.app_user.model.AppUserEntity

interface AppUserRepository {
    fun createAppUser(appUserEntity: AppUserEntity)
}