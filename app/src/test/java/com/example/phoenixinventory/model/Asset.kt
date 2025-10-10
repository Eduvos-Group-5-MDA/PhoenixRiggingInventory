package com.example.myapp.model

data class Asset(
    val assetId: Int = 0,
    val name: String,
    val status: String,
    val location: String? = null,
    val category: String? = null
)
