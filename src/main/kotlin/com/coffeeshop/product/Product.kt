package com.coffeeshop.product

import com.coffeeshop.category.Category
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "products")
class Product(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "category_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_products_category")
    )
    var category: Category,

    @Column(name = "name_ru", nullable = false, length = 150)
    var nameRu: String,

    @Column(name = "name_en", nullable = false, length = 150)
    var nameEn: String,

    @Column(name = "description_ru")
    var descriptionRu: String? = null,

    @Column(name = "description_en")
    var descriptionEn: String? = null,

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    var price: BigDecimal,

    @Column(name = "available", nullable = false)
    var available: Boolean = true,

    @Column(name = "weight")
    var weight: Int? = null,

    @Column(name = "is_drink", nullable = false)
    var isDrink: Boolean = false,

    @Column(name = "volume")
    var volume: Int? = null
)