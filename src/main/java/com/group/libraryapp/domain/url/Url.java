package com.group.libraryapp.domain.url;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
public class Url {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String shortKey;

    @Column(nullable = false)
    private String originalUrl;

    public Url() {
    }

    public Url(String shortKey, String originalUrl) {
        this.shortKey = shortKey;
        this.originalUrl = originalUrl;
    }

    public String getShortKey() {
        return shortKey;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }
}
