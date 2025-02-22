package kkito.reagent_order.order.controller

import kkito.reagent_order.app_user.value.AppUserId
import kkito.reagent_order.order.service.OrderService
import kkito.reagent_order.order.value.*
import kkito.reagent_order.util.ControllerUtil
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import java.time.LocalDateTime
import java.util.*

@Controller
class OrderController(private val orderService: OrderService) : ControllerUtil() {
    @PostMapping("/order")
    fun postUserOrder(@RequestBody userOrderRequest: UserOrderRequest): ResponseEntity<UserOrderResponse> {
        val authAppUser = user()
        val orderDto = OrderDto(
            AppUserId(UUID.fromString(authAppUser.id.toString())),
            userOrderRequest.title,
            userOrderRequest.orderDetails.map {
                OrderDetailDto(
                    ReagentName(it.reagentName),
                    it.url,
                    it.count,
                    OrderStatus.PENDING
                )
            }
        )
        return ResponseEntity.ok(orderService.postUserOrder(orderDto, authAppUser))
    }

    // 申請詳細追加API
    @PostMapping("/order/orderDetail/{orderId}")
    fun postOrderDetail(
        @PathVariable orderId: Long,
        @RequestBody orderDetailRequest: OrderDetailRequest
    ): ResponseEntity<OrderDetailResponse> {
        val authAppUserEntity = user()
        val orderDetailDto = OrderDetailDto(
            reagentName = ReagentName(orderDetailRequest.reagentName),
            url = orderDetailRequest.url,
            count = orderDetailRequest.count,
            status = OrderStatus.PENDING,
            createdAt = LocalDateTime.now()
        )
        return ResponseEntity.ok(
            orderService.postOrderDetail(
                UserOrderId(orderId),
                orderDetailDto,
                authAppUserEntity
            )
        )
    }

    @GetMapping("/order")
    fun getUserOrders(): ResponseEntity<List<UserOrderResponse>> {
        return ResponseEntity.ok(orderService.getUserOrders())
    }

    @GetMapping("/order/{orderId}")
    fun getUserOrder(@PathVariable orderId: Long): ResponseEntity<UserOrderResponse> {
        return ResponseEntity.ok(orderService.getUserOrder(UserOrderId(orderId)))
    }

    @GetMapping("/order/orderDetail/{orderDetailId}")
    fun getOrderDetail(@PathVariable orderDetailId: Long): ResponseEntity<OrderDetailResponse> {
        return ResponseEntity.ok(orderService.getOrderDetail(OrderDetailId(orderDetailId)))
    }

    @PutMapping("/order/orderDetail/{orderDetailId}")
    fun changeOrderDetailStatus(
        @PathVariable orderDetailId: Long,
        @RequestBody changeOrderRequest: ChangeOrderRequest
    ): ResponseEntity<OrderDetailResponse> {
        return ResponseEntity.ok(
            orderService.changeOrderDetailStatus(
                user(),
                OrderDetailId(orderDetailId),
                OrderStatus.fromValue(changeOrderRequest.orderStatus)
            )
        )
    }

    @DeleteMapping("/order/{orderId}")
    fun deleteOrder(@PathVariable orderId: Long): ResponseEntity<Any> {
        val authAppUserEntity = user()
        return ResponseEntity.ok(
            orderService.deleteOrder(
                UserOrderId(orderId),
                authAppUserEntity
            )
        )
    }

    @DeleteMapping("/order/orderDetail/{orderDetailId}")
    fun deleteOrderDetail(@PathVariable orderDetailId: Long): ResponseEntity<Any> {
        val authAppUserEntity = user()
        return ResponseEntity.ok(
            orderService.deleteOrderDetail(
                OrderDetailId(orderDetailId),
                authAppUserEntity
            )
        )
    }
}