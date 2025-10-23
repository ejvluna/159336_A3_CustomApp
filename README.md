**Last Updated**: 23 October 2025

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Features](#features)
- [System Components](#system-components)
- [Data Flow](#data-flow)
- [Technology Stack](#technology-stack)
- [Setup Instructions](#setup-instructions)
- [Project Structure](#project-structure)
- [API Integration](#api-integration)
- [Database Schema](#database-schema)
- [Building & Running](#building--running)

---

## 🎯 Overview

**<Verifica** is an Android application that helps users verify claims and statements by cross-referencing them against trusted information sources. The app leverages Perplexity's Sonar API to perform fact-checking and provides detailed verification results with citations.

### ✨ Key Features 

- **Intelligent Claim Verification**: Submit claims up to 500 characters for AI-powered fact-checking via Perplexity's Sonar API. The API searches across trusted sources (Reuters, BBC, Wikipedia, Snopes, FactCheck, CDC, NASA, WHO, and more) to provide evidence-based analysis.

- **Input Validation & Real-Time Feedback**: 
  - Character counter with 500-character limit enforcement
  - Empty submission prevention
  - Real-time error messages for invalid input
  - Disabled submit button during verification

- **Color-Coded Verification Results**: 
  - 🟢 **TRUE** (Green): Claim is verified as accurate
  - 🟡 **MISLEADING** (Yellow): Claim contains partially true information
  - 🔴 **FALSE** (Red): Claim is inaccurate or false
  - ⚪ **UNABLE TO VERIFY** (Grey): Insufficient information available

- **Comprehensive Result Display**: Each verification includes:
  - Verification rating with visual indicator
  - Brief summary of findings
  - Detailed explanation with evidence-based analysis
  - Clickable citations linking to source materials

- **Local History Management**: 
  - Automatic saving of all verification queries
  - Browse past queries with timestamps (newest first)
  - Delete individual history entries
  - Persistent storage on device

- **Modern UI/UX with Material Design 3**: 
  - Smooth screen transitions and animations
  - Loading indicators during verification
  - Responsive layout for all screen sizes
  - Intuitive bottom navigation between Query and History screens

---

## 🏗️ Architecture

### Architecture Pattern: MVVM + Repository Pattern

The app is designed for clean functionality and maintainable approach that follows a **two-layer architecture** :

```
┌─────────────────────────────────────────┐
│     PRESENTATION LAYER (UI)             │
│  ─────────────────────────────────────  │
│  • MainActivity (Compose UI)            │
│  • Composable Screens                   │
│  • QueryViewModel (State Management)    │
│  • HistoryViewModel (State Management)  │
└─────────────────────────────────────────┘
              ↕ (Data binding)
┌─────────────────────────────────────────┐
│       DATA LAYER (Business Logic)       │
│  ─────────────────────────────────────  │
│  • PerplexityRepository                 │
│  • SonarApiService (Retrofit)           │
│  • ClaimHistoryDao (Room Database)      │
│  • AppDatabase                          │
└─────────────────────────────────────────┘
              ↕ (Data operations)
┌─────────────────────────────────────────┐
│      EXTERNAL DATA SOURCES              │
│  ─────────────────────────────────────  │
│  • Perplexity Sonar API                 │
│  • Local Room Database                  │
└─────────────────────────────────────────┘
```

---

## 🔧 System Components

### Core Components (7 Classes)

#### **1. MainActivity**
- Entry point for the Android application
- Hosts Jetpack Compose UI hierarchy
- Manages bottom navigation between Query and History screens
- Applies Material Design 3 theme

#### **2. QueryViewModel**
- Manages query input screen state using `StateFlow<UiState>`
- States: `Idle`, `Loading`, `Success`, `Error`
- Coordinates verification requests with repository
- Survives configuration changes via ViewModel scope
- Transforms API responses to UI models

#### **3. HistoryViewModel**
- Manages history screen state and data
- Exposes `historyFlow: StateFlow<List<VerificationResult>>`
- Handles delete operations for history entries
- Provides reactive updates when database changes
- Survives configuration changes via ViewModel scope

#### **4. PerplexityRepository**
- Single source of truth for all data operations
- Methods:
  - `suspend fun verifyQuery(query: String): VerificationResult`
  - `suspend fun getHistory(): Flow<List<VerificationResult>>`
  - `suspend fun saveQuery(result: VerificationResult)`
  - `suspend fun deleteQuery(id: Int)`
- Error handling and data transformation
- Bridges API service and database layers

#### **5. SonarApiService**
- Retrofit interface for API communication
- POST endpoint: `/chat/completions`
- Handles Bearer token authentication
- Manages HTTP headers and request configuration

#### **6. ClaimHistoryDao**
- Room database access object (DAO)
- CRUD operations for verification history
- Returns `Flow<List<ClaimHistoryEntity>>` for reactive updates
- Queries ordered by timestamp (newest first)

#### **7. AppDatabase**
- Room database singleton instance
- Provides DAO access
- Thread-safe initialization with double-checked locking
- Manages database creation and versioning

### Supporting Classes (Not Counted)

**Data Classes:**
- `VerificationResult`: UI representation of verification response
- `SonarApiRequest`: API request payload
- `SonarApiResponse`: API response payload
- `ClaimHistoryEntity`: Database entity
- `Rating` enum: Verification status types

**Composable Functions:**
- `QueryInputScreenFull()`: Input and submission UI
- `ResultDisplayScreen()`: Results and citations display
- `HistoryScreenFull()`: History list management
- `HistoryItem()`: Individual history entry
- `StatusIndicator()`: Rating visualization
- `EmptyHistoryState()`: Empty state UI

**Configuration Classes:**
- `TrustedSources.kt`: Pre-configured domain filter list for API search constraints.
- Theme files: Material Design 3 styling

---

## 🔄 Data Flow

### User Input Flow

```
User Input
    ↓
QueryInputScreen (Composable)
    ↓
QueryViewModel.verifyQuery(query)
    ↓
PerplexityRepository.verifyQuery(query)
    ↓
SonarApiService.verifyClaim(request)
    ↓
Perplexity Sonar API (Network Request)
```

### API Processing Flow

```
API Request with Domain Filters
    ↓
Perplexity Searches Trusted Sources (18 domains):
├─ News: Reuters, AP News, NPR, BBC, The Guardian
├─ Encyclopedias: Britannica, Stanford, Scholarpedia, Encyclopedia.com
├─ Fact-Checkers: Snopes, FactCheck.org, PolitiFact
└─ Government/Scientific: CDC, NASA, WHO, NIH, Nature, Science, ScienceDirect, JSTOR
    ↓
API Returns JSON Response
├─ Claim verification status
├─ Detailed explanation
└─ Source citations with URLs
```

### Response Processing Flow

```
SonarApiResponse (JSON)
    ↓
Repository Maps → VerificationResult (UI Model)
    ↓
Repository Saves → ClaimHistoryEntity (Database)
    ↓
QueryViewModel Updates → StateFlow<UiState>
    ↓
ResultDisplayScreen Observes & Renders UI
    ↓
Database Change Triggers HistoryViewModel Update
```

### History Retrieval Flow

```
HistoryViewModel Observes History
    ↓
Repository.getHistory() → ClaimHistoryDao
    ↓
Room Query Results → Flow<List<ClaimHistoryEntity>>
    ↓
Repository Maps → Flow<List<VerificationResult>>
    ↓
HistoryScreenFull Observes & Renders List
```

---

## 🛠️ Technology Stack

### Core Android
- **Language**: Kotlin 2.0.0
- **SDK**: Target SDK 34, Minimum SDK 26
- **UI Framework**: Jetpack Compose

### Architecture Components
- **ViewModel**: UI state management
- **StateFlow**: Reactive state holder
- **Room Database**: Local persistence

### Networking
- **Retrofit 2** (2.11.0): Type-safe HTTP client
- **OkHttp 3** (4.12.0): HTTP interceptor and configuration
- **Gson** (2.10.1): JSON serialization/deserialization

### Asynchronous Programming
- **Kotlin Coroutines**: Non-blocking operations
- **Flow**: Reactive data streams

### Design System
- **Material Design 3**: Modern UI components
- **Material Icons**: Icon library

### Architectural Patterns & Design Decisions

#### Singleton Pattern (Database)
The `AppDatabase` class implements the Singleton pattern to ensure only one database instance exists throughout the app's lifetime. This prevents resource conflicts, data inconsistencies, and redundant database connections. The pattern uses `@Volatile` annotation for thread-safe lazy initialization and `synchronized` blocks to prevent race conditions in multi-threaded scenarios.

#### Repository Pattern
The `PerplexityRepository` class acts as a single source of truth for data operations, abstracting both API calls and database operations. This decouples the UI layer from data sources, making the code more testable and maintainable. The repository handles error handling, data transformation, and provides reactive data streams via Flow.

#### MVVM Architecture
The app follows the Model-View-ViewModel (MVVM) pattern with clear separation of concerns:
- **Model**: Data classes (`VerificationResult`, `ClaimHistoryEntity`) and database entities
- **View**: Jetpack Compose UI components
- **ViewModel**: `QueryViewModel` and `HistoryViewModel` manage UI state and business logic, exposed via `StateFlow` for reactive updates

#### Reactive Data Streams
Using `Flow` and `StateFlow` enables reactive, non-blocking data updates. The database queries return `Flow<List<>>` allowing the UI to automatically update whenever data changes, without manual polling or callbacks.

#### Error Handling Strategy
Comprehensive try-catch blocks with specific exception handling for HTTP errors, network timeouts, connection failures, and JSON parsing errors. Each error type returns user-friendly messages and appropriate fallback states, ensuring graceful degradation without app crashes.

#### Resource Management Strategy
The app implements MVP-level resource management to prevent memory leaks and optimize resource usage:
- **Coroutine Cleanup**: `DisposableEffect` in `CustomApp()` cancels pending coroutines when the composable is disposed, preventing orphaned API calls
- **Singleton Pattern**: `RetrofitClient` and `AppDatabase` use lazy initialization to create single instances, enabling connection pooling and preventing resource duplication
- **Compose Framework Cleanup**: `rememberCoroutineScope()` and `collectAsState()` are automatically cancelled/unsubscribed when composables leave composition
- **Stateless Repository**: `PerplexityRepository` holds no resources; API calls are managed by the calling coroutine scope and database operations by Room

---

## 🚀 Setup Instructions

### Prerequisites
- Android Studio (2024.1 or later)
- Android SDK (API 26+)
- Kotlin 2.0.0
- Perplexity API Key

### Step 1: Clone the Repository
```bash
git clone https://github.com/ejvluna/159336_A3_CustomApp.git
cd 159336_A3_CustomApp
```

### Step 2: Obtain Perplexity API Key
1. Visit [Perplexity API Platform](https://www.perplexity.ai/)
2. Sign up or log in to your account
3. Generate an API key from the dashboard
4. Store the key securely

### Step 3: Configure API Key
Create a `secrets.properties` file in the project root:
```properties
PERPLEXITY_API_KEY=your_api_key_here
```

**Important**: Never commit API keys to version control. Add `secrets.properties` to `.gitignore`. The app uses the Secrets Gradle Plugin to securely manage API keys.

### Step 4: Build the Project
```bash
./gradlew build
```

### Step 5: Run on Emulator
```bash
./gradlew installDebug
# Or use Android Studio: Run → Run 'app'
```

---

## 📁 Project Structure

```
app/
├── src/main/
│   ├── java/com/example/customapp/
│   │   ├── MainActivity.kt              # Entry point with Compose UI, screen navigation, and state management
│   │   ├── config/
│   │   │   ├── ApiConfig.kt             # API configuration (base URL, endpoints, model settings)
│   │   │   └── TrustedSources.kt        # Pre-configured trusted domains for search filtering
│   │   ├── data/
│   │   │   ├── PerplexityRepository.kt  # Data layer coordinator (API + Database operations)
│   │   │   ├── api/
│   │   │   │   ├── RetrofitClient.kt    # Retrofit singleton with OkHttp configuration
│   │   │   │   ├── SonarApiService.kt   # Retrofit API interface for Perplexity Sonar API
│   │   │   │   ├── SonarApiRequest.kt   # Request data class for API calls
│   │   │   │   └── SonarApiResponse.kt  # Response data class from API
│   │   │   ├── database/
│   │   │   │   ├── AppDatabase.kt       # Room database singleton instance
│   │   │   │   ├── ClaimHistoryDao.kt   # DAO for CRUD operations on history
│   │   │   │   └── ClaimHistoryEntity.kt# Database entity for claim history
│   │   │   └── model/
│   │   │       └── VerificationResult.kt# UI model with nested Rating enum
│   │   ├── ui/
│   │   │   ├── QueryInputScreen.kt      # Query input composable with validation
│   │   │   ├── ResultDisplayScreen.kt   # Results display composable with citations
│   │   │   ├── HistoryScreen.kt         # History list composable with delete functionality
│   │   │   ├── QueryViewModel.kt        # Query screen state management (StateFlow)
│   │   │   ├── HistoryViewModel.kt      # History screen state management (StateFlow)
│   │   │   └── theme/
│   │   │       ├── Color.kt             # Material Design 3 color definitions
│   │   │       ├── Type.kt              # Material Design 3 typography
│   │   │       └── Theme.kt             # App theme with dynamic color support
│   │   └── res/
│   │       ├── values/
│   │       ├── xml/
│   │       └── mipmap/
│   └── build.gradle.kts                 # Dependencies and build configuration
└── README.md                             # Project documentation
```

---

## 🌐 API Integration

### Perplexity Sonar API

**Endpoint**: `POST https://api.perplexity.ai/chat/completions`

**Authentication**: Bearer Token (API Key)

**Request Format**:
```json
{
  "model": "sonar",
  "messages": [
    {
      "role": "user",
      "content": "Fact-check the following claim and provide a rating with these definitions:\n- TRUE: The claim is fully supported by credible evidence\n- FALSE: The claim is contradicted by credible evidence\n- MISLEADING: The claim contains some truth but is presented deceptively or lacks important context\n- UNABLE_TO_VERIFY: There is insufficient evidence to verify the claim\n\nProvide a concise summary and detailed explanation with citations from trusted sources.\n\nClaim: [User's claim]"
    }
  ],
  "search_domain_filter": [
    "reuters.com",
    "apnews.com",
    "npr.org",
    "bbc.com",
    "theguardian.com",
    "britannica.com",
    "plato.stanford.edu",
    "scholarpedia.org",
    "encyclopedia.com",
    "snopes.com",
    "factcheck.org",
    "politifact.com",
    "cdc.gov",
    "nasa.gov",
    "who.int",
    "nih.gov",
    "nature.com",
    "sciencemag.org",
    "sciencedirect.com",
    "jstor.org"
  ],
  "response_format": {
    "type": "json_schema",
    "json_schema": {
      "name": "FactCheckResult",
      "schema": {
        "type": "object",
        "properties": {
          "rating": {
            "type": "string",
            "enum": ["TRUE", "FALSE", "MISLEADING", "UNABLE_TO_VERIFY"]
          },
          "summary": {
            "type": "string"
          },
          "explanation": {
            "type": "string"
          }
        },
        "required": ["rating", "summary", "explanation"]
      }
    }
  },
  "temperature": 0.2,
  "max_tokens": 1000,
  "max_tokens_per_page": 512,
  "max_results": 5,
  "num_sources": 3
}
```

**Response Format**:
```json
{
  "id": "...",
  "model": "sonar",
  "created": 1234567890,
  "usage": { "prompt_tokens": 42, "completion_tokens": 100 },
  "choices": [
    {
      "index": 0,
      "finish_reason": "stop",
      "message": {
        "role": "assistant",
        "content": "[Fact-check result]"
      },
      "citations": ["https://example.com", "https://source.com"]
    }
  ]
}
```


---

## 💾 Database Schema

### ClaimHistoryEntity Table

```kotlin
@Entity(tableName = "claim_history")
data class ClaimHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val query: String,              // User's original claim
    val result: String,             // Verification result
    val status: String,             // TRUE, FALSE, MISLEADING, UNABLE_TO_VERIFY
    val citations: String,          // JSON array of source URLs
    val timestamp: Long,            // Query timestamp in milliseconds
    val summary: String,            // Brief summary
    val explanation: String         // Detailed explanation
)
```

**Indexes**:
- Primary key on `id` for efficient lookups
- Ordering by `timestamp DESC` for newest-first display

---

## 🏃 Building & Running

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

### Run Tests
```bash
./gradlew test
```

### Run on Android Emulator
1. Open Android Studio
2. Click "Run" or press `Shift + F10`
3. Select target emulator (API 26+)

### Run on Physical Device
1. Enable Developer Mode on device
2. Connect via USB
3. Run from Android Studio or CLI

---

## 📝 External Libraries

| Library | Purpose | Version |
|---------|---------|---------|
| Kotlin | Programming language | 2.0.0 |
| Retrofit 2 | Type-safe HTTP client | 2.11.0 |
| OkHttp 3 | HTTP client and interceptor | 4.12.0 |
| Gson | JSON serialization | 2.10.1 |
| Room | Local database | 2.6.1 |
| Jetpack Compose | UI framework | 2024.06.00 |
| Material 3 | Design system | Latest (from BOM) |
| Kotlin Coroutines | Async programming | 1.8.0 |
| ViewModel | State management | 2.8.4 |
| StateFlow | Reactive data | Latest (from BOM) |
| KSP | Kotlin Symbol Processing | 2.0.0-1.0.22 |
| Secrets Gradle Plugin | API key management | Latest |
| JUnit | Unit testing | 4.13.2 |
| Mockito | Mocking framework | 5.7.0 |
| Espresso | UI testing | 3.5.1 |

---

## ⚙️ Configuration

### API Configuration
- **Temperature**: 0.2 (lower = more factual, deterministic)
- **Max Tokens**: 1000 (concise responses)
- **Max Tokens Per Page**: 512 (content extraction limit per webpage)
- **Max Results**: 5 (maximum search results to process)
- **Num Sources**: 3 (maximum citations to return)
- **Model**: "sonar" (standard) or "sonar-pro" (better factuality)
- **Return Citations**: true (always enabled)

### Network Configuration
- **Connection Timeout**: 30 seconds
- **Read Timeout**: 30 seconds
- **Write Timeout**: 30 seconds
- **Retry Policy**: Enabled for connection failures

### Database Configuration
- **Type**: Room SQLite
- **Version**: 1
- **Export Schema**: Enabled

---

## 🐛 Error Handling

The app implements comprehensive error handling with specific messages for each error type:

- **Connection Error**: "Failed to connect to the API. Please check your internet connection."
- **Network Timeout**: "The request took too long. Please check your internet connection and try again."
- **Authentication (401)**: "Invalid API Key: Authentication failed. Please check your API key configuration."
- **Rate Limiting (429)**: "Rate Limited: Too many requests. Please wait a moment and try again."
- **Server Error (500, 502, 503)**: "Server Error: The API service is temporarily unavailable. Please try again later."
- **Invalid Response Format**: "The API returned an unexpected response format. Please try again."
- **Invalid Input**: "Please enter a valid claim (1-500 characters)."
- **Unexpected Errors**: "An unexpected error occurred: [error details]"

---

## 📚 Learning Resources

- [Android Architecture Guide](https://developer.android.com/topic/architecture)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Room Database Guide](https://developer.android.com/training/data-storage/room)
- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Perplexity API Docs](https://docs.perplexity.ai/)

---

## 📄 License

This project is for educational purposes as part of assignment A3.

---

## 👤 Author

**E.J. Luna**  
Repository: [159336_A3_CustomApp](https://github.com/ejvluna/159336_A3_CustomApp)

---

## 🔗 Links

- [Perplexity AI](https://www.perplexity.ai/)
- [Perplexity API Documentation](https://docs.perplexity.ai/)
- [Android Developer Documentation](https://developer.android.com/)
- [Jetpack Compose UI Toolkit](https://developer.android.com/jetpack/compose)

---


```
