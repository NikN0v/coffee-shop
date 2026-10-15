package com.coffeeshop.order

import com.coffeeshop.websocket.OrderReadyNotification
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderReadyService(
    private val orderRepository: OrderRepository,
    private val messagingTemplate: SimpMessagingTemplate
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun markReady(orderId: Long) {
        val order = orderRepository.findById(orderId).orElse(null) ?: run {
            logger.warn("Order {} not found", orderId)
            return
        }

        if (order.status != OrderStatus.PREPARING) {
            logger.info("Order {} is not preparing: {}", orderId, order.status)
            return
        }

        order.status = OrderStatus.READY
        orderRepository.save(order)

        val email = order.user.email

        messagingTemplate.convertAndSendToUser(
            email,
            "/queue/orders",
            OrderReadyNotification(
                orderId = order.id!!,
                status = OrderStatus.READY.name,
                message = "Ваш заказ готов!"
            )
        )

        logger.info("READY notification sent for order {} to {}", orderId, email)
    }
}