package kkito.reagent_order.login.repository.impl

import com.generate.jooq.Tables.APP_USER
import kkito.reagent_order.app_user.entity.AppUserEntity
import kkito.reagent_order.app_user.value.AppUserId
import kkito.reagent_order.app_user.value.Email
import kkito.reagent_order.app_user.value.Password
import kkito.reagent_order.error.ErrorCode
import kkito.reagent_order.error.NotFoundException
import kkito.reagent_order.login.repository.LoginRepository
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
open class LoginRepositoryImpl(
    private val dslContext: DSLContext
) : LoginRepository {
    override fun getAppUser(email: Email, password: Password): AppUserEntity {
        return dslContext.selectFrom(APP_USER)
            .where(APP_USER.EMAIL.eq(email.value))
            .and(APP_USER.PASSWORD.eq(password.value))
            .fetchOneInto(AppUserEntity::class.java)
            ?: throw NotFoundException(ErrorCode.E0006)
    }

    override fun getAppUserByEmail(email: Email): AppUserEntity {
        return dslContext.selectFrom(APP_USER)
            .where(APP_USER.EMAIL.eq(email.value))
            .and(APP_USER.DELETED_AT.isNull)
            .fetchOneInto(AppUserEntity::class.java)
            ?: throw NotFoundException(ErrorCode.E0006)
    }

    override fun getAppUserByAppUserId(appUserId: AppUserId): AppUserEntity {
        return dslContext.selectFrom(APP_USER)
            .where(APP_USER.ID.eq(appUserId.appUserId.toString()))
            .and(APP_USER.DELETED_AT.isNull)
            .fetchOneInto(AppUserEntity::class.java)
            ?: throw NotFoundException(ErrorCode.E0006)
    }
}