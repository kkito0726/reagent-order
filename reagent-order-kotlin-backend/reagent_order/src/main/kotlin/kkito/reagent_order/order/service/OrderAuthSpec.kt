package kkito.reagent_order.order.service

import kkito.reagent_order.app_user.entity.AppUserEntity
import kkito.reagent_order.error.ErrorCode
import kkito.reagent_order.error.ForbiddenException
import kkito.reagent_order.error.NotFoundException
import kkito.reagent_order.order.repository.OrderRepository
import kkito.reagent_order.order.value.OrderDetailId
import kkito.reagent_order.order.value.UserOrderId
import org.springframework.stereotype.Service

@Service
class OrderAuthSpec(private val orderRepository: OrderRepository) {
    fun check(orderId: UserOrderId, authAppUserEntity: AppUserEntity) {
        val appUserId = orderRepository.getAppUserIdByOrderId(orderId)
            ?: throw NotFoundException(ErrorCode.E0014)

        if (appUserId != authAppUserEntity.id) {
            throw ForbiddenException(ErrorCode.E0009)
        }
    }
    fun check(orderDetailId: OrderDetailId, authAppUserEntity: AppUserEntity) {
        val appUserId = orderRepository.getAppUserIdByOrderDetailId(orderDetailId)
            ?: throw NotFoundException(ErrorCode.E0013)

        // ログインユーザーのIDと申請詳細に紐づくappUserIdが異なる場合
        if (appUserId != authAppUserEntity.id) {
            throw ForbiddenException(ErrorCode.E0009)
        }
    }
}