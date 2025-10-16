# High-Level Architecture & Design Document

## Project Overview

**App Name**: `Truthiness` (or your preferred name)

**Purpose**: An educational fact-checking application that validates user queries against pre-defined trusted information sources using Perplexity's Sonar API.[10][11]

**Target Platform**: Android (API level 24+ recommended)

**Development Language**: Kotlin

**Architecture Pattern**: MVVM (Model-View-ViewModel) with Jetpack Compose[2][5][1]

***

## Core Functionality

The app provides fact-checking capabilities by analyzing user-submitted claims against trusted information sources:

**User Input**
- User enters a claim or statement to verify (up to 500 characters)

**Fact-Checking Process**
1. App sends claim to Perplexity's Sonar API with pre-defined trusted domain filters
2. API performs web research across trusted sources (news, encyclopedias, academic, fact-checking organizations, government/scientific)
3. API returns detailed analysis with citations

**Results Display**
- **Overall Rating**: MOSTLY_TRUE, MIXED, or MOSTLY_FALSE (color-coded indicator)
- **Summary**: Brief overview of fact-checking findings
- **Detailed Analysis**: Explanation of the verification result
- **Citations**: List of sources with URLs used for verification
- **Action**: User can start a new query or view history

**History & Persistence**
- All queries and results are saved to local database
- Users can view past queries with timestamps
- Users can delete queries from history

***

## System Architecture

### Layer Structure

Your app will follow a two-layer architecture optimized for MVP simplicity:[5][1][2]

**1. Presentation Layer (UI)**
- Activity with Jetpack Compose
- Composables for UI screens
- ViewModels
- UI State management
- User input handling

**2. Data Layer**
- Repository pattern
- API service interface
- Local database (Room) for history
- Data models and mappers

***

## Component Architecture

### Core Components (Within 10-Class Limit)

**1. `MainActivity`** - Entry point with Compose UI[9][1]

**2. `QueryViewModel`** - Manages UI state and coordinates verification requests[2][1]

**3. `PerplexityRepository`** - Handles all API interactions and data operations[5][2]

**4. `SonarApiService`** - Retrofit interface for Perplexity API calls[2]

**5. `ClaimHistoryDao`** - Room database interface for local storage[2]

**6. `AppDatabase`** - Room database instance[2]

**Excluded from count** (as per assignment rules):[9]
- Data classes: `VerificationResult`, `ClaimHistoryEntity`, `SonarApiRequest`, `SonarApiResponse`
- Composable functions (UI screens)
- Extension functions and mappers
- Configuration classes: Theme setup

***

## Data Flow Architecture

### User Input Flow

1. User enters claim/query in Compose UI[1]
2. Input captured and passed to `QueryViewModel`[1][2]
3. ViewModel calls `PerplexityRepository.verifyQuery()`[5]

### API Request Flow

1. Repository formats query with domain filter parameters[11]
2. `SonarApiService` sends POST request to Perplexity API[10][11]
3. Request includes:
    - User's claim as message content
    - `search_domain_filter` array with trusted sources
    - Model specification (sonar or sonar-pro)
    - Return citations parameter set to true[11]

### Response Processing Flow

1. API returns JSON response with verification result and citations[10]
2. Repository maps response to `VerificationResult` data class[5]
3. Repository saves to local database via `ClaimHistoryDao`[2]
4. Result returned to ViewModel[3]
5. ViewModel updates UI state via StateFlow[1][2]
6. Composable observes state and displays results[1]

***

## Detailed Component Specifications

### 1. Presentation Layer

**MainActivity**
- Single Activity with Jetpack Compose[1]
- Bottom navigation for Query, History tabs
- Hosts all Composable screens
- Manages app-level UI state

