package kkito.reagent_order.app_user.service

import kkito.reagent_order.app_user.entity.AppUserEntity
import kkito.reagent_order.app_user.value.AppUserResponse
import kkito.reagent_order.app_user.repository.AppUserRepository
import kkito.reagent_order.app_user.service.spec.AppUserCreateSpec
import org.springframework.stereotype.Service

@Service
open class AppUserService(
    private val appUserCreateSpec: AppUserCreateSpec,
    private val appUserRepository: AppUserRepository
) {
    fun createAppUser(appUserEntity: AppUserEntity): AppUserResponse {
        // TODO: アプリユーザが重複していないかの確認
        appUserCreateSpec.check(appUserEntity)

        // TODO: 永続化
        appUserRepository.createAppUser(appUserEntity)
        return AppUserResponse(
            appUserEntity.id.toString(),
            appUserEntity.appUserName.toString(),
            appUserEntity.email.toString(),
            appUserEntity.password.value,
            appUserEntity.createdAt,
            appUserEntity.deletedAt
        )
    }
}