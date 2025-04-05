package com.developerspoints.devstores.Model

data class dmodel(
    val fileName: String = "",
    val developerName: String = "",
    val description: String = "",
    val fileUrl: String = "",
    val logoUrl: String = "",  // ✅ Ensure this field exists
    val timestamp: Long = 0L
)

