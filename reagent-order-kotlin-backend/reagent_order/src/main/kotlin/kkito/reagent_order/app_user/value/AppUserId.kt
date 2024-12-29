package kkito.reagent_order.app_user.value

import java.util.UUID

data class AppUserId(val appUserId: UUID) {
    companion object {
        fun of(appUserId: UUID): AppUserId {
            return AppUserId(UUID.randomUUID())
        }
    }

    override fun toString(): String {
        return appUserId.toString()
    }
}