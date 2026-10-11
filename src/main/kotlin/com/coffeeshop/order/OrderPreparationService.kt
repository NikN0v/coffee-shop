package com.coffeeshop.order

import com.coffeeshop.websocket.OrderReadyNotification
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.concurrent.ThreadLocalRandom

@Service
class OrderPreparationService(
    private val taskScheduler: TaskScheduler,
    private val orderRepository: OrderRepository,
    private val messagingTemplate: SimpMessagingTemplate
) {
    fun schedule(orderId: Long) {
        val delayMinutes = ThreadLocalRandom.current().nextLong(1, 11)

        taskScheduler.schedule(
            { markReady(orderId) },
            Instant.now().plusSeconds(delayMinutes * 60)
        )
    }

    @Transactional
    fun markReady(orderId: Long) {
        val order = orderRepository.findById(orderId).orElse(null) ?: return

        if (order.status != OrderStatus.PREPARING) return

        order.status = OrderStatus.READY
        orderRepository.save(order)

        messagingTemplate.convertAndSendToUser(
            order.user.email,
            "/queue/orders",
            OrderReadyNotification(
                orderId = order.id!!,
                status = OrderStatus.READY.name,
                message = "Ваш заказ готов!"
            )
        )
    }
}