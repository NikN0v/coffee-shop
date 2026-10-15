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
    private val orderReadyService: OrderReadyService
) {
    fun schedule(orderId: Long) {
        val delayMinutes = ThreadLocalRandom.current().nextLong(1, 6)

        taskScheduler.schedule(
            { orderReadyService.markReady(orderId) },
            Instant.now().plusSeconds(delayMinutes * 60)
        )
    }
}