package kkito.reagent_order.order.value

data class OrderDetailRequest(
    val reagentName: String,
    val url: String?,
    val count: Int
)
