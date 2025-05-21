package com.griotold.mcp_demo.controller

import com.griotold.mcp_demo.dto.ShortenUrlRequest
import com.griotold.mcp_demo.dto.ShortenUrlResponse
import com.griotold.mcp_demo.service.UrlShortenerService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/shorten")
class UrlShortenerController(
    private val urlShortenerService: UrlShortenerService
) {
    private val logger = KotlinLogging.logger {}
    
    @PostMapping
    fun shortenUrl(@RequestBody request: ShortenUrlRequest): ResponseEntity<ShortenUrlResponse> {
        logger.info { "Received request to shorten URL: ${request.url}" }
        
        if (request.url.isNullOrBlank()) {
            logger.info { "Invalid URL provided" }
            return ResponseEntity.badRequest().build()
        }
        
        val response = urlShortenerService.shortenUrl(request)
        logger.info { "URL shortened successfully: ${response.shortUrl}" }
        
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/{shortKey}")
    fun redirectToOriginalUrl(@PathVariable shortKey: String): ResponseEntity<String> {
        logger.info { "Received request to get original URL for key: $shortKey" }
        
        val originalUrl = urlShortenerService.getOriginalUrl(shortKey)
        
        if (originalUrl == null) {
            logger.info { "No URL found for key: $shortKey" }
            return ResponseEntity.notFound().build()
        }
        
        logger.info { "Returning original URL: $originalUrl" }
        return ResponseEntity.ok(originalUrl)
    }
}
