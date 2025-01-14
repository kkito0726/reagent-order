package kkito.reagent_order.test_data

import com.fasterxml.jackson.databind.ObjectMapper
import kkito.reagent_order.order.value.UserOrderRequest
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@Component
class TestDataOrder {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    fun createOrder(orderRequest: UserOrderRequest, jwtToken: String): ResultActions {
        return mockMvc.perform(
            MockMvcRequestBuilders.post("/order").contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $jwtToken")
                .content(objectMapper.writeValueAsString(orderRequest))
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }
}