package kkito.reagent_order.order.service

import kkito.reagent_order.app_user.entity.AppUserEntity
import kkito.reagent_order.error.ErrorCode
import kkito.reagent_order.error.NotFoundException
import kkito.reagent_order.order.repository.OrderRepository
import kkito.reagent_order.order.value.*
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderAuthSpec: OrderAuthSpec
) {
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
                id = orderEntity.id.value,
                appUserName = orderEntity.appUserName.value,
                title = orderEntity.title,
                createdAt = orderEntity.createdAt,
                orderDetails = orderEntity.orderDetailEntities.map {
                    OrderDetailResponse(
                        orderDetailId = it.id.value,
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

    fun getUserOrder(orderId: UserOrderId): UserOrderResponse {
        val orderEntity = orderRepository.getOrders(orderId).firstOrNull()
            ?: throw NotFoundException(ErrorCode.E0014)
        return UserOrderResponse(
            id = orderEntity.id.value,
            appUserName = orderEntity.appUserName.value,
            title = orderEntity.title,
            createdAt = orderEntity.createdAt,
            orderDetails = orderEntity.orderDetailEntities.map {
                OrderDetailResponse(
                    orderDetailId = it.id.value,
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

    fun getOrderDetail(orderDetailId: OrderDetailId): OrderDetailResponse {
        val orderDetailEntity = orderRepository.getOrderDetail(orderDetailId)
        return OrderDetailResponse(
            orderDetailId = orderDetailEntity.id.value,
            reagentName = orderDetailEntity.reagentName.value,
            url = orderDetailEntity.url,
            count = orderDetailEntity.count.value,
            status = orderDetailEntity.status.value,
            createdAt = orderDetailEntity.createdAt,
            updatedAt = orderDetailEntity.updatedAt,
        )
    }

    fun deleteOrder(orderId: UserOrderId, authAppUserEntity: AppUserEntity) {
        orderAuthSpec.check(orderId, authAppUserEntity)
        val orderDetailIds =
            orderRepository.getOrders(orderId).firstOrNull()?.orderDetailEntities?.map {
                it.id
            } ?: listOf()
        orderRepository.deleteOrder(orderId, orderDetailIds)
    }

    fun deleteOrderDetail(orderDetailId: OrderDetailId, authAppUserEntity: AppUserEntity) {
        orderAuthSpec.check(orderDetailId, authAppUserEntity)
        orderRepository.deleteOrderDetail(orderDetailId)
    }
}