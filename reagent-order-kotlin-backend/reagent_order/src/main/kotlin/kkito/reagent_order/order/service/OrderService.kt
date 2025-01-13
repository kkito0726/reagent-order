package kkito.reagent_order.order.service

import kkito.reagent_order.app_user.entity.AppUserEntity
import kkito.reagent_order.order.repository.OrderRepository
import kkito.reagent_order.order.value.OrderDetailResponse
import kkito.reagent_order.order.value.OrderDto
import kkito.reagent_order.order.value.UserOrderResponse
import org.springframework.stereotype.Service

@Service
class OrderService(private val orderRepository: OrderRepository) {
    fun postUserOrder(userOrderDto: OrderDto, authAppUser: AppUserEntity): UserOrderResponse {
        val orderSetEntities = orderRepository.createOrder(userOrderDto)
        return UserOrderResponse(
            orderSetEntities[0].orderId,
            authAppUser.appUserName.value,
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

    fun getUserOrders(): List<UserOrderResponse> {
        val orderEntities = orderRepository.getOrders()

        return orderEntities.map { orderEntity ->
            UserOrderResponse(
                id = orderEntity.id,
                appUserName = orderEntity.appUserName.value,
                title = orderEntity.title,
                createdAt = orderEntity.createdAt,
                orderDetails = orderEntity.orderDetailEntities.map {
                    OrderDetailResponse(
                        orderDetailId = it.id,
                        reagentName = it.reagentName.value,
                        url = it.url,
                        count = it.count.value,
                        status = it.status.value,
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt
                    )
                }
            )
        }
    }
}