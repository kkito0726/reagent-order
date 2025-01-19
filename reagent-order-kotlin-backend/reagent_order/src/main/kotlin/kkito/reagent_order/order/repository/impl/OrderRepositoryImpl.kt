package kkito.reagent_order.order.repository.impl

import com.generate.jooq.Tables.APP_USER
import com.generate.jooq.Tables.ORDER_DETAIL
import com.generate.jooq.Tables.ORDER_SET
import com.generate.jooq.Tables.USER_ORDER
import kkito.reagent_order.app_user.value.AppUserId
import kkito.reagent_order.app_user.value.AppUserName
import kkito.reagent_order.error.ErrorCode
import kkito.reagent_order.error.InternalServerError
import kkito.reagent_order.error.NotFoundException
import kkito.reagent_order.order.entity.OrderDetailEntity
import kkito.reagent_order.order.entity.OrderEntity
import kkito.reagent_order.order.entity.OrderSetEntity
import kkito.reagent_order.order.repository.OrderRepository
import kkito.reagent_order.order.value.*
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
open class OrderRepositoryImpl(private val dslContext: DSLContext) : OrderRepository {
    override fun createOrder(orderDto: OrderDto): List<OrderSetEntity> {
        val orderSetEntities = mutableListOf<OrderSetEntity>()
        dslContext.transaction { config ->
            val transactionDsl = DSL.using(config)
            val userOrderRecord = transactionDsl.insertInto(USER_ORDER)
                .set(USER_ORDER.APP_USER_ID, orderDto.appUserId.toString())
                .set(USER_ORDER.TITLE, orderDto.title)
                .set(USER_ORDER.CREATED_AT, orderDto.createdAt)
                .returning(USER_ORDER.ID) // 挿入後のIDを取得
                .fetchOne()
                ?: throw InternalServerError(ErrorCode.E0012)
            val orderId = userOrderRecord.id

            val orderDetailRecords = orderDto.orderDetailDtoList.map {
                transactionDsl.insertInto(ORDER_DETAIL)
                    .set(ORDER_DETAIL.REAGENT_NAME, it.reagentName.value)
                    .set(ORDER_DETAIL.URL, it.url)
                    .set(ORDER_DETAIL.COUNT, it.count)
                    .set(ORDER_DETAIL.STATUS, it.status.value)
                    .set(ORDER_DETAIL.CREATED_AT, it.createdAt)
                    .returning(ORDER_DETAIL.ID)
                    .fetchOne()
                    ?: throw InternalServerError(ErrorCode.E0012)
            }

            orderDetailRecords.map {
                orderSetEntities.add(
                    transactionDsl.insertInto(ORDER_SET)
                        .set(ORDER_SET.ORDER_ID, orderId)
                        .set(ORDER_SET.ORDER_DETAIL_ID, it.id)
                        .returning()
                        .fetchOne()
                        ?.into(OrderSetEntity::class.java)
                        ?: throw InternalServerError(ErrorCode.E0012)
                )
            }
        }
        return orderSetEntities
    }

    override fun getOrders(orderId: UserOrderId?): List<OrderEntity> {
        val baseCondition = listOf(
            USER_ORDER.DELETED_AT.isNull,
            ORDER_DETAIL.DELETED_AT.isNull
        )

        // orderId が渡された場合、条件を追加
        val conditions = if (orderId != null) {
            baseCondition + USER_ORDER.ID.eq(orderId.value)
        } else {
            baseCondition
        }

        val record = dslContext.select(
            USER_ORDER.ID,
            APP_USER.APP_USER_NAME,
            USER_ORDER.TITLE,
            USER_ORDER.CREATED_AT,
            ORDER_DETAIL.ID,
            ORDER_DETAIL.REAGENT_NAME,
            ORDER_DETAIL.URL,
            ORDER_DETAIL.COUNT,
            ORDER_DETAIL.STATUS,
            ORDER_DETAIL.CREATED_AT,
            ORDER_DETAIL.UPDATED_AT
        )
            .from(USER_ORDER)
            .innerJoin(APP_USER).on(USER_ORDER.APP_USER_ID.eq(APP_USER.ID))
            .innerJoin(ORDER_SET).on(USER_ORDER.ID.eq(ORDER_SET.ORDER_ID))
            .innerJoin(ORDER_DETAIL).on(ORDER_SET.ORDER_DETAIL_ID.eq(ORDER_DETAIL.ID))
            .where(conditions)
            .orderBy(USER_ORDER.CREATED_AT)

        val group = record.groupBy { it[USER_ORDER.ID] }
        return group.map { (id, row) ->
            val firstRow = row.first()
            OrderEntity(
                id = UserOrderId(id),
                appUserName = AppUserName(firstRow[APP_USER.APP_USER_NAME]),
                title = firstRow[USER_ORDER.TITLE],
                createdAt = firstRow[USER_ORDER.CREATED_AT],
                orderDetailEntities = row.map {
                    OrderDetailEntity(
                        id = OrderDetailId(it[ORDER_DETAIL.ID]),
                        reagentName = ReagentName(it[ORDER_DETAIL.REAGENT_NAME]),
                        url = it[ORDER_DETAIL.URL],
                        count = ReagentCount(it[ORDER_DETAIL.COUNT]),
                        status = OrderStatus.fromValue(it[ORDER_DETAIL.STATUS]),
                        createdAt = it[ORDER_DETAIL.CREATED_AT],
                        updatedAt = it[ORDER_DETAIL.UPDATED_AT]
                    )
                }
            )
        }
    }

