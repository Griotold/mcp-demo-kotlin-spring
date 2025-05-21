package com.griotold.mcp_demo.service

import com.griotold.mcp_demo.domain.Url
import com.griotold.mcp_demo.dto.ShortenUrlRequest
import com.griotold.mcp_demo.dto.ShortenUrlResponse
import com.griotold.mcp_demo.repository.UrlRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom

@Service
class UrlShortenerService(
    private val urlRepository: UrlRepository
) {
    private val logger = KotlinLogging.logger {}
    private val urlMap: MutableMap<String, String> = HashMap()
    private val random = SecureRandom()
    
    companion object {
        private const val CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        private const val KEY_LENGTH = 6
    }
    
    @Transactional
    fun shortenUrl(request: ShortenUrlRequest): ShortenUrlResponse {
        val originalUrl = request.url
        logger.info { "Shortening URL: $originalUrl" }
        
        val shortKey = generateUniqueKey()
        
        // Store in both in-memory map and database
        urlMap[shortKey] = originalUrl
        urlRepository.save(Url(shortKey, originalUrl))
        
        logger.info { "URL shortened successfully. Short key: $shortKey" }
        return ShortenUrlResponse(shortKey)
    }
    
    @Transactional(readOnly = true)
    fun getOriginalUrl(shortKey: String): String? {
        logger.info { "Looking up original URL for short key: $shortKey" }
        
        // First check in-memory map
        var originalUrl = urlMap[shortKey]
        
        // If not found in memory, try the database
        if (originalUrl == null) {
            val urlOptional = urlRepository.findByShortKey(shortKey)
            if (urlOptional.isPresent) {
                originalUrl = urlOptional.get().originalUrl
                // Cache in memory for future lookups
                urlMap[shortKey] = originalUrl
            } else {
                logger.info { "No URL found for key: $shortKey" }
                return null
            }
        }
        
        logger.info { "Original URL found: $originalUrl" }
        return originalUrl
    }
    
    private fun generateUniqueKey(): String {
        var key: String
        do {
            key = generateKey()
        } while (urlMap.containsKey(key) || urlRepository.existsByShortKey(key))
        
        return key
    }
    
    private fun generateKey(): String {
        val sb = StringBuilder(KEY_LENGTH)
        
        // Ensure first character is a letter
        sb.append(CHARACTERS[random.nextInt(52)]) // First 52 chars are letters
        
        // Add remaining characters (can be letters or digits)
        for (i in 1 until KEY_LENGTH) {
            sb.append(CHARACTERS[random.nextInt(CHARACTERS.length)])
        }
        
        return sb.toString()
    }
}
