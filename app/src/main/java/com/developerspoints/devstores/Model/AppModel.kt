package com.developerspoints.devstores.Model

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
