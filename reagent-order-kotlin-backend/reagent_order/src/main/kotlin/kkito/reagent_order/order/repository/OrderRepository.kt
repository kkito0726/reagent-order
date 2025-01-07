package kkito.reagent_order.order.repository

import kkito.reagent_order.order.entity.OrderSetEntity
import kkito.reagent_order.order.value.OrderDto

interface OrderRepository {
    fun createOrder(orderDto: OrderDto): List<OrderSetEntity>
}