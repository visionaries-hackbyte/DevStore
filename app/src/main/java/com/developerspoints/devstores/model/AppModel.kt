package com.developerspoints.devstores.model

data class AppModel(
    val fileName: String = "",
    val fileType: String = "",
    val fileUrl: String = "",
    val picUrl: String = "",
    val uploadId: String = "",
    val uploadTime: Long = 0L,
    val uploadedBy: String = "",
    val description: String = ""
)
