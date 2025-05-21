package com.group.libraryapp.service.url;

import com.group.libraryapp.domain.url.Url;
import com.group.libraryapp.domain.url.UrlRepository;
import com.group.libraryapp.dto.url.request.ShortenUrlRequest;
import com.group.libraryapp.dto.url.response.ShortenUrlResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UrlShortenerService {

    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerService.class);
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int KEY_LENGTH = 6;
    
    // In-memory hashmap for storing shortened URLs
    private final Map<String, String> urlMap = new HashMap<>();
    private final UrlRepository urlRepository;
    private final SecureRandom random = new SecureRandom();

    public UrlShortenerService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Transactional
    public ShortenUrlResponse shortenUrl(ShortenUrlRequest request) {
        String originalUrl = request.getUrl();
        logger.info("Shortening URL: {}", originalUrl);

        String shortKey = generateUniqueKey();
        
        // Store in both in-memory map and database
        urlMap.put(shortKey, originalUrl);
        urlRepository.save(new Url(shortKey, originalUrl));
        
        logger.info("URL shortened successfully. Short key: {}", shortKey);
        return new ShortenUrlResponse(shortKey);
    }

    @Transactional(readOnly = true)
    public String getOriginalUrl(String shortKey) {
        logger.info("Looking up original URL for short key: {}", shortKey);
        
        // First check in-memory map
        String originalUrl = urlMap.get(shortKey);
        
        // If not found in memory, try the database
        if (originalUrl == null) {
            Optional<Url> urlOptional = urlRepository.findByShortKey(shortKey);
            if (urlOptional.isPresent()) {
                originalUrl = urlOptional.get().getOriginalUrl();
                // Cache in memory for future lookups
                urlMap.put(shortKey, originalUrl);
            } else {
                logger.info("No URL found for key: {}", shortKey);
                return null;
            }
        }
        
        logger.info("Original URL found: {}", originalUrl);
        return originalUrl;
    }

    private String generateUniqueKey() {
        String key;
        do {
            key = generateKey();
        } while (urlMap.containsKey(key) || urlRepository.existsByShortKey(key));
        
        return key;
    }

    private String generateKey() {
        StringBuilder sb = new StringBuilder(KEY_LENGTH);
        
        // Ensure first character is a letter
        sb.append(CHARACTERS.charAt(random.nextInt(52))); // First 52 chars are letters
        
        // Add remaining characters (can be letters or digits)
        for (int i = 1; i < KEY_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        
        return sb.toString();
    }
}
