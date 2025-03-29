package kkito.reagent_order.order

import kkito.reagent_order.TestSupport
import kkito.reagent_order.app_user.value.Role
import kkito.reagent_order.error.ErrorCode
import kkito.reagent_order.lib.fixture.AppUserFixture
import kkito.reagent_order.order.value.ChangeOrderRequest
import kkito.reagent_order.order.value.OrderStatus
import kkito.reagent_order.test_data.TestDataAppUser
import kkito.reagent_order.test_data.TestDataOrder
import org.assertj.db.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ChangeOrderStatusTest(
    @Autowired private val passwordEncoder: PasswordEncoder,
    @Autowired private val testDataAppUser: TestDataAppUser,
    @Autowired private val testDataOrder: TestDataOrder,
) :
    TestSupport() {

    companion object {
        private val TABLE_NAMES = listOf("app_user", "user_order", "order_detail", "order_set")
    }

    private lateinit var orderDetailId: String

    @BeforeEach
    override fun setUp() {
        super.setUp()
        testDataAppUser.createAppUser()
        val jwtToken = testDataAppUser.login()
        val createOrderResponse =
            createResponseBodyJson(testDataOrder.createOrder(jwtToken = jwtToken))
        orderDetailId =
            createOrderResponse
                .getJSONArray("orderDetails")
                .getJSONObject(0)
                .getString("orderDetailId")
    }

    @ParameterizedTest
    @CsvSource(
        "'申請ステータスを発注完了にできる', 'completed'",
        "'申請ステータスをキャンセルにできる', 'cancelled'")
    fun 申請ステータスを発注完了にできる(description: String, status: String) {
        // 管理ユーザー作成・ログイン
        val adminUser = AppUserFixture(passwordEncoder)
        adminUser.insert(dslContext)
        val adminLoginId = testDataAppUser.login(adminUser.email, adminUser.password)

        // テスト対象の実行
        val changes = createChanges(TABLE_NAMES).setStartPointNow()
        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.put("/order/orderDetail/$orderDetailId")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer $adminLoginId")
                .content(objectMapper.writeValueAsString(ChangeOrderRequest(status)))
        ).andExpect(MockMvcResultMatchers.status().isOk)
        changes.setEndPointNow()
        val response = createResponseBodyJson(resultActions)

        // レスポンス検証
        val targetOrderDetail = testDataOrder.userOrderRequest.orderDetails[0]
        assertEquals(status, response.getString("status"))
        assertEquals(targetOrderDetail.reagentName, response.getString("reagentName"))
        assertEquals(targetOrderDetail.count.toString(), response.getString("count"))
        assertEquals(targetOrderDetail.url, response.getString("url"))
        assertNotNull(response.getString(("createdAt")))
        assertNotNull(response.getString("updatedAt"))
        assertNotNull(response.getString("orderDetailId"))

        // DB検証
        Assertions.assertThat(changes)
            .ofDeletionOnTable("order_detail")
            .hasNumberOfChanges(0)
            .ofCreationOnTable("order_detail")
            .hasNumberOfChanges(0)
            .ofModificationOnTable("order_detail")
            .hasNumberOfChanges(1)
            .change()
            .isModification()
            .rowAtEndPoint()
            .value("id").isNotNull()
            .value("reagent_name").isEqualTo(targetOrderDetail.reagentName)
            .value("url").isEqualTo(targetOrderDetail.url)
            .value("count").isEqualTo(targetOrderDetail.count)
            .value("status").isEqualTo(status)
            .value("created_at").isNotNull()
            .value("updated_at").isNotNull()
            .value("deleted_at").isNull()
    }

    @Test
    fun 一般ユーザーがステータスを更新しようとする場合_E0009エラーになる() {
        // 管理ユーザー作成・ログイン
        val appUser = AppUserFixture(passwordEncoder, role = Role.USER.value)
        appUser.insert(dslContext)
        val adminLoginId = testDataAppUser.login(appUser.email, appUser.password)

        // テスト対象の実行
        val changes = createChanges(TABLE_NAMES).setStartPointNow()
        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.put("/order/orderDetail/$orderDetailId")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer $adminLoginId")
                .content(objectMapper.writeValueAsString(ChangeOrderRequest(OrderStatus.COMPLETED.value)))
        ).andExpect(MockMvcResultMatchers.status().isForbidden)
        changes.setEndPointNow()
        val response = createResponseBodyJson(resultActions)

        // レスポンス検証
        assertEquals(ErrorCode.E0009.code, response.getString("errorCode"))
        assertEquals(ErrorCode.E0009.message, response.getString("message"))

        // DB検証
        Assertions.assertThat(changes)
            .ofDeletionOnTable("order_detail")
            .hasNumberOfChanges(0)
            .ofCreationOnTable("order_detail")
            .hasNumberOfChanges(0)
            .ofModificationOnTable("order_detail")
            .hasNumberOfChanges(0)
    }
}