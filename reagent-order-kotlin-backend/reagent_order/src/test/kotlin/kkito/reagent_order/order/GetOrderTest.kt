package kkito.reagent_order.order

import com.fasterxml.jackson.databind.ObjectMapper
import kkito.reagent_order.TestSupport
import kkito.reagent_order.order.value.OrderDetailRequest
import kkito.reagent_order.order.value.OrderStatus
import kkito.reagent_order.order.value.UserOrderRequest
import kkito.reagent_order.test_data.TestDataAppUser
import kkito.reagent_order.test_data.TestDataOrder
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.stream.IntStream
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetOrderTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
    @Autowired private val testDataAppUser: TestDataAppUser,
    @Autowired private val testDataOrder: TestDataOrder,
) : TestSupport() {
    private lateinit var createdUserResponse: JSONObject
    private lateinit var jwtToken: String

    private lateinit var otherCreateUserResponse: JSONObject
    private lateinit var otherJwtToken: String

    @BeforeEach
    override fun setUp() {
        super.setUp()
        createdUserResponse = createResponseBodyJson(testDataAppUser.createAppUser())
        jwtToken = testDataAppUser.login()

        otherCreateUserResponse = createResponseBodyJson(
            testDataAppUser.createAppUser(
                appUserName = "サブユーザー",
                email = "sum_mail@test.gmail.com",
                password = "SubPassword123"
            )
        )
        otherJwtToken = testDataAppUser.login(
            email = "sum_mail@test.gmail.com",
            password = "SubPassword123"
        )
    }

    @Test
    fun 全申請情報を取得できる() {
        val requestOrders = listOf(
            UserOrderRequest(
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
            ),
            UserOrderRequest(
                "テスト 試薬発注申請B",
                listOf(
                    OrderDetailRequest(
                        "コラゲナーゼ",
                        "https://www.sigmaaldrich.com/JP/ja/product/sigma/c5138",
                        1
                    ),
                    OrderDetailRequest(
                        "DMEM",
                        "https://labchem-wako.fujifilm.com/jp/product/detail/W01W0104-2978.html",
                        2
                    ),
                    OrderDetailRequest(
                        "P/S",
                        "https://labchem-wako.fujifilm.com/jp/product/detail/W01W0116-2319.html",
                        3
                    )
                )
            )
        )
        testDataOrder.createOrder(requestOrders[0], jwtToken)
        testDataOrder.createOrder(requestOrders[1], otherJwtToken)
        val userNames = listOf(
            createdUserResponse.getString("appUserName"),
            otherCreateUserResponse.getString("appUserName")
        )
        val resultActions = mockMvc.perform(
            get("/order").contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $jwtToken")
        ).andExpect(status().isOk)
        val responseArray = createResponseJsonArray(resultActions)

        IntStream.range(0, responseArray.length()).forEach {
            val order = responseArray.getJSONObject(it)
            assertNotNull(order.get("id"))
            assertEquals(userNames[it], order.getString("appUserName"))
            assertEquals(requestOrders[it].title, order.getString("title"))
            assertNotNull(order.getString("createdAt"))

            // 申請詳細のアサート
            val orderDetails = order.getJSONArray("orderDetails")
            IntStream.range(0, orderDetails.length()).forEach { detailIndex ->
                val orderDetail = orderDetails.getJSONObject(detailIndex)
                assertNotNull(orderDetail.getString("orderDetailId"))
                assertEquals(
                    requestOrders[it].orderDetails[detailIndex].reagentName,
                    orderDetail.getString("reagentName")
                )
                assertEquals(
                    requestOrders[it].orderDetails[detailIndex].url,
                    orderDetail.getString("url")
                )
                assertEquals(
                    requestOrders[it].orderDetails[detailIndex].count.toString(),
                    orderDetail.getString("count")
                )
                assertEquals(OrderStatus.PENDING.value, orderDetail.getString("status"))
                assertNotNull(orderDetail.get("createdAt"))
                assertEquals("null", orderDetail.getString("updatedAt"))
            }
        }
    }
}