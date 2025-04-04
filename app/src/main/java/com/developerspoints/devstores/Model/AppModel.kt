package com.developerspoints.devstores.Model

data class AppModel(
    val id: String,
    val name: String,
    val developer: String,
    val description: String,
    val logoUrl: String,
    val downloads: Int,
    val rating: Float,
    val fileUrl: String
)
