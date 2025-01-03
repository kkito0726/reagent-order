package kkito.reagent_order.app_user.repository.impl

import com.generate.jooq.Tables.APP_USER
import kkito.reagent_order.app_user.entity.AppUserEntity
import kkito.reagent_order.app_user.repository.AppUserRepository
import kkito.reagent_order.app_user.value.AppUserDto
import kkito.reagent_order.app_user.value.AppUserId
import org.jooq.DSLContext
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
open class AppUserRepositoryImpl(
    private val dslContext: DSLContext,
    private val passwordEncoder: PasswordEncoder
) : AppUserRepository {
    override fun createAppUser(appUserEntity: AppUserEntity) {
        dslContext.insertInto(APP_USER).columns(
            APP_USER.ID,
            APP_USER.APP_USER_NAME,
            APP_USER.EMAIL,
            APP_USER.PASSWORD,
            APP_USER.CREATED_AT,
            APP_USER.DELETED_AT
        ).values(
            appUserEntity.id.appUserId.toString(),
            appUserEntity.appUserName.value,
            appUserEntity.email.value,
            passwordEncoder.encode(appUserEntity.password.value),
            appUserEntity.createdAt,
            appUserEntity.deletedAt
        ).execute()
    }

    override fun getAppUser(appUserId: AppUserId): AppUserEntity? {
        return dslContext.selectFrom(APP_USER).where(APP_USER.ID.eq(appUserId.toString()))
            .fetchOneInto(AppUserEntity::class.java)
    }

    override fun updateAppUser(newAppUserDto: AppUserDto): Int {
        return dslContext.update(APP_USER)
            .set(APP_USER.APP_USER_NAME, newAppUserDto.appUserName.value)
            .set(APP_USER.EMAIL, newAppUserDto.email.value)
            .set(APP_USER.PASSWORD, newAppUserDto.password.value)
            .where(APP_USER.ID.eq(newAppUserDto.id.toString())).execute()
    }

    override fun deleteAppUser(appUserId: AppUserId) {
        dslContext.update(APP_USER).set(APP_USER.DELETED_AT, LocalDateTime.now())
            .where(APP_USER.ID.eq(appUserId.toString())).execute()
    }
}