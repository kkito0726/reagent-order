package kkito.reagent_order.app_user.service

import kkito.reagent_order.app_user.entity.AppUserEntity
import kkito.reagent_order.app_user.repository.AppUserRepository
import kkito.reagent_order.app_user.service.spec.AppUserCreateSpec
import kkito.reagent_order.app_user.service.spec.AppUserDeleteSpec
import kkito.reagent_order.app_user.service.spec.AppUserUpdateSpec
import kkito.reagent_order.app_user.value.*
import kkito.reagent_order.error.ErrorCode
import kkito.reagent_order.error.NotFoundException
import kkito.reagent_order.login.service.LoginService
import kkito.reagent_order.login.value.LoginResponse
import org.springframework.stereotype.Service

@Service
open class AppUserService(
    private val appUserCreateSpec: AppUserCreateSpec,
    private val loginService: LoginService,
    private val appUserUpdateSpec: AppUserUpdateSpec,
    private val appUserDeleteSpec: AppUserDeleteSpec,
    private val appUserRepository: AppUserRepository
) {
    fun createAppUser(appUserEntity: AppUserEntity): LoginResponse {
        // アプリユーザが重複していないかの確認
        appUserCreateSpec.check(appUserEntity)

        // 永続化
        appUserRepository.createAppUser(appUserEntity)

        // 作成したユーザーでログイン処理
        return loginService.getAppUser(
            Email(appUserEntity.email.value),
            Password(appUserEntity.password.value)
        )
    }

    fun getAppUser(appUserId: AppUserId): AppUserEntity {
        return appUserRepository.getAppUser(appUserId)
            ?: throw NotFoundException(ErrorCode.E0006)
    }

    fun updateAppUser(
        newAppUserDto: AppUserDto,
        authAppUserEntity: AppUserEntity
    ): AppUserResponse {
        // 更新権限と更新内容が重複してないか確認
        appUserUpdateSpec.check(newAppUserDto, authAppUserEntity)

        // 永続化
        appUserRepository.updateAppUser(newAppUserDto)
        return AppUserResponse(
            newAppUserDto.id.toString(),
            newAppUserDto.appUserName.value,
            newAppUserDto.email.value,
            null,
            newAppUserDto.role
        )
    }

    fun deleteAppUser(appUserId: AppUserId, authAppUserEntity: AppUserEntity) {
        appUserDeleteSpec.check(appUserId, authAppUserEntity)
        appUserRepository.deleteAppUser(appUserId)
    }
}