    override fun getOrderDetail(orderDetailId: OrderDetailId): OrderDetailEntity {
        val record = dslContext.select(
            ORDER_DETAIL.ID,
            ORDER_DETAIL.REAGENT_NAME,
            ORDER_DETAIL.URL,
            ORDER_DETAIL.COUNT,
            ORDER_DETAIL.STATUS,
            ORDER_DETAIL.CREATED_AT,
            ORDER_DETAIL.UPDATED_AT
        ).from(ORDER_DETAIL)
            .where(ORDER_DETAIL.ID.eq(orderDetailId.value))
            .and(ORDER_DETAIL.DELETED_AT.isNull())
            .fetchOne()
            ?: throw NotFoundException(ErrorCode.E0013)
        return OrderDetailEntity(
            id = OrderDetailId(record[ORDER_DETAIL.ID]),
            reagentName = ReagentName(record[ORDER_DETAIL.REAGENT_NAME]),
            url = record[ORDER_DETAIL.URL],
            count = ReagentCount(record[ORDER_DETAIL.COUNT]),
            status = OrderStatus.fromValue(record[ORDER_DETAIL.STATUS]),
            createdAt = record[ORDER_DETAIL.CREATED_AT],
            updatedAt = record[ORDER_DETAIL.UPDATED_AT]
        )
    }

    override fun getAppUserIdByOrderId(orderId: UserOrderId): AppUserId? {
        return dslContext.select(APP_USER.ID)
            .from(USER_ORDER)
            .innerJoin(APP_USER).on(USER_ORDER.APP_USER_ID.eq(APP_USER.ID))
            .where(USER_ORDER.ID.eq(orderId.value))
            .fetchOneInto(AppUserId::class.java)
    }

    override fun getAppUserIdByOrderDetailId(orderDetailId: OrderDetailId): AppUserId? {
        return dslContext.select(APP_USER.ID)
            .from(ORDER_SET)
            .innerJoin(USER_ORDER).on(ORDER_SET.ORDER_ID.eq(USER_ORDER.ID))
            .innerJoin(APP_USER).on(USER_ORDER.APP_USER_ID.eq(APP_USER.ID))
            .where(ORDER_SET.ORDER_DETAIL_ID.eq(orderDetailId.value))
            .fetchOneInto(AppUserId::class.java)
    }

    override fun deleteOrder(orderId: UserOrderId, orderDetailIds: List<OrderDetailId>) {
        dslContext.transaction { configuration ->
            val ctx = DSL.using(configuration)
            ctx.update(USER_ORDER)
                .set(USER_ORDER.DELETED_AT, LocalDateTime.now())
                .where(USER_ORDER.ID.eq(orderId.value))
                .execute()

            if (orderDetailIds.isNotEmpty()) {
                ctx.update(ORDER_DETAIL)
                    .set(ORDER_DETAIL.DELETED_AT, LocalDateTime.now())
                    .where(ORDER_DETAIL.ID.`in`(orderDetailIds.map { it.value }))
                    .execute()
            }
        }
    }

    override fun deleteOrderDetail(orderDetailId: OrderDetailId) {
        dslContext.update(ORDER_DETAIL)
            .set(ORDER_DETAIL.DELETED_AT, LocalDateTime.now())
            .where(ORDER_DETAIL.ID.eq(orderDetailId.value))
            .execute()
    }
}