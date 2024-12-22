package kkito.reagent_order.app_user.repository

import kkito.reagent_order.app_user.value.AppUserName
import kkito.reagent_order.app_user.value.Email

interface AppUserCheckDuplicate {
    fun isDuplicateAppUserName(appUserName: AppUserName): Boolean
    fun isDuplicateEmail(email: Email): Boolean
}