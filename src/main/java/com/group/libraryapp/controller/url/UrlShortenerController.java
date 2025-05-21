package com.group.libraryapp.controller.url;

import com.group.libraryapp.dto.url.request.ShortenUrlRequest;
import com.group.libraryapp.dto.url.response.ShortenUrlResponse;
import com.group.libraryapp.service.url.UrlShortenerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shorten")
public class UrlShortenerController {

    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerController.class);
    private final UrlShortenerService urlShortenerService;

    public UrlShortenerController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @PostMapping
    public ResponseEntity<ShortenUrlResponse> shortenUrl(@RequestBody ShortenUrlRequest request) {
        logger.info("Received request to shorten URL: {}", request.getUrl());
        
        if (request.getUrl() == null || request.getUrl().trim().isEmpty()) {
            logger.info("Invalid URL provided");
            return ResponseEntity.badRequest().build();
        }
        
        ShortenUrlResponse response = urlShortenerService.shortenUrl(request);
        logger.info("URL shortened successfully: {}", response.getShortUrl());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{shortKey}")
    public ResponseEntity<String> redirectToOriginalUrl(@PathVariable String shortKey) {
        logger.info("Received request to get original URL for key: {}", shortKey);
        
        String originalUrl = urlShortenerService.getOriginalUrl(shortKey);
        
        if (originalUrl == null) {
            logger.info("No URL found for key: {}", shortKey);
            return ResponseEntity.notFound().build();
        }
        
        logger.info("Returning original URL: {}", originalUrl);
        return ResponseEntity.ok(originalUrl);
    }
}
