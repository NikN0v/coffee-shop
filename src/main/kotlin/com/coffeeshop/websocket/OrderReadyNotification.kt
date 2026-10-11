package com.coffeeshop.websocket

data class OrderReadyNotification(
    val orderId: Long,
    val status: String,
    val message: String
)