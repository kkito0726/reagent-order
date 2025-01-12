package kkito.reagent_order.order.service

import kkito.reagent_order.order.repository.OrderRepository
import kkito.reagent_order.order.value.OrderDetailResponse
import kkito.reagent_order.order.value.OrderDto
import kkito.reagent_order.order.value.UserOrderResponse
import org.springframework.stereotype.Service

@Service
class OrderService(private val orderRepository: OrderRepository) {
    fun postUserOrder(userOrderDto: OrderDto): UserOrderResponse {
        val orderSetEntities = orderRepository.createOrder(userOrderDto)
        return UserOrderResponse(
            orderSetEntities[0].orderId,
            userOrderDto.title,
            userOrderDto.createdAt,
            orderSetEntities.mapIndexed() { i, it ->
                OrderDetailResponse(
                    it.orderDetailId,
                    userOrderDto.orderDetailDtoList[i].reagentName.value,
                    userOrderDto.orderDetailDtoList[i].url,
                    userOrderDto.orderDetailDtoList[i].count,
                    userOrderDto.orderDetailDtoList[i].status.value,
                    userOrderDto.createdAt,
                    null
                )
            }
        )
    }
}