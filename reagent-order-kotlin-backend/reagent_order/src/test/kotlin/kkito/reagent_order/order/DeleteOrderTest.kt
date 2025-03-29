package kkito.reagent_order.order

import kkito.reagent_order.TestSupport
import kkito.reagent_order.error.ErrorCode
import kkito.reagent_order.test_data.TestDataAppUser
import kkito.reagent_order.test_data.TestDataOrder
import org.assertj.db.api.Assertions
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class DeleteOrderTest(
    @Autowired private val testDataAppUser: TestDataAppUser,
    @Autowired private val testDataOrder: TestDataOrder
) : TestSupport() {
    companion object {
        private val TABLE_NAMES = listOf("app_user", "user_order", "order_detail", "order_set")
    }

    private lateinit var jwtToken: String
    private lateinit var createOrderResponse: JSONObject

    @BeforeEach
    override fun setUp() {
        super.setUp()
        testDataAppUser.createAppUser()
        jwtToken = testDataAppUser.login()
        createOrderResponse = createResponseBodyJson(testDataOrder.createOrder(jwtToken = jwtToken))
    }

    @Test
    fun 試薬発注申請を削除できる() {
        val changes = createChanges(TABLE_NAMES).setStartPointNow()
        val resultActions = mockMvc.perform(
            delete("/order/${createOrderResponse.getString("id")}").contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $jwtToken")
        ).andExpect(status().isOk)
        changes.setEndPointNow()
        val response = createResponseBodyJson(resultActions)

        assertEquals("{}", response.toString())

        Assertions.assertThat(changes)
            .ofModificationOnTable("user_order")
            .hasNumberOfChanges(1)
            .change()
            .rowAtEndPoint()
            .value("deleted_at").isNotNull()

        Assertions.assertThat(changes)
            .ofModificationOnTable("order_detail")
            .hasNumberOfChanges(2)
            .change()
            .rowAtEndPoint()
            .value("reagent_name").isEqualTo("DMEM")
            .value("deleted_at").isNotNull()
            .value("updated_at").isNull()
            .change()
            .rowAtEndPoint()
            .value("reagent_name").isEqualTo("P/S")
            .value("deleted_at").isNotNull()
            .value("updated_at").isNull()
    }

    @Test
    fun 試薬発注申請詳細を削除できる() {
        val changes = createChanges(TABLE_NAMES).setStartPointNow()
        val resultActions = mockMvc.perform(
            delete(
                "/order/orderDetail/${
                    createOrderResponse
                        .getJSONArray("orderDetails")
                        .getJSONObject(0)
                        .getString("orderDetailId")
                }"
            ).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $jwtToken")
        ).andExpect(status().isOk)
        changes.setEndPointNow()
        val response = createResponseBodyJson(resultActions)

        assertEquals("{}", response.toString())

        Assertions.assertThat(changes)
            .ofModificationOnTable("order_detail")
            .hasNumberOfChanges(1)
            .change()
            .isModification()
            .rowAtEndPoint()
            .value("reagent_name").isEqualTo("DMEM")
            .value("updated_at").isNull()
            .value("deleted_at").isNotNull()
    }

    @Test
    fun 他のユーザーが試薬発注申請を削除しようとする場合_E0009エラーになる() {
        // 他ユーザー作成
        createResponseBodyJson(
            testDataAppUser.createAppUser(
                appUserName = "サブユーザー",
                email = "sum_mail@test.gmail.com",
                password = "SubPassword123"
            )
        )
        val otherJwtToken = testDataAppUser.login(
            email = "sum_mail@test.gmail.com",
            password = "SubPassword123"
        )

        val resultActions = mockMvc.perform(
            delete(
                "/order/orderDetail/${
                    createOrderResponse
                        .getJSONArray("orderDetails")
                        .getJSONObject(0)
                        .getString("orderDetailId")
                }"
            ).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $otherJwtToken")
        ).andExpect(status().isForbidden)
        val response = createResponseBodyJson(resultActions)
        assertEquals(ErrorCode.E0009.code, response.getString("errorCode"))
        assertEquals(ErrorCode.E0009.message, response.getString("message"))
    }

    @Test
    fun 他ユーザーが試薬発注申請詳細を削除しようとする場合_E0009エラーになる() {
        // 他ユーザー作成
        createResponseBodyJson(
            testDataAppUser.createAppUser(
                appUserName = "サブユーザー",
                email = "sum_mail@test.gmail.com",
                password = "SubPassword123"
            )
        )
        val otherJwtToken = testDataAppUser.login(
            email = "sum_mail@test.gmail.com",
            password = "SubPassword123"
        )

        val resultActions = mockMvc.perform(
            delete("/order/${createOrderResponse.getString("id")}").contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $otherJwtToken")
        ).andExpect(status().isForbidden)
        val response = createResponseBodyJson(resultActions)
        assertEquals(ErrorCode.E0009.code, response.getString("errorCode"))
        assertEquals(ErrorCode.E0009.message, response.getString("message"))
    }
}