package kkito.reagent_order.order.controller

import kkito.reagent_order.app_user.value.AppUserId
import kkito.reagent_order.order.service.OrderService
import kkito.reagent_order.order.value.*
import kkito.reagent_order.util.ControllerUtil
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.*

@Controller
class OrderController(private val orderService: OrderService): ControllerUtil() {
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
    fun getUserOrder(): ResponseEntity<List<UserOrderResponse>> {
        return ResponseEntity.ok(orderService.getUserOrders())
    }
}