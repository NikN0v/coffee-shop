package com.coffeeshop.order

import com.coffeeshop.order.dto.OrderCreateRequest
import com.coffeeshop.order.dto.OrderItemResponse
import com.coffeeshop.order.dto.OrderResponse
import com.coffeeshop.product.ProductRepository
import com.coffeeshop.user.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val orderItemRepository: OrderItemRepository
) {

    @Transactional
    fun create(userEmail: String, request: OrderCreateRequest): OrderResponse {
        val user = userRepository.findByEmail(userEmail)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Пользователь не найден"
            )

        val quantities = request.items
            .groupBy { it.productId }
            .mapValues { (_, items) -> items.sumOf { it.quantity } }

        val products = productRepository.findAllByIdInAndAvailableTrue(quantities.keys)

        if (products.size != quantities.size) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Один или несколько продуктов отсутствуют в магазине"
            )
        }

        val items = products.map { product ->
            val quantity = quantities.getValue(product.id!!)
            val unitPrice = product.price
            val itemTotal = unitPrice.multiply(BigDecimal.valueOf(quantity.toLong()))

            OrderItem(
                order = Order(
                    user = user,
                    totalPrice = BigDecimal.ZERO
                ),
                product = product,
                productName = product.nameRu,
                unitPrice = unitPrice,
                quantity = quantity,
                totalPrice = itemTotal
            )
        }

        val total = items.fold(BigDecimal.ZERO) { sum, item ->
            sum.add(item.totalPrice)
        }

        val order = orderRepository.save(
            Order(user = user, totalPrice = total)
        )

        val savedItems = orderItemRepository.saveAll(
            products.map { product ->
                val quantity = quantities.getValue(product.id!!)
                val unitPrice = product.price
                val itemTotal = unitPrice.multiply(BigDecimal.valueOf(quantity.toLong()))

                OrderItem(
                    order = order,
                    product = product,
                    productName = product.nameRu,
                    unitPrice = unitPrice,
                    quantity = quantity,
                    totalPrice = itemTotal
                )
            }
        )

        val itemResponses = savedItems.map { item ->
            OrderItemResponse(
                productId = item.product.id!!,
                productName = item.productName,
                unitPrice = item.unitPrice,
                quantity = item.quantity,
                totalPrice = item.totalPrice
            )
        }

        return OrderResponse(
            id = order.id!!,
            status = order.status,
            totalPrice = order.totalPrice,
            createdAt = order.createdAt,
            items = itemResponses
        )
    }

    @Transactional(readOnly = true)
    fun getMyOrders(userEmail: String): List<OrderResponse> {
        val user = userRepository.findByEmail(userEmail)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Пользователь не найден"
            )

        val orders = orderRepository.findAllByUserIdOrderByCreatedAtDesc(user.id!!)

        if (orders.isEmpty()) return emptyList()

        val itemsByOrder = orderItemRepository
            .findAllByOrderIdIn(orders.map { it.id!! })
            .groupBy { it.order.id!! }

        return orders.map { order ->
            toResponse(order, itemsByOrder[order.id].orEmpty())
        }
    }

    @Transactional(readOnly = true)
    fun getMyOrder(userEmail: String, orderId: Long): OrderResponse {
        val user = userRepository.findByEmail(userEmail)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Пользователь не найден"
            )

        val order = orderRepository.findByIdAndUserId(orderId, user.id!!)
            .orElseThrow {
                ResponseStatusException(HttpStatus.NOT_FOUND, "Заказ не найден")
            }

        val items = orderItemRepository.findAllByOrderId(order.id!!)

        return toResponse(order, items)
    }

    private fun toResponse(
        order: Order,
        items: List<OrderItem>
    ): OrderResponse =
        OrderResponse(
            id = order.id!!,
            status = order.status,
            totalPrice = order.totalPrice,
            createdAt = order.createdAt,
            items = items.map { item ->
                OrderItemResponse(
                    productId = item.product.id!!,
                    productName = item.productName,
                    unitPrice = item.unitPrice,
                    quantity = item.quantity,
                    totalPrice = item.totalPrice
                )
            }
        )
}