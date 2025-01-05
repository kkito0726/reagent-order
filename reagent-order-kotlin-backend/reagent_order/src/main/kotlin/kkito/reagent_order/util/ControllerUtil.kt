package kkito.reagent_order.util

import kkito.reagent_order.app_user.entity.AppUserEntity
import org.springframework.security.core.context.SecurityContextHolder

open class ControllerUtil {
    fun user(): AppUserEntity {
        return SecurityContextHolder.getContext().authentication.principal as AppUserEntity
    }
}