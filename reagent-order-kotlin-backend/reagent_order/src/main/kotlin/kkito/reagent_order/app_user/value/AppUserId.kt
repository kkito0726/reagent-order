package kkito.reagent_order.app_user.value

import java.util.UUID

data class AppUserId(val appUserId: UUID) {
    override fun toString(): String {
        return appUserId.toString()
    }
}