package kkito.reagent_order.login

import com.fasterxml.jackson.databind.ObjectMapper
import kkito.reagent_order.TestSupport
import kkito.reagent_order.login.value.LoginRequest
import kkito.reagent_order.test_data.TestDataAppUser
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
class LoginTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
    @Autowired private val testDataAppUser: TestDataAppUser
) : TestSupport() {
    companion object {
        private val TABLE_NAMES = listOf("app_user")
    }

    @BeforeEach
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun ログインできる() {
        val resisterUser = createResponseBodyJson(testDataAppUser.createAppUser())
        val request = LoginRequest(
            resisterUser.getString("email"),
            "Test_pass_12345678"
        )

        val resultActions = mockMvc.perform(
            post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk)
        val responseBody = createResponseBodyJson(resultActions)
        assertNotNull(
            responseBody.getString("loginId")
        )
    }
}