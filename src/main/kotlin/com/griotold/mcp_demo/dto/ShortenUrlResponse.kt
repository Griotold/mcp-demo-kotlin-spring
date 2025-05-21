package com.griotold.mcp_demo.dto

data class ShortenUrlResponse(
    val shortUrl: String
) {
    companion object {
        fun fromShortKey(shortKey: String): ShortenUrlResponse =
            ShortenUrlResponse("/api/shorten/$shortKey")
    }
}
