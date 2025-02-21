package kkito.reagent_order.app_user.service.spec

import kkito.reagent_order.app_user.entity.AppUserEntity
import kkito.reagent_order.app_user.repository.AppUserCheckDuplicate
import kkito.reagent_order.app_user.repository.AppUserRepository
import kkito.reagent_order.app_user.value.AppUserDto
import kkito.reagent_order.app_user.value.Role
import kkito.reagent_order.error.ConflictException
import kkito.reagent_order.error.ErrorCode
import kkito.reagent_order.error.ForbiddenException
import kkito.reagent_order.error.NotFoundException
import org.springframework.stereotype.Service

@Service
class AppUserUpdateSpec(
    private val appUserCheckDuplicate: AppUserCheckDuplicate,
    private val appUserRepository: AppUserRepository
) {
    fun check(newAppUserDto: AppUserDto, authAppUserEntity: AppUserEntity) {
        // ログインユーザーとパスパラメータで指定した更新対象ユーザのIDをチェックする
        // 自身の情報と管理ユーザーの変更のみ有効
        if (authAppUserEntity.id != newAppUserDto.id && authAppUserEntity.role !in listOf(
                Role.ADMIN,
                Role.SYSTEM
            )
        ) {
            throw ForbiddenException(ErrorCode.E0009)
        }

        val ordAppUserEntity = appUserRepository.getAppUser(newAppUserDto.id)
            ?: throw NotFoundException(ErrorCode.E0006)

        // 権限を変えようとしているのが一般ユーザーの場合は権限エラー
        if (newAppUserDto.role != ordAppUserEntity.role && authAppUserEntity.role == Role.USER
        ) {
            throw ForbiddenException(ErrorCode.E0009)
        }

        // ユーザーネームを変更しようとしている場合重複チェックをする
        if (newAppUserDto.appUserName != ordAppUserEntity.appUserName) {
            if (appUserCheckDuplicate.isDuplicateAppUserName(newAppUserDto.appUserName)) {
                throw ConflictException(ErrorCode.E0004)
            }
        }

        // メールアドレスを変更しようとしている場合重複チェックをする
        if (newAppUserDto.email != ordAppUserEntity.email) {
            if (appUserCheckDuplicate.isDuplicateEmail(newAppUserDto.email)) {
                throw ConflictException(ErrorCode.E0005)
            }
        }
    }
}
