// /config/TrustedSources.kt
package com.example.customapp.config

// Pre-defined trusted domains for Sonar API search filtering implemented as a Kotlin `object` for lazy initialization and single instance guarantee
object TrustedSources {
    // Domains are listed to 20 per Sonar API documentation, and organized by source type for maintainability and clarity
    val DOMAINS = listOf(
        // News organizations: Reuters, AP News, NPR, BBC, The Guardian
        "reuters.com",
        "apnews.com",
        "npr.org",
        "bbc.com",
        "theguardian.com",

        // Encyclopedias: Britannica, Stanford, Scholarpedia, Encyclopedia.com
        "britannica.com",
        "plato.stanford.edu",
        "scholarpedia.org",
        "encyclopedia.com",

        // Fact-checkers: Snopes, FactCheck.org, PolitiFact
        "snopes.com",
        "factcheck.org",
        "politifact.com",

        // Government/Scientific: CDC, NASA, WHO, NIH, Nature, Science, ScienceDirect, JSTOR
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
