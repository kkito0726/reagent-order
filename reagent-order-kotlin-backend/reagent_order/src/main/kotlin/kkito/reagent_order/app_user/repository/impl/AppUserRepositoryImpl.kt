package kkito.reagent_order.app_user.repository.impl

import com.generate.jooq.Tables.APP_USER
import com.generate.jooq.tables.AppUser
import kkito.reagent_order.app_user.model.AppUserEntity
import kkito.reagent_order.app_user.repository.AppUserRepository
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
open class AppUserRepositoryImpl(private val dslContext: DSLContext) : AppUserRepository {
    override fun createAppUser(appUserEntity: AppUserEntity) {
        dslContext.insertInto(APP_USER).columns(
            APP_USER.ID,
            APP_USER.APP_USER_NAME,
            APP_USER.EMAIL,
            APP_USER.CREATEDAT,
            APP_USER.DELETEDAT
        ).values(
            appUserEntity.id.appUserId.toString(),
            appUserEntity.appUserName.value,
            appUserEntity.email.value,
            appUserEntity.createdAt,
            appUserEntity.deletedAt
        ).execute()
    }
}