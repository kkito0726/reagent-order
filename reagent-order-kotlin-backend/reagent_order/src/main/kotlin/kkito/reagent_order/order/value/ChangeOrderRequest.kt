package kkito.reagent_order.order.value

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ChangeOrderRequest @JsonCreator constructor(
    @JsonProperty("orderStatus") val orderStatus: String
) {
    fun toOrderStatus(): OrderStatus = OrderStatus.fromValue(orderStatus)
}