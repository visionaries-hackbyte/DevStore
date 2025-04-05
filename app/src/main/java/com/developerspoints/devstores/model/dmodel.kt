package com.developerspoints.devstores.model

data class dmodel(
    val fileName: String = "",
    val developerName: String = "",
    val description: String = "",
    val fileUrl: String = "",
    val logoUrl: String = "",  // ✅ Ensure this field exists
    val timestamp: Long = 0L
)

