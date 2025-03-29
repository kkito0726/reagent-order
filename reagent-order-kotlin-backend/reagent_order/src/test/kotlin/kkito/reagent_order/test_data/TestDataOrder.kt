package kkito.reagent_order.test_data

import com.fasterxml.jackson.databind.ObjectMapper
import kkito.reagent_order.order.value.OrderDetailRequest
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
class TestDataOrder(
    val userOrderRequest: UserOrderRequest = UserOrderRequest(
            "テスト 試薬発注申請A",
            listOf(
                OrderDetailRequest(
                    "DMEM",
                    "https://labchem-wako.fujifilm.com/jp/product/detail/W01W0104-2978.html",
                    3
                ),
                OrderDetailRequest(
                    "P/S",
                    "https://labchem-wako.fujifilm.com/jp/product/detail/W01W0116-2319.html",
                    2
                )
            )
        )
) {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    fun createOrder(
        orderRequest: UserOrderRequest = userOrderRequest,
        jwtToken: String
    ): ResultActions {
        return mockMvc.perform(
            MockMvcRequestBuilders.post("/order").contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $jwtToken")
                .content(objectMapper.writeValueAsString(orderRequest))
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }
}