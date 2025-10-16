# High-Level Architecture & Design Document

## Project Overview

**App Name**: `Truthiness` (or your preferred name)

**Purpose**: An educational fact-checking application that validates user queries against pre-defined trusted information sources using Perplexity's Sonar API.[10][11]

**Target Platform**: Android (API level 24+ recommended)

**Development Language**: Kotlin

**Architecture Pattern**: MVVM (Model-View-ViewModel) with Clean Architecture principles[2][5][1]

***

## Scope

this is not meant to be a production-grade app, just a prototype. The app would use Android's networking libraries (Retrofit or similar) to make HTTP requests to your chosen API. The API response would be parsed and displayed in a user-friendly format.

Android Integration
The Sonar API uses a simple REST interface compatible with OpenAI's format, making it straightforward to integrate into Android using standard HTTP libraries like Retrofit or OkHttp. You'll make POST requests with your user's claim as the message content and receive AI-generated, cited responses from only your trusted sources.

This approach perfectly aligns with your coursework requirements: functional prototype scope, educational focus, fact-checking capability, and leverages AI for intelligent responses rather than raw search results

## System Architecture

### Layer Structure

Your app will follow a three-layer architecture that aligns with Android best practices:[5][1][2]

**1. Presentation Layer (UI)**
- Activities and Fragments
- ViewModels
- UI State management
- User input handling

**2. Domain Layer (Business Logic)**
- Use Cases / Interactors
- Domain models
- Business rules

**3. Data Layer**
- Repository pattern
- API service interface
- Local database (Room) for history
- Data models and mappers

***

## Component Architecture

### Core Components (Within 10-Class Limit)

**1. MainActivity** - Entry point and navigation controller[9][1]

**2. QueryInputFragment** - User interface for entering claims to verify[1]

**3. ResultDisplayFragment** - Shows verification results with citations[1]

**4. HistoryFragment** - Displays previously checked claims[1]

**5. QueryViewModel** - Manages UI state and coordinates verification requests[2][1]

**6. VerifyClaimUseCase** - Orchestrates the fact-checking workflow[3][4]

**7. PerplexityRepository** - Handles all API interactions and data operations[5][2]

**8. SonarApiService** - Retrofit interface for Perplexity API calls[2]

**9. ClaimHistoryDao** - Room database interface for local storage[2]

**10. AppDatabase** - Room database instance[2]

**Excluded from count** (as per assignment rules):[9]
- Data classes: `ClaimQuery`, `VerificationResult`, `ClaimHistoryEntity`, `SonarApiRequest`, `SonarApiResponse`
- Configuration classes: Theme and dependency injection setup

***

## Data Flow Architecture

### User Input Flow

1. User enters claim/query in `QueryInputFragment`[1]
2. Input captured and passed to `QueryViewModel`[1][2]
3. ViewModel triggers `VerifyClaimUseCase`[4][3]
4. UseCase calls `PerplexityRepository.verifyQuery()`[5]

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
2. Repository maps response to domain model `VerificationResult`[5]
3. Repository saves to local database via `ClaimHistoryDao`[2]
4. Result returned to UseCase, then ViewModel[3]
5. ViewModel updates UI state LiveData/StateFlow[1][2]
6. Fragment observes state and displays results[1]

***

## Detailed Component Specifications

### 1. Presentation Layer

**MainActivity**
- Single Activity architecture with Navigation Component[1]
- Bottom navigation for Query, History tabs
- Handles fragment transactions
- Manages app-level UI state

**QueryInputFragment**
- Material Design text input field
- Submit button
- Loading indicator
- Error state handling
- Input validation (non-empty, character limits)

**ResultDisplayFragment**
- Scrollable result display
- Formatted verification text
- Citation list with source links
- Credibility indicator
- Share functionality
- Save to favorites option

**HistoryFragment**
- RecyclerView of past queries
- Click to view full results
- Swipe to delete
- Search/filter functionality

**QueryViewModel**
- Holds UI state using StateFlow[2][1]
- States: Idle, Loading, Success, Error
- Triggers use case execution
- Transforms domain data to UI models
- Survives configuration changes[6]

***

### 2. Domain Layer

**VerifyClaimUseCase**
- Single responsibility: verify one claim[4][3]
- `operator fun invoke(query: String): Flow<Result<VerificationResult>>`
- Coordinates repository calls
- Handles business logic errors
- Pure Kotlin with no Android dependencies[5]

**Domain Models**
- `ClaimQuery`: Represents user input
- `VerificationResult`: Contains verification outcome, summary, citations, confidence score
- `Citation`: Source title, URL, relevance
- `TrustedSource`: Domain configuration

***

### 3. Data Layer

**PerplexityRepository**
- Single source of truth pattern[5][2]
- Implements Repository interface defined in domain layer[5]
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
- Material Design 3 components

**Architecture Components**[2][1]
- ViewModel
- LiveData / StateFlow
- Navigation Component
- Room Database

**Networking**[2]
- Retrofit 2
- OkHttp 3
- Gson / Moshi for JSON parsing

**Dependency Injection**[3]
- Hilt (optional but recommended)

**Asynchronous Programming**[6][2]
- Kotlin Coroutines
- Flow

**Testing**[5]
- JUnit 4/5
- Mockito
- Espresso (UI tests)

***

## User Interface Design

### Screen 1: Query Input
- Large text input area with hint: "Enter a claim or statement to verify..."
- Character counter (suggest 500 char limit)
- Primary action button: "Verify Claim"
- Loading spinner during API call
- Material Design card elevation

### Screen 2: Results Display
- Top section: Overall verification status (True/False/Partially True/Unverifiable)
- Color-coded indicator (green/red/yellow/gray)
- Main content: AI-generated explanation
- Expandable citations section
- Action buttons: Share, Save, New Query

### Screen 3: History
- List of past queries with timestamp
- Preview of result status
- Tap to view full details
- Swipe actions for delete

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
    val isFavorite: Boolean = false
)
```

***

## Implementation Phases

**Phase 1: Setup & UI** (Days 1-2)
- Create project structure
- Implement basic UI layouts
- Setup navigation
- Configure dependencies

**Phase 2: API Integration** (Days 3-4)
- Implement Retrofit service
- Create repository
- Test API calls with mock data
- Handle responses

**Phase 3: Core Logic** (Days 5-6)
- Implement ViewModel
- Create use case
- Wire up data flow
- Add error handling

**Phase 4: Database & History** (Day 7)
- Setup Room database
- Implement history feature
- Add persistence

**Phase 5: Polish & Testing** (Day 8)
- UI refinements
- Add loading states
- Test edge cases
- Code comments for submission[9]

***

## Key Design Decisions

**Why MVVM?** Industry standard for Android, good separation of concerns, testable, works well with Architecture Components[6][1][2]

**Why Repository Pattern?** Abstracts data sources, makes testing easier, follows dependency inversion principle[5][2]

**Why Room for local storage?** Simple to implement, part of Android Jetpack, provides compile-time SQL verification[2]

**Why Retrofit?** De facto standard for Android networking, type-safe, works seamlessly with Coroutines[2]

**Why Coroutines?** Modern approach to async programming in Kotlin, cleaner than callbacks, efficient[3][6]

***

## Assignment Compliance

**Class Count**: 10 classes (within individual limit)[9]

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
5. **Implement base architecture** (Activity, Fragments, ViewModel)
6. **Build Repository and API service** following the specifications above
7. **Test API integration** with simple queries
8. **Implement UI components** and wire everything together
9. **Add database layer** for history functionality
10. **Polish and comment code** for submission[9]

