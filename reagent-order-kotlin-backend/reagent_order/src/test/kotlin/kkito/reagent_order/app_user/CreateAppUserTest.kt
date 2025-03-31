package kkito.reagent_order.app_user

import com.generate.jooq.Tables.APP_USER
import kkito.reagent_order.TestSupport
import kkito.reagent_order.app_user.value.CreateAppUserRequest
import kkito.reagent_order.app_user.value.Role
import kkito.reagent_order.error.ErrorCode
import org.assertj.db.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
@AutoConfigureMockMvc
class CreateAppUserTest : TestSupport() {

    companion object {
        private val TABLE_NAMES = listOf("app_user")
    }

    @BeforeEach
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun ユーザー登録できる() {
        val request = CreateAppUserRequest(
            "テスト 太郎",
            "test_email@test.gmail.com",
            "Test_pass_12345678"
        )
        val changes = createChanges(TABLE_NAMES).setStartPointNow()
        val resultAction = mockMvc.perform(
            post("/app_user/create").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk)
        changes.setEndPointNow()

        // レスポンスのアサート
        val responseBody = createResponseBodyJson(resultAction)
        val appUserEntity = responseBody.getJSONObject("appUserEntity")
        assertNotNull(appUserEntity.getString("id"))
        assertEquals(appUserEntity.getString("appUserName"), request.appUserName)
        assertEquals(appUserEntity.getString("email"), request.email)
        val password = dslContext.select(APP_USER.PASSWORD).from(APP_USER)
            .where(APP_USER.ID.eq(appUserEntity.getString("id"))).fetchOne()?.get("password")
            .toString()
        assertEquals(
            appUserEntity.getString("createdAt").substring(0, 10), LocalDate.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd")
            )
        )
        assertEquals(appUserEntity.getString("role"), Role.USER.value)
        assertNotNull(responseBody.getString("token"))

        // DBアサート
        Assertions.assertThat(changes)
            .ofModificationOnTable("app_user")
            .hasNumberOfChanges(0)
            .ofDeletionOnTable("app_user")
            .hasNumberOfChanges(0)
            .ofCreationOnTable("app_user")
            .hasNumberOfChanges(1)
            .change()
            .isCreation()
            .rowAtEndPoint()
            .value("id").isNotNull()
            .value("app_user_name").isEqualTo(request.appUserName)
            .value("email").isEqualTo(request.email)
            .value("password").isEqualTo(password)
            .value("created_at").isNotNull()
            .value("deleted_at").isNull()
            .value("role").isEqualTo(Role.USER.value)
    }

    @ParameterizedTest
    @CsvSource(
        "ユーザー名が3文字未満の場合_E0001エラーになる, 2",
        "ユーザー名が16文字異常の場合_E0001エラーになる, 16",
    )
    fun ユーザー名の文字数が不正な場合_E0001エラーになる(display: String, nameLength: Int) {
        val request = CreateAppUserRequest(
            "a".repeat(nameLength),
            "test_email",
            "1234"
        )
        val resultAction = mockMvc.perform(
            post("/app_user/create").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest)
        val responseBody = createResponseBodyJson(resultAction)
        assertEquals(ErrorCode.E0001.code, responseBody.getString("errorCode"))
        assertEquals(ErrorCode.E0001.message, responseBody.getString("message"))
    }

    @Test
    fun メールアドレスの形式担っていない場合の場合_E0002エラーになる() {
        val request = CreateAppUserRequest(
            "テスト 太郎",
            "test_email",
            "test_pass_12345678"
        )
        val resultAction = mockMvc.perform(
            post("/app_user/create").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest)
        val responseBody = createResponseBodyJson(resultAction)
        assertEquals(ErrorCode.E0002.code, responseBody.getString("errorCode"))
        assertEquals(ErrorCode.E0002.message, responseBody.getString("message"))
    }

    @Test
    fun パスワードの文字数が5文字未満の場合_E0003エラーになる() {
        val request = CreateAppUserRequest(
            "テスト 太郎",
            "test_email@test.gmail.com",
            "1234"
        )
        val resultAction = mockMvc.perform(
            post("/app_user/create").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest)
        val responseBody = createResponseBodyJson(resultAction)
        assertEquals(ErrorCode.E0003.code, responseBody.getString("errorCode"))
        assertEquals(ErrorCode.E0003.message, responseBody.getString("message"))
    }

    @Test
    fun ユーザー名が重複している場合_E_0004エラーになる() {
        val request1 = CreateAppUserRequest(
            "test user name",
            "test_email@test.gmail.com",
            "Test_pass_12345678"
        )

        val request2 = CreateAppUserRequest(
            "test user name",
            "test_email_2@test.gmail.com",
            "Test_pass_12345678"
        )

        // 1回目のリクエストは成功する
        mockMvc.perform(
            post("/app_user/create").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1))
        ).andExpect(status().isOk)

        // 同じユーザーだと409
        val resultAction = mockMvc.perform(
            post("/app_user/create").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2))
        ).andExpect(status().isConflict)
        val responseBody = createResponseBodyJson(resultAction)
        assertEquals(responseBody.getString("errorCode"), ErrorCode.E0004.code)
        assertEquals(responseBody.getString("message"), ErrorCode.E0004.message)
    }

    @Test
    fun メールアドレスが重複している場合_E0005エラーになる() {
        val request1 = CreateAppUserRequest(
            "test user name",
            "test_email@test.gmail.com",
            "Test_pass_12345678"
        )

        val request2 = CreateAppUserRequest(
            "テスト 太郎",
            "test_email@test.gmail.com",
            "Test_pass_12345678"
        )

        // 1回目のリクエストは成功する
        mockMvc.perform(
            post("/app_user/create").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1))
        ).andExpect(status().isOk)

        // 同じメールアドレスだと409
        val resultAction = mockMvc.perform(
            post("/app_user/create").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2))
        ).andExpect(status().isConflict)
        val responseBody = createResponseBodyJson(resultAction)
        assertEquals(ErrorCode.E0005.code, responseBody.getString("errorCode"))
        assertEquals(ErrorCode.E0005.message, responseBody.getString("message"))
    }
}
