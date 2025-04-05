package com.developerspoints.devstores.model

data class AppItem(
    val uploadId: String,
    val fileName: String,
    val description: String,
    val fileUrl: String,
    val picUrl: String,
    val uploadedBy: String,
    val uploadTime: Long
)

