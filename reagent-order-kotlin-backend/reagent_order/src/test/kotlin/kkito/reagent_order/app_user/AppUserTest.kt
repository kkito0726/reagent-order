package kkito.reagent_order.app_user

import com.fasterxml.jackson.databind.ObjectMapper
import kkito.reagent_order.TestSupport
import kkito.reagent_order.app_user.value.AppUserRequest
import org.assertj.db.api.Assertions
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
@AutoConfigureMockMvc
class AppUserTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
) : TestSupport() {

    companion object {
        private val TABLE_NAMES = listOf("app_user")
    }

    @BeforeEach
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun ユーザー登録できる() {
        val request = AppUserRequest(
            "test user name",
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
        val responseBody = JSONObject(resultAction.andReturn().response.contentAsString)
        assertNotNull(responseBody.getString("id"))
        assertEquals(responseBody.getString("appUserName"), request.appUserName)
        assertEquals(responseBody.getString("email"), request.email)
        assertEquals(responseBody.getString("password"), request.password)
        assertEquals(
            responseBody.getString("createdAt").substring(0, 10), LocalDate.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd")
            )
        )
        assertEquals(responseBody.getString("deletedAt"), "null")

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
            .value("password").isEqualTo(request.password)
            .value("created_at").isNotNull()
            .value("deleted_at").isNull()
    }
}