**Composable Screens** (not counted as classes)
- `QueryInputScreen`: Material Design text input, submit button, loading indicator, error handling, input validation
- `ResultDisplayScreen`: Scrollable result display, formatted verification text, citation list with links, credibility indicator, new query button
- `HistoryScreen`: List of past queries, click to view full results, swipe to delete, search/filter functionality

**QueryViewModel**
- Holds UI state using StateFlow[2][1]
- States: Idle, Loading, Success, Error
- Calls repository directly for verification requests
- Transforms data to UI models
- Survives configuration changes[6]

***

### 2. Data Layer

**PerplexityRepository**
- Single source of truth pattern[5][2]
- Methods:
    - `suspend fun verifyQuery(query: String): VerificationResult`
    - `suspend fun getHistory(): List<VerificationResult>`
    - `suspend fun saveQuery(result: VerificationResult)`
    - `suspend fun deleteQuery(id: String)`
- Manages network and database operations
- Error handling and retry logic

**SonarApiService (Retrofit Interface)**
```kotlin
interface SonarApiService {
    @POST("chat/completions")
    suspend fun verifyQuery(
        @Header("Authorization") token: String,
        @Body request: SonarApiRequest
    ): SonarApiResponse
}
```

**API Request Format**[12][11]
```json
{
  "model": "sonar",
  "messages": [
    {
      "role": "system",
      "content": "You are a fact-checker. Verify claims and provide evidence-based analysis."
    },
    {
      "role": "user",
      "content": "[User's claim]"
    }
  ],
  "search_domain_filter": [
    "britannica.com",
    "wikipedia.org",
    "bbc.com/news",
    "reuters.com",
    "apnews.com"
  ],
  "return_citations": true,
  "temperature": 0.2
}
```

**ClaimHistoryDao (Room)**
```kotlin
@Dao
interface ClaimHistoryDao {
    @Query("SELECT * FROM claim_history ORDER BY timestamp DESC")
    fun getAllQueries(): Flow<List<ClaimHistoryEntity>>
    
    @Insert
    suspend fun insertQuery(query: ClaimHistoryEntity)
    
    @Delete
    suspend fun deleteQuery(query: ClaimHistoryEntity)
}
```

***

## Pre-Defined Trusted Sources

Your `search_domain_filter` configuration will include:[13][11]

**Encyclopedias & Reference**
- britannica.com
- wikipedia.org
- encyclopedia.com

**Academic & Educational**
- edu (all .edu domains)
- scholar.google.com
- jstor.org

**Fact-Checking Organizations**
- snopes.com
- factcheck.org
- politifact.com

**Quality News Sources**
- reuters.com
- apnews.com
- bbc.com/news
- npr.org

**Government & Scientific**
- gov (all .gov domains)
- who.int
- cdc.gov
- nasa.gov

You can configure these in a Constants file and easily modify them.[11]

***

## Technology Stack

**Core Android**
- Kotlin 1.9+
- Android SDK (minSdk 24, targetSdk 34)
- Jetpack Compose (UI framework)
- Material Design 3 components

**Architecture Components**[2][1]
- ViewModel
- StateFlow
- Room Database

**Networking**[2]
- Retrofit 2
- OkHttp 3
- Gson / Moshi for JSON parsing

**Asynchronous Programming**[6][2]
- Kotlin Coroutines
- Flow


***

## User Interface Design (Jetpack Compose)

### Screen 1: Query Input
- Large text input field with hint: "Enter a claim or statement to verify..."
- Character counter (500 char limit)
- Primary action button: "Verify Claim"
- Loading indicator during API call
- Material Design 3 styling

### Screen 2: Results Display
- Top section: Overall verification status (True/False/Partially True/Unverifiable)
- Color-coded indicator (green/red/yellow/gray)
- Main content: AI-generated explanation
- Citations section with source links
- Action button: New Query

### Screen 3: History
- LazyColumn list of past queries with timestamp
- Preview of result status
- Tap to view full details
- Swipe to delete functionality

***

## API Integration Details

