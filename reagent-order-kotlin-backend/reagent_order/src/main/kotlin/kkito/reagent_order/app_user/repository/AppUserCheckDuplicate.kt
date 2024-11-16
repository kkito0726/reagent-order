package kkito.reagent_order.app_user.repository

import kkito.reagent_order.app_user.model.AppUserName
import kkito.reagent_order.app_user.model.Email

interface AppUserCheckDuplicate {
    fun isDuplicateAppUserName(appUserName: AppUserName): Boolean
    fun isDuplicateEmail(email: Email): Boolean
}