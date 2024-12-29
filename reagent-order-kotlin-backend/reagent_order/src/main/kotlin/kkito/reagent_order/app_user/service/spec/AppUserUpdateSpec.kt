package kkito.reagent_order.app_user.service.spec

import kkito.reagent_order.app_user.entity.AppUserEntity
import kkito.reagent_order.app_user.repository.AppUserCheckDuplicate
import kkito.reagent_order.app_user.value.AppUserDto
import kkito.reagent_order.error.ConflictException
import kkito.reagent_order.error.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class AppUserUpdateSpec(private val appUserCheckDuplicate: AppUserCheckDuplicate) {
        fun check(oldAppUserEntity: AppUserEntity, newAppUserDto: AppUserDto) {
            // ユーザーネームを変更しようとしている場合重複チェックをする
            if (newAppUserDto.appUserName != oldAppUserEntity.appUserName) {
                if (appUserCheckDuplicate.isDuplicateAppUserName(newAppUserDto.appUserName)) {
                    throw ConflictException(HttpStatus.CONFLICT, ErrorCode.E0004)
                }
            }

            // メールアドレスを変更しようとしている場合重複チェックをする
            if (newAppUserDto.email != oldAppUserEntity.email) {
                if (appUserCheckDuplicate.isDuplicateEmail(newAppUserDto.email)) {
                    throw ConflictException(HttpStatus.CONFLICT, ErrorCode.E0005)
                }
            }
        }


}