**Authentication**[14][12]
- Store API key securely (not in version control)
- Use BuildConfig or local.properties
- Add to requests as Bearer token header

**Request Configuration**[10][11]
- Model: "sonar" (standard) or "sonar-pro" (better factuality, higher cost)
- Temperature: 0.2 (lower = more factual)
- Max tokens: 500-1000 (concise responses)
- Return citations: true

**Error Handling**
- Network errors (no connection)
- API errors (rate limit, invalid key)
- Timeout handling
- Graceful fallback messages

***

## Database Schema

**ClaimHistoryEntity Table**
```kotlin
@Entity(tableName = "claim_history")
data class ClaimHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val query: String,
    val result: String,
    val status: String, // VERIFIED, DISPUTED, UNVERIFIABLE
    val citations: String, // JSON array
    val timestamp: Long,
)
```

***



## Key Design Decisions

**Why MVVM?** Industry standard for Android, good separation of concerns, testable, works well with Architecture Components[6][1][2]

**Why Repository Pattern?** Abstracts data sources, makes testing easier, follows dependency inversion principle[5][2]

**Why Room for local storage?** Simple to implement, part of Android Jetpack, provides compile-time SQL verification[2]

**Why Retrofit?** De facto standard for Android networking, type-safe, works seamlessly with Coroutines[2]

**Why Coroutines?** Modern approach to async programming in Kotlin, cleaner than callbacks, efficient[3][6]

***

## Assignment Compliance

**Class Count**: 6 classes (within individual limit)[9]

**Originality**: All code written specifically for this assignment[9]

**External Libraries**: Clearly documented as Gradle dependencies[9]

**Documentation**: Comprehensive code comments explaining architecture decisions[9]

**Functionality**: Demonstrates API integration, local storage, UI design, and Android best practices[9]

**Testability**: Runs on Android emulator without issues[9]

***

## Next Steps

1. **Create a new Android Studio project** with Kotlin and minimum SDK 24
2. **Add dependencies** to build.gradle files
3. **Obtain Perplexity API key** from their platform[14]
4. **Define your trusted sources list** in a constants file
5. **Implement base architecture** (Activity, Composables, ViewModel)
6. **Build Repository and API service** following the specifications above
7. **Test API integration** with simple queries
8. **Implement Compose screens** and wire everything together
9. **Add database layer** for history functionality
10. **Polish and comment code** for submission[9]

This architecture provides a solid foundation for your prototype while staying within the assignment constraints and demonstrating professional Android development practices.[1][5][9][2]

[1](https://developer.android.com/topic/architecture)
[2](https://developer.android.com/topic/architecture/recommendations)
[3](https://proandroiddev.com/a-flexible-modern-android-app-architecture-complete-step-by-step-d76901e29993)
[4](https://cekrem.github.io/posts/a-use-case-for-usecases-in-kotlin/)
[5](https://cesarmauri.com/a-clean-architecture-implementation-for-android-in-kotlin/)
[6](https://stackoverflow.com/questions/61295883/common-app-state-architecture-for-java-kotlin-android-apps)
[7](https://blog.octo.com/a-responsive-and-clean-android-app-with-kotlin-actors)
[8](https://proandroiddev.com/how-to-architect-android-apps-a-deep-dive-into-principles-not-rules-2f1eb7f26402)
[9](https://ppl-ai-file-upload.s3.amazonaws.com/web/direct-files/attachments/28284446/31c10c91-2d4e-4106-88b0-99eefcc809d9/Instructions.md)
[10](https://www.perplexity.ai/hub/blog/introducing-the-sonar-pro-api)
[11](https://docs.perplexity.ai/guides/search-domain-filters)
[12](https://docs.perplexity.ai/getting-started/quickstart)
[13](https://thetradable.com/ai/perplexity-adds-domain-filters-to-search-api-ig--m)
[14](https://www.youtube.com/watch?v=o8euh5GkUzg)