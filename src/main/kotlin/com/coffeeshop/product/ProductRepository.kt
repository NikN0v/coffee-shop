package com.coffeeshop.product

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Long> {

    fun findAllByCategoryId(categoryId: Long, pageable: Pageable): Page<Product>

    fun findAllByAvailable(available: Boolean, pageable: Pageable): Page<Product>

    fun findAllByCategoryIdAndAvailable(
        categoryId: Long,
        available: Boolean,
        pageable: Pageable
    ): Page<Product>
}