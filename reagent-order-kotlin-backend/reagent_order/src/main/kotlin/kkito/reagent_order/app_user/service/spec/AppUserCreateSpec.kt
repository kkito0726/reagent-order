package kkito.reagent_order.app_user.service.spec

import kkito.reagent_order.app_user.entity.AppUserEntity
import kkito.reagent_order.app_user.repository.AppUserCheckDuplicate
import org.springframework.stereotype.Service

@Service
class AppUserCreateSpec(private val appUserCheckDuplicate: AppUserCheckDuplicate) {
    fun check(appUserEntity: AppUserEntity) {
        if (appUserCheckDuplicate.isDuplicateAppUserName(appUserEntity.appUserName)) {
            // TODO: throw ConflictException
            println("throw ConflictException")
        }

        if (appUserCheckDuplicate.isDuplicateEmail(appUserEntity.email)) {
            // TODO: throw ConflictException
            println("throw ConflictException")
        }
    }
}