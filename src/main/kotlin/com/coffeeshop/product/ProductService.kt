package com.coffeeshop.product

import com.coffeeshop.category.CategoryRepository
import com.coffeeshop.common.dto.PageResponse
import com.coffeeshop.product.dto.ProductCreateRequest
import com.coffeeshop.product.dto.ProductPatchRequest
import com.coffeeshop.product.dto.ProductResponse
import com.coffeeshop.product.dto.ProductUpdateRequest
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.util.Locale

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) {

    @Transactional(readOnly = true)
    fun getAll(locale: Locale): List<ProductResponse> =
        productRepository.findAll().map { it.toResponse(locale) }

    @Transactional(readOnly = true)
    fun getById(id: Long, locale: Locale): ProductResponse =
        findProduct(id).toResponse(locale)

    @Transactional
    fun create(
        request: ProductCreateRequest,
        locale: Locale
    ): ProductResponse {
        val category = categoryRepository.findById(
            requireNotNull(request.categoryId)
        ).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Категория не найдена")
        }

        validateProductDetails(
            isDrink = request.isDrink,
            volume = request.volume
        )

        val product = Product(
            category = category,
            nameRu = request.nameRu.trim(),
            nameEn = request.nameEn.trim(),
            descriptionRu = request.descriptionRu,
            descriptionEn = request.descriptionEn,
            price = requireNotNull(request.price),
            available = request.available,
            weight = request.weight,
            isDrink = request.isDrink,
            volume = request.volume
        )

        return productRepository.save(product).toResponse(locale)
    }

    @Transactional
    fun update(
        id: Long,
        request: ProductUpdateRequest,
        locale: Locale
    ): ProductResponse {
        val product = findProduct(id)

        val category = categoryRepository.findById(
            requireNotNull(request.categoryId)
        ).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Категория не найдена")
        }

        val newIsDrink = request.isDrink ?: product.isDrink
        val newVolume = request.volume

        validateProductDetails(
            isDrink = newIsDrink,
            volume = newVolume
        )

        product.category = category
        product.nameRu = request.nameRu.trim()
        product.nameEn = request.nameEn.trim()
        product.descriptionRu = request.descriptionRu
        product.descriptionEn = request.descriptionEn
        product.price = requireNotNull(request.price)
        product.available = requireNotNull(request.available)
        product.weight = request.weight
        product.isDrink = newIsDrink
        product.volume = newVolume

        return productRepository.save(product).toResponse(locale)
    }

    @Transactional
    fun patch(
        id: Long,
        request: ProductPatchRequest,
        locale: Locale
    ): ProductResponse {
        val product = findProduct(id)

        request.categoryId?.let { categoryId ->
            product.category = categoryRepository.findById(categoryId).orElseThrow {
                ResponseStatusException(HttpStatus.NOT_FOUND, "Категория не найдена")
            }
        }

        request.nameRu?.let { product.nameRu = it.trim() }
        request.nameEn?.let { product.nameEn = it.trim() }
        request.descriptionRu?.let { product.descriptionRu = it }
        request.descriptionEn?.let { product.descriptionEn = it }
        request.price?.let { product.price = it }
        request.available?.let { product.available = it }
        request.weight?.let { product.weight = it }

        val newIsDrink = request.isDrink ?: product.isDrink

        // volume == null в запросе может означать как «не менять», так и «сбросить»
        // для случая перехода из напитка в обычный товар, поэтому сверяемся с флагом isDrink
        val newVolume = when {
            request.volume != null -> request.volume
            request.isDrink != null && !request.isDrink -> null
            else -> product.volume
        }

        validateProductDetails(
            isDrink = newIsDrink,
            volume = newVolume
        )

        product.isDrink = newIsDrink
        product.volume = newVolume

        return productRepository.save(product).toResponse(locale)
    }

    @Transactional
    fun delete(id: Long) {
        val product = findProduct(id)
        productRepository.delete(product)
    }

    private fun findProduct(id: Long): Product =
        productRepository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Товар не найден")
        }

    private fun Product.toResponse(locale: Locale) = ProductResponse(
        id = requireNotNull(id),
        categoryId = category.id!!,
        name = if (locale.language == "en") nameEn else nameRu,
        description = if (locale.language == "en") descriptionEn else descriptionRu,
        price = price,
        available = available,
        weight = weight,
        isDrink = isDrink,
        volume = volume,
        nameRu = nameRu,
        nameEn = nameEn,
        descriptionRu = descriptionRu,
        descriptionEn = descriptionEn
    )

    private fun validateProductDetails(
        isDrink: Boolean,
        volume: Int?
    ) {
        if (isDrink && (volume == null || volume <= 0)) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Для напитка необходимо указать положительный объём"
            )
        }

        if (!isDrink && volume != null) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Объём можно указать только для напитка"
            )
        }
    }

    @Transactional(readOnly = true)
    fun search(
        categoryId: Long?,
        available: Boolean?,
        page: Int,
        size: Int,
        sortBy: String,
        direction: String,
        locale: Locale
    ): PageResponse<ProductResponse> {
        require(page >= 0) { "Номер страницы не может быть отрицательным" }
        require(size in 1..100) { "Размер страницы должен быть от 1 до 100" }

        val allowedSortFields = setOf("id", "price", "nameRu", "nameEn", "available")
        require(sortBy in allowedSortFields) { "Недопустимое поле сортировки" }

        val sortDirection = when (direction.lowercase()) {
            "asc" -> Sort.Direction.ASC
            "desc" -> Sort.Direction.DESC
            else -> throw IllegalArgumentException(
                "Направление сортировки должно быть asc или desc"
            )
        }

        val pageable = PageRequest.of(
            page,
            size,
            Sort.by(sortDirection, sortBy)
        )

        val result = when {
            categoryId != null && available != null ->
                productRepository.findAllByCategoryIdAndAvailable(
                    categoryId, available, pageable
                )

            categoryId != null ->
                productRepository.findAllByCategoryId(categoryId, pageable)

            available != null ->
                productRepository.findAllByAvailable(available, pageable)

            else ->
                productRepository.findAll(pageable)
        }

        return PageResponse(
            content = result.content.map { it.toResponse(locale) },
            page = result.number,
            size = result.size,
            totalElements = result.totalElements,
            totalPages = result.totalPages,
            first = result.isFirst,
            last = result.isLast
        )
    }
}