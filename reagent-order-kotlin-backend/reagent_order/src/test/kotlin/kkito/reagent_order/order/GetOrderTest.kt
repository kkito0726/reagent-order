package kkito.reagent_order.order

import kkito.reagent_order.TestSupport
import kkito.reagent_order.error.ErrorCode
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.stream.IntStream
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetOrderTest(
    @Autowired private val testDataAppUser: TestDataAppUser,
    @Autowired private val testDataOrder: TestDataOrder,
) : TestSupport() {
    private lateinit var appUser: JSONObject
    private lateinit var jwtToken: String

    private lateinit var otherUser: JSONObject
    private lateinit var otherJwtToken: String

    @BeforeEach
    override fun setUp() {
        super.setUp()
        val createdUserResponse = createResponseBodyJson(testDataAppUser.createAppUser())
        appUser = createdUserResponse.getJSONObject("appUserEntity")
        jwtToken = createdUserResponse.getString("token")

        val otherCreateUserResponse = createResponseBodyJson(
            testDataAppUser.createAppUser(
                appUserName = "サブユーザー",
                email = "sum_mail@test.gmail.com",
                password = "SubPassword123"
            )
        )
        otherUser = otherCreateUserResponse.getJSONObject("appUserEntity")
        otherJwtToken = otherCreateUserResponse.getString("token")
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
            appUser.getString("appUserName"),
            otherUser.getString("appUserName")
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

    @Test
    fun 全申請情報取得するときに申請情報が存在しない場合_空のリストが返る() {
        val resultActions = mockMvc.perform(
            get("/order").contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $jwtToken")
        ).andExpect(status().isOk)
        val responseArray = createResponseJsonArray(resultActions)
        assertEquals("[]", responseArray.toString())
    }

    @Test
    fun 単一申請情報を取得できる() {
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
        val createOrderResponse =
            createResponseBodyJson(testDataOrder.createOrder(requestOrders[1], otherJwtToken))

        val resultActions = mockMvc.perform(
            get("/order/${createOrderResponse.getString("id")}").contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $jwtToken")
        ).andExpect(status().isOk)
        val responseBody = createResponseBodyJson(resultActions)
        assertNotNull(responseBody.getString("id"))
        assertEquals("サブユーザー", responseBody.getString("appUserName"))
        assertEquals("テスト 試薬発注申請B", responseBody.getString("title"))
        assertNotNull(responseBody.getString("createdAt"))
        // 申請詳細のアサート
        val orderDetails = responseBody.getJSONArray("orderDetails")
        IntStream.range(0, orderDetails.length()).forEach { i ->
            val orderDetail = orderDetails.getJSONObject(i)
            assertNotNull(orderDetail.getString("orderDetailId"))
            assertEquals(
                requestOrders[1].orderDetails[i].reagentName,
                orderDetail.getString("reagentName")
            )
            assertEquals(
                requestOrders[1].orderDetails[i].url,
                orderDetail.getString("url")
            )
            assertEquals(
                requestOrders[1].orderDetails[i].count.toString(),
                orderDetail.getString("count")
            )
            assertEquals(OrderStatus.PENDING.value, orderDetail.getString("status"))
            assertNotNull(orderDetail.get("createdAt"))
            assertEquals("null", orderDetail.getString("updatedAt"))
        }
    }

    @Test
    fun 申請詳細情報を取得できる() {
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
        val createOrderResponse =
            createResponseBodyJson(testDataOrder.createOrder(requestOrders[1], otherJwtToken))
        val requestOrderDetail = createOrderResponse.getJSONArray("orderDetails").getJSONObject(0)
        val resultActions = mockMvc.perform(
            get(
                "/order/orderDetail/${requestOrderDetail.getString("orderDetailId")}"
            ).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $jwtToken")
        ).andExpect(status().isOk)
        val responseBody = createResponseBodyJson(resultActions)
        assertEquals(
            requestOrderDetail.getString("orderDetailId"),
            responseBody.getString("orderDetailId")
        )
        assertEquals(
            requestOrderDetail.getString("reagentName"),
            responseBody.getString("reagentName")
        )
        assertEquals(requestOrderDetail.getString("url"), responseBody.getString("url"))
        assertEquals(requestOrderDetail.getString("count"), responseBody.getString("count"))
        assertEquals(requestOrderDetail.getString("status"), responseBody.getString("status"))
        assertNotNull(responseBody.getString("createdAt"))
        assertEquals(requestOrderDetail.getString("updatedAt"), responseBody.getString("updatedAt"))
    }

    @Test
    fun 申請詳細情報が見つからない場合_E0013エラーになる() {
        val resultActions = mockMvc.perform(
            get(
                "/order/orderDetail/9999"
            ).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $jwtToken")
        ).andExpect(status().isNotFound)
        val responseBody = createResponseBodyJson(resultActions)
        assertEquals(ErrorCode.E0013.code, responseBody.getString("errorCode"))
        assertEquals(ErrorCode.E0013.message, responseBody.getString("message"))
    }

    @Test
    fun 申請情報が見つからない場合_E0014エラーになる() {
        val resultActions = mockMvc.perform(
            get(
                "/order/9999"
            ).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $jwtToken")
        ).andExpect(status().isNotFound)
        val responseBody = createResponseBodyJson(resultActions)
        assertEquals(ErrorCode.E0014.code, responseBody.getString("errorCode"))
        assertEquals(ErrorCode.E0014.message, responseBody.getString("message"))
    }
}