package kkito.reagent_order.lib.fixture

import com.generate.jooq.Tables
import org.jooq.DSLContext
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime
import java.util.UUID

class AppUserFixture(
    passwordEncoder: PasswordEncoder, // 依存性注入
    private val id: UUID = UUID.randomUUID(),
    private val appUserName: String = "テストユーザー",
    val email: String = "test@fixture.com",
    val password: String = "Fixture12345678",
    private val createdAt: LocalDateTime = LocalDateTime.now(),
    private val deletedAt: LocalDateTime? = null,
    private val role: String = "SYSTEM",
) {
    private val encodedPassword: String = passwordEncoder.encode(password) // エンコード済みパスワード

    fun insert(dslContext: DSLContext): Int {
        return dslContext.insertInto(Tables.APP_USER)
            .columns(
                Tables.APP_USER.ID,
                Tables.APP_USER.APP_USER_NAME,
                Tables.APP_USER.EMAIL,
                Tables.APP_USER.PASSWORD,
                Tables.APP_USER.CREATED_AT,
                Tables.APP_USER.DELETED_AT,
                Tables.APP_USER.ROLE
            ).values(
                id.toString(),
                appUserName,
                email,
                encodedPassword,
                createdAt,
                deletedAt,
                role
            ).execute()
    }
}

// 拡張関数を追加
fun DSLContext.insertAppUser(fixture: AppUserFixture): Int {
    return fixture.insert(this)
}