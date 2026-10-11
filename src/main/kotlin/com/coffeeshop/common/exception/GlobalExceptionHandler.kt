package com.coffeeshop.common.exception

import com.coffeeshop.common.dto.ApiError
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.server.ResponseStatusException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ApiError> {
        val details = ex.bindingResult.fieldErrors.map {
            "${it.field}: ${it.defaultMessage ?: "Некорректное значение"}"
        }

        return ResponseEntity.badRequest().body(
            ApiError(
                status = 400,
                error = "Bad Request",
                message = "Ошибка валидации",
                details = details
            )
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadableBody(): ResponseEntity<ApiError> =
        ResponseEntity.badRequest().body(
            ApiError(
                status = 400,
                error = "Bad Request",
                message = "Некорректное тело запроса"
            )
        )

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(
        ex: MethodArgumentTypeMismatchException
    ): ResponseEntity<ApiError> =
        ResponseEntity.badRequest().body(
            ApiError(
                status = 400,
                error = "Bad Request",
                message = "Некорректный параметр: ${ex.name}"
            )
        )

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(): ResponseEntity<ApiError> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ApiError(
                status = 500,
                error = "Internal Server Error",
                message = "Внутренняя ошибка сервера"
            )
        )

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatus(
        ex: ResponseStatusException
    ): ResponseEntity<ApiError> {
        val status = ex.statusCode.value()

        return ResponseEntity.status(status).body(
            ApiError(
                status = status,
                error = ex.statusCode.toString(),
                message = ex.reason ?: "Ошибка запроса"
            )
        )
    }
}