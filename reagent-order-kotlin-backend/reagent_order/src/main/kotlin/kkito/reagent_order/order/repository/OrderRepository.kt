package kkito.reagent_order.order.repository

import kkito.reagent_order.app_user.value.AppUserId
import kkito.reagent_order.order.entity.OrderDetailEntity
import kkito.reagent_order.order.entity.OrderEntity
import kkito.reagent_order.order.entity.OrderSetEntity
import kkito.reagent_order.order.value.OrderDetailDto
import kkito.reagent_order.order.value.OrderDetailId
import kkito.reagent_order.order.value.OrderDto
import kkito.reagent_order.order.value.UserOrderId

interface OrderRepository {
    fun createOrder(orderDto: OrderDto): List<OrderSetEntity>
    fun createOrderDetail(orderId: UserOrderId, orderDetailDto: OrderDetailDto): OrderSetEntity
    fun isOrderExist(orderId: UserOrderId): Boolean
    fun getOrders(orderId: UserOrderId? = null): List<OrderEntity>
    fun getOrderDetail(orderDetailId: OrderDetailId): OrderDetailEntity
    fun getAppUserIdByOrderId(orderId: UserOrderId): AppUserId?
    fun getAppUserIdByOrderDetailId(orderDetailId: OrderDetailId): AppUserId?
    fun deleteOrder(orderId: UserOrderId, orderDetailIds: List<OrderDetailId>)
    fun deleteOrderDetail(orderDetailId: OrderDetailId)
}