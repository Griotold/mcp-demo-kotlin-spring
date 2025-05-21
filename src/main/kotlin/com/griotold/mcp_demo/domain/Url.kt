package com.griotold.mcp_demo.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Url(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false, unique = true)
    val shortKey: String,
    
    @Column(nullable = false)
    val originalUrl: String
) {
    constructor(shortKey: String, originalUrl: String) : this(null, shortKey, originalUrl)
}
