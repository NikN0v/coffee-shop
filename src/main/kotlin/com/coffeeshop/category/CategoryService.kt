package com.coffeeshop.category

import com.coffeeshop.category.dto.CategoryCreateRequest
import com.coffeeshop.category.dto.CategoryResponse
import com.coffeeshop.category.dto.CategoryUpdateRequest
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.Locale

@Service
class CategoryService(private val categoryRepository: CategoryRepository) {

    @Transactional(readOnly = true)
    fun getAll(locale: Locale): List<CategoryResponse> {
        return categoryRepository.findAll().map { category ->
            CategoryResponse(
                id = requireNotNull(category.id),
                name = if (locale.language == "en") {
                    category.nameEn
                } else {
                    category.nameRu
                }
            )
        }
    }

    @Transactional
    fun create(request: CategoryCreateRequest): CategoryResponse {
        val category = Category(
            nameRu = request.nameRu.trim(),
            nameEn = request.nameEn.trim()
        )

        val saved = categoryRepository.save(category)

        return CategoryResponse(
            id = requireNotNull(saved.id),
            name = saved.nameRu
        )
    }

    @Transactional
    fun update(id: Long, request: CategoryUpdateRequest): CategoryResponse {
        val category = categoryRepository.findById(id)
            .orElseThrow {
                ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Category with id $id not found"
                )
            }

        category.nameRu = request.nameRu.trim()
        category.nameEn = request.nameEn.trim()

        val saved = categoryRepository.save(category)

        return CategoryResponse(
            id = requireNotNull(saved.id),
            name = if (LocaleContextHolder.getLocale().language == "ru") {
                saved.nameRu
            } else {
                saved.nameEn
            }
        )
    }

    @Transactional
    fun delete(id: Long) {
        if (!categoryRepository.existsById(id)) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Category with id $id not found"
            )
        }

        categoryRepository.deleteById(id)
    }
}