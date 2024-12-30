package kkito.reagent_order.app_user

import com.fasterxml.jackson.databind.ObjectMapper
import kkito.reagent_order.TestSupport
import kkito.reagent_order.app_user.value.AppUserRequest
import kkito.reagent_order.error.ErrorCode
import kkito.reagent_order.test_data.TestDataAppUser
import org.assertj.db.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
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
) : TestSupport() {
    companion object {
        private val TABLE_NAMES = listOf("app_user")
    }

    @BeforeEach
    override fun setUp() {
        super.setUp()
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
        val resisterUser = createResponseBodyJson(testDataAppUser.createAppUser())
        val request = AppUserRequest(
            userName,
            email,
            password
        )

        val changes = createChanges(TABLE_NAMES).setStartPointNow()
        val resultActions = mockMvc.perform(
            put("/app_user/${resisterUser.getString("id")}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk)
        changes.setEndPointNow()
        // レスポンスのアサート
        val responseBody = createResponseBodyJson(resultActions)
        assertNotNull(responseBody.getString("id"))
        assertEquals(responseBody.getString("appUserName"), request.appUserName)
        assertEquals(responseBody.getString("email"), request.email)
        assertEquals(responseBody.getString("password"), request.password)
        assertEquals(responseBody.getString("deletedAt"), "null")

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
            .value("password").isEqualTo(request.password)
            .value("created_at").isNotNull()
            .value("deleted_at").isNull()
    }

    @Test
    fun 変更しようとしたユーザ名が重複した場合_E0004エラーになる() {
        testDataAppUser.createAppUser()
        val resisterUser = createResponseBodyJson(
            testDataAppUser.createAppUser(
                appUserName = "テスト 太郎2",
                email = "second_user@test.gmail.com"
            )
        )
        val request = AppUserRequest(
            "テスト 太郎",
            "second_user@test.gmail.com",
            "Test_pass_12345678"
        )

        val resultActions = mockMvc.perform(
            put("/app_user/${resisterUser.getString("id")}")
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
        testDataAppUser.createAppUser()
        val resisterUser = createResponseBodyJson(
            testDataAppUser.createAppUser(
                appUserName = "テスト 太郎2",
                email = "second_user@test.gmail.com"
            )
        )
        val request = AppUserRequest(
            "てすと 太郎",
            "test_email@test.gmail.com",
            "Test_pass_12345678"
        )

        val resultActions = mockMvc.perform(
            put("/app_user/${resisterUser.getString("id")}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isConflict)
        // レスポンスのアサート
        val responseBody = createResponseBodyJson(resultActions)
        assertEquals(ErrorCode.E0005.code, responseBody.getString("errorCode"))
        assertEquals(ErrorCode.E0005.message, responseBody.getString("message"))
    }

    @Test
    fun 更新するユーザーが見つからない場合_E0006エラーになる() {
        testDataAppUser.createAppUser()
        createResponseBodyJson(
            testDataAppUser.createAppUser(
                appUserName = "テスト 太郎2",
                email = "second_user@test.gmail.com"
            )
        )
        val request = AppUserRequest(
            "てすと 太郎",
            "second_user@test.gmail.com",
            "Test_pass_12345678"
        )

        val resultActions = mockMvc.perform(
            put("/app_user/${UUID.randomUUID()}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isNotFound)
        // レスポンスのアサート
        val responseBody = createResponseBodyJson(resultActions)
        assertEquals(ErrorCode.E0006.code, responseBody.getString("errorCode"))
        assertEquals(ErrorCode.E0006.message, responseBody.getString("message"))
    }
}
