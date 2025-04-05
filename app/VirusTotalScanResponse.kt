package com.developerspoints.malware.model

import com.google.gson.annotations.SerializedName

data class VirusTotalScanResponse(
    @SerializedName("data") val data: ScanData?
)

data class ScanData(
    @SerializedName("id") val id: String?,
    @SerializedName("type") val type: String?
)
