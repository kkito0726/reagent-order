package kkito.reagent_order.app_user.repository.impl

import com.generate.jooq.Tables.APP_USER
import kkito.reagent_order.app_user.value.AppUserName
import kkito.reagent_order.app_user.value.Email
import kkito.reagent_order.app_user.repository.AppUserCheckDuplicate
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
open class AppUserCheckDuplicateImpl(private val dslContext: DSLContext) : AppUserCheckDuplicate {
    override fun isDuplicateAppUserName(appUserName: AppUserName): Boolean {
        val count = dslContext
            .selectCount()
            .from(APP_USER)
            .where(APP_USER.APP_USER_NAME.eq(appUserName.value))
            .fetchOne(0, Int::class.java) ?: 0

        return count > 0
    }

    override fun isDuplicateEmail(email: Email): Boolean {
        val count = dslContext
            .selectCount()
            .from(APP_USER)
            .where(APP_USER.EMAIL.eq(email.value))
            .fetchOne(0, Int::class.java) ?: 0

        return count > 0
    }
}