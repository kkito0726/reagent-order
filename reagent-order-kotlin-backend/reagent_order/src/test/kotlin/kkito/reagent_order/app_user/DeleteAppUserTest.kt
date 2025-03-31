package kkito.reagent_order.app_user

import kkito.reagent_order.TestSupport
import kkito.reagent_order.error.ErrorCode
import kkito.reagent_order.test_data.TestDataAppUser
import org.assertj.db.api.Assertions
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID
import kotlin.test.assertEquals

@SpringBootTest
@AutoConfigureMockMvc
class DeleteAppUserTest(
    @Autowired private val testDataAppUser: TestDataAppUser,
) : TestSupport() {
    companion object {
        private val TABLE_NAMES = listOf("app_user")
    }

    private lateinit var appUser: JSONObject
    private lateinit var jwtToken: String

    @BeforeEach
    override fun setUp() {
        super.setUp()
        val createdUserResponse = createResponseBodyJson(testDataAppUser.createAppUser())
        appUser = createdUserResponse.getJSONObject("appUserEntity")
        jwtToken = createdUserResponse.getString("token")
    }

    @Test
    fun ユーザーを退会処理できる() {
        val changes = createChanges(TABLE_NAMES).setStartPointNow()
        val resultActions = mockMvc.perform(
            delete("/app_user/${appUser.getString("id")}").header(
                    "Authorization",
                    "Bearer $jwtToken"
                )
        ).andExpect(status().isOk())
        changes.setEndPointNow()

        // からのレスポンスボディが返ること
        val responseBody = createResponseBodyJson(resultActions)
        assertEquals("{}", responseBody.toString())

        // DBアサーション
        Assertions.assertThat(changes).ofModificationOnTable("app_user").hasNumberOfChanges(1)
            .ofDeletionOnTable("app_user").hasNumberOfChanges(0).ofCreationOnTable("app_user")
            .hasNumberOfChanges(0).changeOfModification().rowAtEndPoint().value("id")
            .isEqualTo(appUser.getString("id")).value("app_user_name")
            .isEqualTo(appUser.getString("appUserName")).value("email")
            .isEqualTo(appUser.getString("email"))
//            .value("password").isEqualTo(createdUserResponse.getString("password"))
            .value("created_at").isNotNull().value("deleted_at").isNotNull()
    }

    @Test
    fun ログインユーザーのIDとパスパラメータのIDが異なる場合_E0009認証エラーになる() {
        val resultActions = mockMvc.perform(
            delete("/app_user/${UUID.randomUUID()}").header("Authorization", "Bearer $jwtToken")
        ).andExpect(status().isForbidden())
        val responseBody = createResponseBodyJson(resultActions)

        assertEquals(ErrorCode.E0009.code, responseBody.getString("errorCode"))
        assertEquals(ErrorCode.E0009.message, responseBody.getString("message"))
    }

    @Test
    fun 削除対象のユーザが削除済の場合() {
        testDataAppUser.deleteAppUser(appUser.getString("id"), jwtToken)

        val resultActions = mockMvc.perform(
            delete("/app_user/${appUser.getString("id")}").header(
                    "Authorization",
                    "Bearer $jwtToken"
                )
        ).andExpect(status().isNotFound)

        val responseBody = createResponseBodyJson(resultActions)
        assertEquals(ErrorCode.E0006.code, responseBody.getString("errorCode"))
        assertEquals(ErrorCode.E0006.message, responseBody.getString("message"))
    }
}