package com.coffeeshop.category

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "categories")
class Category(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "name_ru", nullable = false, length = 100)
    var nameRu: String,

    @Column(name = "name_en", nullable = false, length = 100)
    var nameEn: String,
)