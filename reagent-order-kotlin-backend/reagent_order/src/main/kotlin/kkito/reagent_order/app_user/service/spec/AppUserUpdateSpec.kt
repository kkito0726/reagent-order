package kkito.reagent_order.app_user.service.spec

import kkito.reagent_order.app_user.entity.AppUserEntity
import kkito.reagent_order.app_user.repository.AppUserCheckDuplicate
import kkito.reagent_order.app_user.value.AppUserDto
import kkito.reagent_order.error.ConflictException
import kkito.reagent_order.error.ErrorCode
import kkito.reagent_order.error.ForbiddenException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class AppUserUpdateSpec(private val appUserCheckDuplicate: AppUserCheckDuplicate) {
    fun check(newAppUserDto: AppUserDto, authAppUserEntity: AppUserEntity) {
        // ログインユーザーとパスパラメータで指定した更新対象ユーザのIDをチェックする
        if (authAppUserEntity.id != newAppUserDto.id) {
            throw ForbiddenException(HttpStatus.FORBIDDEN, ErrorCode.E0009)
        }

        // ユーザーネームを変更しようとしている場合重複チェックをする
        if (newAppUserDto.appUserName != authAppUserEntity.appUserName) {
            if (appUserCheckDuplicate.isDuplicateAppUserName(newAppUserDto.appUserName)) {
                throw ConflictException(HttpStatus.CONFLICT, ErrorCode.E0004)
            }
        }

        // メールアドレスを変更しようとしている場合重複チェックをする
        if (newAppUserDto.email != authAppUserEntity.email) {
            if (appUserCheckDuplicate.isDuplicateEmail(newAppUserDto.email)) {
                throw ConflictException(HttpStatus.CONFLICT, ErrorCode.E0005)
            }
        }
    }
}
