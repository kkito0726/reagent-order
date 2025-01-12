package kkito.reagent_order.order.repository.impl

import com.generate.jooq.Tables.ORDER_DETAIL
import com.generate.jooq.Tables.ORDER_SET
import com.generate.jooq.Tables.USER_ORDER
import kkito.reagent_order.error.ErrorCode
import kkito.reagent_order.error.InternalServerError
import kkito.reagent_order.order.entity.OrderSetEntity
import kkito.reagent_order.order.repository.OrderRepository
import kkito.reagent_order.order.value.OrderDto
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
}