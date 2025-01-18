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
import org.springframework.web.bind.annotation.RequestBody
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