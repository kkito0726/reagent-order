package kkito.reagent_order.order.repository

import kkito.reagent_order.order.entity.OrderEntity
import kkito.reagent_order.order.entity.OrderSetEntity
import kkito.reagent_order.order.value.OrderDto
import kkito.reagent_order.order.value.UserOrderId

interface OrderRepository {
    fun createOrder(orderDto: OrderDto): List<OrderSetEntity>
    fun getOrders(orderId: UserOrderId? = null): List<OrderEntity>
}