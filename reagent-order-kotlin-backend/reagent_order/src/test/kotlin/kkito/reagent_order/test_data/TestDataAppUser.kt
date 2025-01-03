package kkito.reagent_order.test_data

import com.fasterxml.jackson.databind.ObjectMapper
import kkito.reagent_order.TestSupport
import kkito.reagent_order.app_user.value.AppUserRequest
import kkito.reagent_order.login.value.LoginRequest
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@Component
class TestDataAppUser {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    fun createAppUser(
        appUserName: String = "テスト 太郎",
        email: String = "test_email@test.gmail.com",
        password: String = "Test_pass_12345678",
    ): ResultActions {
        val request = AppUserRequest(
            appUserName,
            email,
            password
        )
        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/app_user/create").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().isOk)
        return resultActions
    }

    fun login(
        email: String = "test_email@test.gmail.com",
        password: String = "Test_pass_12345678",
    ): String {
        val request = LoginRequest(
            email,
            password
        )
        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().isOk)
        val responseBody = createResponseBodyJson(resultActions)
        return responseBody.getString("loginId")
    }

    private fun createResponseBodyJson(resultAction: ResultActions): JSONObject {
        return JSONObject(resultAction.andReturn().response.contentAsByteArray.toString(Charsets.UTF_8))
    }
}