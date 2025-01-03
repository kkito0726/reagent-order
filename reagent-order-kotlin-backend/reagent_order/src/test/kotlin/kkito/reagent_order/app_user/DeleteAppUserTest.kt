package kkito.reagent_order.app_user

import com.fasterxml.jackson.databind.ObjectMapper
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
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID
import kotlin.test.assertEquals

@SpringBootTest
@AutoConfigureMockMvc
class DeleteAppUserTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
    @Autowired private val testDataAppUser: TestDataAppUser,
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

    @Test
    fun ユーザーを削除できる() {
        val changes = createChanges(TABLE_NAMES).setStartPointNow()
        val resultActions = mockMvc.perform(
            delete("/app_user/${createdUserResponse.getString("id")}")
                .header("Authorization", "Bearer $jwtToken")
        ).andExpect(status().isOk())
        changes.setEndPointNow()

        // からのレスポンスボディが返ること
        val responseBody = createResponseBodyJson(resultActions)
        assertEquals("{}", responseBody.toString())

        // DBアサーション
        Assertions.assertThat(changes)
            .ofModificationOnTable("app_user")
            .hasNumberOfChanges(1)
            .ofDeletionOnTable("app_user")
            .hasNumberOfChanges(0)
            .ofCreationOnTable("app_user")
            .hasNumberOfChanges(0)
            .changeOfModification()
            .rowAtEndPoint()
            .value("id").isEqualTo(createdUserResponse.getString("id"))
            .value("app_user_name").isEqualTo(createdUserResponse.getString("appUserName"))
            .value("email").isEqualTo(createdUserResponse.getString("email"))
//            .value("password").isEqualTo(createdUserResponse.getString("password"))
            .value("created_at").isNotNull()
            .value("deleted_at").isNotNull()
    }

    @Test
    fun 削除対象のユーザーが見つからない場合_E0006エラーになる() {
        val resultActions = mockMvc.perform(
            delete("/app_user/${UUID.randomUUID()}")
                .header("Authorization", "Bearer $jwtToken")
        ).andExpect(status().isNotFound())
        val responseBody = createResponseBodyJson(resultActions)

        assertEquals(ErrorCode.E0006.code, responseBody.getString("errorCode"))
        assertEquals(ErrorCode.E0006.message, responseBody.getString("message"))
    }
}