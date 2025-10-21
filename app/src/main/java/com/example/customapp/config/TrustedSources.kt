// /config/TrustedSources.kt
package com.example.customapp.config

// Pre-defined trusted sources for fact-checking.Used in search_domain_filter parameter to ensure information comes from reliable sources only.
// Limit is 20 per SonarAPI documentation: https://docs.perplexity.ai/guides/search-quickstart

object TrustedSources {
    val DOMAINS = listOf(
        // News organizations: only internationally recognized media outlets with established editorial standards, fact-checking processes, and a reputation for accurate, unbiased reporting across global events
        "reuters.com",
        "apnews.com",
        "npr.org",
        "bbc.com",
        "theguardian.com",

        // Encyclopedias: only curated sources maintained by subject-matter experts through peer review and editorial oversight, not crowd-sourced content like Wikipedia
        "britannica.com",
        "plato.stanford.edu",
        "scholarpedia.org",
        "encyclopedia.com",

        // Fact-checkers: only reputable fact-checking organizations with established processes for verifying claims and maintaining high standards of accuracy and integrity
        "snopes.com",
        "factcheck.org",
        "politifact.com",

        // Government/Scientific: only official government websites and reputable scientific organizations with established processes for verifying claims and maintaining high standards of accuracy and integrity
        "cdc.gov",
        "nasa.gov",
        "who.int",
        "nih.gov",
        "nature.com",
        "sciencemag.org",
        "sciencedirect.com",
        "jstor.org",

    )
}
