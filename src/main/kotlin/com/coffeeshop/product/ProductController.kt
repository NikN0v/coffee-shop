package com.coffeeshop.product

import com.coffeeshop.common.dto.PageResponse
import com.coffeeshop.product.dto.ProductCreateRequest
import com.coffeeshop.product.dto.ProductPatchRequest
import com.coffeeshop.product.dto.ProductResponse
import com.coffeeshop.product.dto.ProductUpdateRequest
import com.fasterxml.jackson.databind.JsonNode
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.Locale

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productService: ProductService
) {

    @GetMapping
    fun getAll(
        @RequestParam(required = false) categoryId: Long?,
        @RequestParam(required = false) available: Boolean?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "id") sortBy: String,
        @RequestParam(defaultValue = "asc") direction: String,
        locale: Locale
    ): PageResponse<ProductResponse> =
        productService.search(
            categoryId = categoryId,
            available = available,
            page = page,
            size = size,
            sortBy = sortBy,
            direction = direction,
            locale = locale
        )

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long, locale: Locale): ProductResponse = productService.getById(id, locale)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: ProductCreateRequest,
        locale: Locale
    ): ProductResponse = productService.create(request, locale)

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: ProductUpdateRequest,
        locale: Locale
    ): ProductResponse = productService.update(id, request, locale)

    @PatchMapping("/{id}")
    fun patch(
        @PathVariable id: Long,
        @Valid @RequestBody request: ProductPatchRequest,
        locale: Locale
    ): ProductResponse = productService.patch(id, request, locale)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) {
        productService.delete(id)
    }
}