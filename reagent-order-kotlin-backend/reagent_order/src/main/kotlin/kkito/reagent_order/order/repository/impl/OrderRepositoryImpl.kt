package kkito.reagent_order.order.repository.impl

import com.generate.jooq.Tables.APP_USER
import com.generate.jooq.Tables.ORDER_DETAIL
import com.generate.jooq.Tables.ORDER_SET
import com.generate.jooq.Tables.USER_ORDER
import kkito.reagent_order.app_user.value.AppUserName
import kkito.reagent_order.error.ErrorCode
import kkito.reagent_order.error.InternalServerError
import kkito.reagent_order.order.entity.OrderDetailEntity
import kkito.reagent_order.order.entity.OrderEntity
import kkito.reagent_order.order.entity.OrderSetEntity
import kkito.reagent_order.order.repository.OrderRepository
import kkito.reagent_order.order.value.OrderDto
import kkito.reagent_order.order.value.OrderStatus
import kkito.reagent_order.order.value.ReagentCount
import kkito.reagent_order.order.value.ReagentName
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

    override fun getOrders(): List<OrderEntity> {
        val record = dslContext.select(
            USER_ORDER.ID.`as`("id"),
            APP_USER.APP_USER_NAME.`as`("appUserName"),
            USER_ORDER.TITLE.`as`("title"),
            USER_ORDER.CREATED_AT.`as`("createdAt"),
            ORDER_DETAIL.ID.`as`("orderDetailId"),
            ORDER_DETAIL.REAGENT_NAME.`as`("reagentName"),
            ORDER_DETAIL.URL.`as`("url"),
            ORDER_DETAIL.COUNT.`as`("count"),
            ORDER_DETAIL.STATUS.`as`("status"),
            ORDER_DETAIL.CREATED_AT.`as`("orderDetailCreatedAt"),
            ORDER_DETAIL.UPDATED_AT.`as`("orderDetailUpdatedAt")
        )
            .from(USER_ORDER)
            .innerJoin(APP_USER).on(USER_ORDER.APP_USER_ID.eq(APP_USER.ID))
            .innerJoin(ORDER_SET).on(USER_ORDER.ID.eq(ORDER_SET.ORDER_ID))
            .innerJoin(ORDER_DETAIL).on(ORDER_SET.ORDER_DETAIL_ID.eq(ORDER_DETAIL.ID))
            .where(USER_ORDER.DELETED_AT.isNull, ORDER_DETAIL.DELETED_AT.isNull)

        val group = record.groupBy { it["id"] as Long }
        return group.map {(id, row) ->
            val firstRow = row.first()
            OrderEntity(
                id = id,
                appUserName = AppUserName(firstRow["appUserName"] as String),
                title = firstRow["title"] as String,
                createdAt = firstRow["createdAt"] as LocalDateTime,
                orderDetailEntities = row.map {
                    OrderDetailEntity(
                        id = it["orderDetailId"] as Long,
                        reagentName = ReagentName(it["reagentName"] as String),
                        url = it["url"] as String,
                        count = ReagentCount(it["count"] as Int),
                        status = OrderStatus.fromValue(it["status"] as String),
                        createdAt = it["orderDetailCreatedAt"] as LocalDateTime,
                        updatedAt = it["orderDetailUpdatedAt"] as LocalDateTime?
                    )
                }
            )
        }
    }
}