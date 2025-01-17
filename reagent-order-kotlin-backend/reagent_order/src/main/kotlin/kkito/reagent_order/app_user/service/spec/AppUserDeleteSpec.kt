package kkito.reagent_order.app_user.service.spec

import kkito.reagent_order.app_user.entity.AppUserEntity
import kkito.reagent_order.app_user.value.AppUserId
import kkito.reagent_order.error.ErrorCode
import kkito.reagent_order.error.ForbiddenException
import org.springframework.stereotype.Service

@Service
class AppUserDeleteSpec {
    fun check(appUserId: AppUserId, authAppUserEntity: AppUserEntity) {
        // パスパラメータとログインユーザのIDが異なる場合認証エラー
        if (appUserId != authAppUserEntity.id) {
            throw ForbiddenException(ErrorCode.E0009)
        }
    }
}