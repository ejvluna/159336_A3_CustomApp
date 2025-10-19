// /config/TrustedSources.kt
package com.example.customapp.config

/**
 * Pre-defined trusted sources for fact-checking.
 * Used in search_domain_filter parameter to ensure
 * information comes from reliable sources only.
 */
object TrustedSources {
    val DOMAINS = listOf(
        // News organizations
        "reuters.com",
        "apnews.com",
        "npr.org",
        "bbc.com",
        "theguardian.com",

        // Encyclopedias
        "britannica.com",

        // Fact-checkers
        "snopes.com",
        "factcheck.org",
        "politifact.com",

        // Government/Scientific
        "cdc.gov",
        "nasa.gov",
        "who.int"
    )
}
