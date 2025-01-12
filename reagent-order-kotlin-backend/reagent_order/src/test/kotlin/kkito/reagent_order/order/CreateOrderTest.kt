package kkito.reagent_order.order

import com.fasterxml.jackson.databind.ObjectMapper
import kkito.reagent_order.TestSupport
import kkito.reagent_order.order.value.OrderDetailRequest
import kkito.reagent_order.order.value.OrderStatus
import kkito.reagent_order.order.value.UserOrderRequest
import kkito.reagent_order.test_data.TestDataAppUser
import org.assertj.db.api.Assertions
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CreateOrderTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
    @Autowired private val testDataAppUser: TestDataAppUser,
) : TestSupport() {
    companion object {
        private val TABLE_NAMES = listOf("app_user", "user_order", "order_detail", "order_set")
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
    fun 試薬発注申請できる() {
        val request = UserOrderRequest(
            "テスト 試薬発注申請A",
            listOf(
                OrderDetailRequest(
                    "DMEM",
                    "https://labchem-wako.fujifilm.com/jp/product/detail/W01W0104-2978.html",
                    3
                )
            )
        )
        val changes = createChanges(TABLE_NAMES).setStartPointNow()
        val resultActions = mockMvc.perform(
            post("/order").contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $jwtToken")
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk)
        changes.setEndPointNow()

        val responseBody = createResponseBodyJson(resultActions)
        assertNotNull(responseBody.get("id"))
        assertEquals(request.title, responseBody.getString("title"))
        assertNotNull(responseBody.get("createdAt"))
        assertNotNull(
            responseBody.getJSONArray("orderDetails").getJSONObject(0).get("orderDetailId")
        )
        assertEquals(
            request.orderDetails[0].reagentName,
            responseBody.getJSONArray("orderDetails").getJSONObject(0).getString("reagentName")
        )
        assertEquals(
            request.orderDetails[0].url,
            responseBody.getJSONArray("orderDetails").getJSONObject(0).getString("url")
        )
        assertEquals(
            request.orderDetails[0].count.toString(),
            responseBody.getJSONArray("orderDetails").getJSONObject(0).getString("count")
        )
        assertEquals(
            "pending",
            responseBody.getJSONArray("orderDetails").getJSONObject(0).getString("status")
        )
        assertNotNull(
            responseBody.getJSONArray("orderDetails").getJSONObject(0).get("orderDetailId")
        )

        // DBアサート
        Assertions.assertThat(changes)
            .ofModificationOnTable("user_order")
            .hasNumberOfChanges(0)
            .ofDeletionOnTable("user_order")
            .hasNumberOfChanges(0)
            .ofCreationOnTable("user_order")
            .hasNumberOfChanges(1)
            .change()
            .isCreation()
            .rowAtEndPoint()
            .value("id").isNotNull()
            .value("app_user_id").isEqualTo(createdUserResponse.getString("id"))
            .value("title").isEqualTo(request.title)
            .value("created_at").isNotNull()
            .value("deleted_at").isNull()

        Assertions.assertThat(changes)
            .ofModificationOnTable("order_detail")
            .hasNumberOfChanges(0)
            .ofDeletionOnTable("order_detail")
            .hasNumberOfChanges(0)
            .ofCreationOnTable("order_detail")
            .hasNumberOfChanges(1)
            .change()
            .isCreation()
            .rowAtEndPoint()
            .value("id").isNotNull()
            .value("reagent_name").isEqualTo(request.orderDetails[0].reagentName)
            .value("url").isEqualTo(request.orderDetails[0].url)
            .value("count").isEqualTo(request.orderDetails[0].count)
            .value("status").isEqualTo(OrderStatus.PENDING.value)
            .value("created_at").isNotNull()
            .value("updated_at").isNull()
            .value("deleted_at").isNull()

        Assertions.assertThat(changes)
            .ofModificationOnTable("order_set")
            .hasNumberOfChanges(0)
            .ofDeletionOnTable("order_set")
            .hasNumberOfChanges(0)
            .ofCreationOnTable("order_set")
            .hasNumberOfChanges(1)
    }

    @Test
    fun 試薬発注申請できる_複数申請詳細() {
        val request = UserOrderRequest(
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
        val changes = createChanges(TABLE_NAMES).setStartPointNow()
        val resultActions = mockMvc.perform(
            post("/order").contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $jwtToken")
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk)
        changes.setEndPointNow()

        val responseBody = createResponseBodyJson(resultActions)
        assertNotNull(responseBody.get("id"))
        assertEquals(request.title, responseBody.getString("title"))
        assertNotNull(responseBody.get("createdAt"))
        assertNotNull(
            responseBody.getJSONArray("orderDetails").getJSONObject(0).get("orderDetailId")
        )
        // 申請詳細1件目
        assertEquals(
            request.orderDetails[0].reagentName,
            responseBody.getJSONArray("orderDetails").getJSONObject(0).getString("reagentName")
        )
        assertEquals(
            request.orderDetails[0].url,
            responseBody.getJSONArray("orderDetails").getJSONObject(0).getString("url")
        )
        assertEquals(
            request.orderDetails[0].count.toString(),
            responseBody.getJSONArray("orderDetails").getJSONObject(0).getString("count")
        )
        assertEquals(
            OrderStatus.PENDING.value,
            responseBody.getJSONArray("orderDetails").getJSONObject(0).getString("status")
        )
        assertNotNull(
            responseBody.getJSONArray("orderDetails").getJSONObject(0).get("orderDetailId")
        )

        // 申請詳細2件目
        assertEquals(
            request.orderDetails[1].reagentName,
            responseBody.getJSONArray("orderDetails").getJSONObject(1).getString("reagentName")
        )
        assertEquals(
            request.orderDetails[1].url,
            responseBody.getJSONArray("orderDetails").getJSONObject(1).getString("url")
        )
        assertEquals(
            request.orderDetails[1].count.toString(),
            responseBody.getJSONArray("orderDetails").getJSONObject(1).getString("count")
        )
        assertEquals(
            OrderStatus.PENDING.value,
            responseBody.getJSONArray("orderDetails").getJSONObject(1).getString("status")
        )
        assertNotNull(
            responseBody.getJSONArray("orderDetails").getJSONObject(1).get("orderDetailId")
        )

        // DBアサート
        Assertions.assertThat(changes)
            .ofModificationOnTable("user_order")
            .hasNumberOfChanges(0)
            .ofDeletionOnTable("user_order")
            .hasNumberOfChanges(0)
            .ofCreationOnTable("user_order")
            .hasNumberOfChanges(1)
            .change()
            .isCreation()
            .rowAtEndPoint()
            .value("id").isNotNull()
            .value("app_user_id").isEqualTo(createdUserResponse.getString("id"))
            .value("title").isEqualTo(request.title)
            .value("created_at").isNotNull()
            .value("deleted_at").isNull()

        Assertions.assertThat(changes)
            .ofModificationOnTable("order_detail")
            .hasNumberOfChanges(0)
            .ofDeletionOnTable("order_detail")
            .hasNumberOfChanges(0)
            .ofCreationOnTable("order_detail")
            .hasNumberOfChanges(2)
            // 申請詳細1件目
            .change()
            .isCreation()
            .rowAtEndPoint()
            .value("id").isNotNull()
            .value("reagent_name").isEqualTo(request.orderDetails[0].reagentName)
            .value("url").isEqualTo(request.orderDetails[0].url)
            .value("count").isEqualTo(request.orderDetails[0].count)
            .value("status").isEqualTo(OrderStatus.PENDING.value)
            .value("created_at").isNotNull()
            .value("updated_at").isNull()
            .value("deleted_at").isNull()
            // 申請詳細2件目
            .change()
            .isCreation()
            .rowAtEndPoint()
            .value("id").isNotNull()
            .value("reagent_name").isEqualTo(request.orderDetails[1].reagentName)
            .value("url").isEqualTo(request.orderDetails[1].url)
            .value("count").isEqualTo(request.orderDetails[1].count)
            .value("status").isEqualTo(OrderStatus.PENDING.value)
            .value("created_at").isNotNull()
            .value("updated_at").isNull()
            .value("deleted_at").isNull()

        Assertions.assertThat(changes)
            .ofModificationOnTable("order_set")
            .hasNumberOfChanges(0)
            .ofDeletionOnTable("order_set")
            .hasNumberOfChanges(0)
            .ofCreationOnTable("order_set")
            .hasNumberOfChanges(2)
    }
}