package kkito.reagent_order.app_user.service.spec

import kkito.reagent_order.app_user.entity.AppUserEntity
import kkito.reagent_order.app_user.repository.AppUserCheckDuplicate
import kkito.reagent_order.error.ConflictException
import kkito.reagent_order.error.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class AppUserCreateSpec(private val appUserCheckDuplicate: AppUserCheckDuplicate) {
    fun check(appUserEntity: AppUserEntity) {
        if (appUserCheckDuplicate.isDuplicateAppUserName(appUserEntity.appUserName)) {
            throw ConflictException(HttpStatus.CONFLICT, ErrorCode.E0004)
        }

        if (appUserCheckDuplicate.isDuplicateEmail(appUserEntity.email)) {
            throw ConflictException(HttpStatus.CONFLICT, ErrorCode.E0005)
        }
    }
}