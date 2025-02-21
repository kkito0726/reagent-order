package kkito.reagent_order.app_user

import com.fasterxml.jackson.databind.ObjectMapper
import kkito.reagent_order.TestSupport
import kkito.reagent_order.app_user.value.UpdateAppUserRequest
import kkito.reagent_order.app_user.value.Role
import kkito.reagent_order.error.ErrorCode
import kkito.reagent_order.lib.fixture.AppUserFixture
import kkito.reagent_order.test_data.TestDataAppUser
import org.assertj.db.api.Assertions
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
@AutoConfigureMockMvc
class UpdateAppUserTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
    @Autowired private val testDataAppUser: TestDataAppUser,
    @Autowired private val passwordEncoder: PasswordEncoder,
) : TestSupport() {
    companion object {
        private val TABLE_NAMES = listOf("app_user")
    }

    private lateinit var createdUserResponse: JSONObject
    private lateinit var jwtToken: String

    @BeforeEach
    override fun setUp() {
        super.setUp()
        createdUserResponse = createResponseBodyJson(testDataAppUser.createAppUser())
        jwtToken = testDataAppUser.login()
    }

    @ParameterizedTest
    @CsvSource(
        "ユーザー名が変更できる, updatedテスト 太郎, test_email@test.gmail.com, Test_pass_12345678",
        "Emailが変更できる, テスト 太郎, updated_test_email@test.gmail.com, Test_pass_12345678",
        "パスワードが変更できる, テスト 太郎, test_email@test.gmail.com, Updated_test_pass_12345678",
        "3項目すべて変更できる, updatedテスト 太郎, updated_test_email@test.gmail.com, Updated_test_pass_12345678"
    )
    fun ユーザー情報を更新できる(
        display: String,
        userName: String,
        email: String,
        password: String
    ) {
        val request = UpdateAppUserRequest(
            userName,
            email,
            password,
            null
        )

        val changes = createChanges(TABLE_NAMES).setStartPointNow()
        val resultActions = mockMvc.perform(
            put("/app_user/${createdUserResponse.getString("id")}")
                .header("Authorization", "Bearer $jwtToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk)
        changes.setEndPointNow()
        // レスポンスのアサート
        val responseBody = createResponseBodyJson(resultActions)
        assertNotNull(responseBody.getString("id"))
        assertEquals(responseBody.getString("appUserName"), request.appUserName)
        assertEquals(responseBody.getString("email"), request.email)
        assertEquals(responseBody.getString("role"), Role.USER.value)

        Assertions.assertThat(changes)
            .ofModificationOnTable("app_user")
            .hasNumberOfChanges(1)
            .ofDeletionOnTable("app_user")
            .hasNumberOfChanges(0)
            .ofCreationOnTable("app_user")
            .hasNumberOfChanges(0)
            .changeOfModification()
            .rowAtEndPoint()
            .value("id").isNotNull()
            .value("app_user_name").isEqualTo(request.appUserName)
            .value("email").isEqualTo(request.email)
            .value("password").isNotNull
            .value("created_at").isNotNull()
            .value("deleted_at").isNull()
            .value("role").isEqualTo("USER")
    }

    @Test
    fun システム管理者が一般ユーザーの権限を管理者に変更できる() {
        val systemUser = AppUserFixture(passwordEncoder = passwordEncoder)
        systemUser.insert(dslContext)
        val systemUserJwt = testDataAppUser.login(systemUser.email, systemUser.password)

        val request = UpdateAppUserRequest(
            createdUserResponse.getString("appUserName"),
            createdUserResponse.getString("email"),
            "Test_pass_12345678",
            Role.ADMIN.value
        )

        val changes = createChanges(TABLE_NAMES).setStartPointNow()
        val resultActions = mockMvc.perform(
            put("/app_user/${createdUserResponse.getString("id")}")
                .header("Authorization", "Bearer $systemUserJwt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk)
        changes.setEndPointNow()

        // レスポンスのアサート
        val responseBody = createResponseBodyJson(resultActions)
        assertNotNull(responseBody.getString("id"))
        assertEquals(responseBody.getString("appUserName"), request.appUserName)
        assertEquals(responseBody.getString("email"), request.email)
        assertEquals(responseBody.getString("role"), Role.ADMIN.value)

        Assertions.assertThat(changes)
            .ofModificationOnTable("app_user")
            .hasNumberOfChanges(1)
            .ofDeletionOnTable("app_user")
            .hasNumberOfChanges(0)
            .ofCreationOnTable("app_user")
            .hasNumberOfChanges(0)
            .changeOfModification()
            .rowAtEndPoint()
            .value("id").isNotNull()
            .value("app_user_name").isEqualTo(request.appUserName)
            .value("email").isEqualTo(request.email)
            .value("password").isNotNull
            .value("created_at").isNotNull()
            .value("deleted_at").isNull()
            .value("role").isEqualTo("ADMIN")
    }

    @Test
    fun パスワード変更後_新しいパスワードでログインできる() {
        val newPassword = "New_password_87654321"
        val request = UpdateAppUserRequest(
            "テスト 太郎",
            "test_email@test.gmail.com",
            newPassword,
            null
        )

        val changes = createChanges(TABLE_NAMES).setStartPointNow()
        val resultActions = mockMvc.perform(
            put("/app_user/${createdUserResponse.getString("id")}")
                .header("Authorization", "Bearer $jwtToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk)
        changes.setEndPointNow()
        // レスポンスのアサート
        val responseBody = createResponseBodyJson(resultActions)
        assertNotNull(responseBody.getString("id"))
        assertEquals(responseBody.getString("appUserName"), request.appUserName)
        assertEquals(responseBody.getString("email"), request.email)

        val newJwt = testDataAppUser.login(password = newPassword)
        assertNotNull(newJwt)
    }

    @Test
    fun 変更しようとしたユーザ名が重複した場合_E0004エラーになる() {
        createResponseBodyJson(
            testDataAppUser.createAppUser(
                appUserName = "テスト 太郎2",
                email = "second_user@test.gmail.com"
            )
        )
        val request = UpdateAppUserRequest(
            "テスト 太郎2",
            "test_email@test.gmail.com",
            "Test_pass_12345678",
            null
        )

        val resultActions = mockMvc.perform(
            put("/app_user/${createdUserResponse.getString("id")}")
                .header("Authorization", "Bearer $jwtToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isConflict)
        // レスポンスのアサート
        val responseBody = createResponseBodyJson(resultActions)
        assertEquals(ErrorCode.E0004.code, responseBody.getString("errorCode"))
        assertEquals(ErrorCode.E0004.message, responseBody.getString("message"))
    }

    @Test
    fun 変更しようとしたメールアドレスが重複する場合_E0005エラーになる() {
        createResponseBodyJson(
            testDataAppUser.createAppUser(
                appUserName = "テスト 太郎2",
                email = "second_user@test.gmail.com"
            )
        )
        val request = UpdateAppUserRequest(
            "てすと 太郎",
            "second_user@test.gmail.com",
            "Test_pass_12345678",
            null
        )

        val resultActions = mockMvc.perform(
            put("/app_user/${createdUserResponse.getString("id")}")
                .header("Authorization", "Bearer $jwtToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isConflict)
        // レスポンスのアサート
        val responseBody = createResponseBodyJson(resultActions)
        assertEquals(ErrorCode.E0005.code, responseBody.getString("errorCode"))
        assertEquals(ErrorCode.E0005.message, responseBody.getString("message"))
    }

    @Test
    fun ログインユーザーとパスパラメータのIDが異なる場合_E0009認証エラーになる() {
        createResponseBodyJson(
            testDataAppUser.createAppUser(
                appUserName = "テスト 太郎2",
                email = "second_user@test.gmail.com"
            )
        )
        val request = UpdateAppUserRequest(
            "てすと 太郎",
            "second_user@test.gmail.com",
            "Test_pass_12345678",
            null
        )

        val resultActions = mockMvc.perform(
            put("/app_user/${UUID.randomUUID()}")
                .header("Authorization", "Bearer $jwtToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isForbidden)
        // レスポンスのアサート
        val responseBody = createResponseBodyJson(resultActions)
        assertEquals(ErrorCode.E0009.code, responseBody.getString("errorCode"))
        assertEquals(ErrorCode.E0009.message, responseBody.getString("message"))
    }

    @Test
    fun 退会済みのユーザーを更新しようとする場合_E0006エラーになる() {
        testDataAppUser.deleteAppUser(createdUserResponse.getString("id"), jwtToken)

        val request = UpdateAppUserRequest(
            "てすと 太郎",
            "second_user@test.gmail.com",
            "Test_pass_12345678",
            null
        )

        val resultActions = mockMvc.perform(
            put("/app_user/${createdUserResponse.getString("id")}")
                .header("Authorization", "Bearer $jwtToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isNotFound)
        // レスポンスのアサート
        val responseBody = createResponseBodyJson(resultActions)
        assertEquals(ErrorCode.E0006.code, responseBody.getString("errorCode"))
        assertEquals(ErrorCode.E0006.message, responseBody.getString("message"))
    }


    @Test
    fun JWTの形式が不正な場合() {
        val request = UpdateAppUserRequest(
            "てすと 太郎",
            "second_user@test.gmail.com",
            "Test_pass_12345678",
            null
        )

        val resultActions = mockMvc.perform(
            put("/app_user/${createdUserResponse.getString("id")}")
                .header("Authorization", "Bearer Invalid token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest)
        val responseBody = createResponseBodyJson(resultActions)

        assertEquals(ErrorCode.E0010.code, responseBody.getString("errorCode"))
        assertEquals(ErrorCode.E0010.message, responseBody.getString("message"))
    }

    @Test
    fun 一般ユーザーが権限を変更しようとする時_E0009エラーになる() {
        val request = UpdateAppUserRequest(
            "てすと 太郎",
            "second_user@test.gmail.com",
            "Test_pass_12345678",
            "ADMIN"
        )

        val resultActions = mockMvc.perform(
            put("/app_user/${createdUserResponse.getString("id")}")
                .header("Authorization", "Bearer $jwtToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isForbidden)
        // レスポンスのアサート
        val responseBody = createResponseBodyJson(resultActions)
        assertEquals(ErrorCode.E0009.code, responseBody.getString("errorCode"))
        assertEquals(ErrorCode.E0009.message, responseBody.getString("message"))
    }
}
