package com.griotold.mcp_demo.repository

import com.griotold.mcp_demo.domain.Url
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UrlRepository : JpaRepository<Url, Long> {
    fun findByShortKey(shortKey: String): Optional<Url>
    fun existsByShortKey(shortKey: String): Boolean
}
