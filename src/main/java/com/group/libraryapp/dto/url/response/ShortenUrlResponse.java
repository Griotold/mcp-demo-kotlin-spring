package com.group.libraryapp.dto.url.response;

public class ShortenUrlResponse {
    private final String shortUrl;

    public ShortenUrlResponse(String shortKey) {
        this.shortUrl = "/api/shorten/" + shortKey;
    }

    public String getShortUrl() {
        return shortUrl;
    }
}
