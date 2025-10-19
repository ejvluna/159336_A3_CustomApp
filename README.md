

```markdown name=README.md

**Last Updated**: October 2025  
**Status**: In Development

# Truthiness - Fact-Checking Android App

An educational Android fact-checking application that validates user queries against pre-defined trusted information sources using Perplexity's Sonar API.

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

**Truthiness** is an Android application that helps users verify claims and statements by cross-referencing them against trusted information sources. The app leverages Perplexity's Sonar API to perform fact-checking and provides detailed verification results with citations.

### Key Features
- **Claim Verification**: Submit claims for fact-checking against trusted sources
- **Detailed Results**: Receive verification ratings (MOSTLY_TRUE, MIXED, MOSTLY_FALSE) with explanations
- **Citation Tracking**: View sources used for verification with clickable links
- **Local History**: Store and manage past verification queries
- **Material Design 3 UI**: Modern, responsive Jetpack Compose interface

---

## 🏗️ Architecture

### Architecture Pattern: MVVM + Repository Pattern

The app follows a **two-layer architecture** optimized for clarity and maintainability:

```
┌─────────────────────────────────────────┐
│     PRESENTATION LAYER (UI)             │
│  ─────────────────────────────────────  │
│  • MainActivity (Compose UI)            │
│  • Composable Screens                   │
│  • QueryViewModel (State Management)    │
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

## ✨ Features

### 1. **Claim Verification**
- Users input claims up to 500 characters
- Input validation with character counter
- Real-time UI feedback during verification

### 2. **Fact-Checking Process**
- Query processed through Perplexity's Sonar API
- Searches across pre-configured trusted domains
- Returns AI-generated analysis with sources

### 3. **Results Display**
- **Verification Rating**: Color-coded credibility indicator
  - 🟢 Green: MOSTLY_TRUE
  - 🟡 Yellow: MIXED
  - 🔴 Red: MOSTLY_FALSE
- **Summary**: Brief overview of findings
- **Detailed Explanation**: In-depth analysis
- **Citations**: Clickable source links for verification

### 4. **History Management**
- Automatic saving of verification results
- Browse past queries with timestamps
- Delete individual history entries
- Sorted by most recent first

---

## 🔧 System Components

### Core Components (6 Classes)

#### **1. MainActivity**
- Entry point for the Android application
- Hosts Jetpack Compose UI hierarchy
- Manages bottom navigation between Query and History screens
- Applies Material Design 3 theme

#### **2. QueryViewModel**
- Manages UI state using `StateFlow<UiState>`
- States: `Idle`, `Loading`, `Success`, `Error`
- Coordinates verification requests with repository
- Survives configuration changes via ViewModel scope
- Transforms API responses to UI models

#### **3. PerplexityRepository**
- Single source of truth for all data operations
- Methods:
  - `suspend fun verifyQuery(query: String): VerificationResult`
  - `suspend fun getHistory(): Flow<List<VerificationResult>>`
  - `suspend fun saveQuery(result: VerificationResult)`
  - `suspend fun deleteQuery(id: Int)`
- Error handling and data transformation
- Bridges API service and database layers

#### **4. SonarApiService**
- Retrofit interface for API communication
- POST endpoint: `/chat/completions`
- Handles Bearer token authentication
- Manages HTTP headers and request configuration

#### **5. ClaimHistoryDao**
- Room database access object (DAO)
- CRUD operations for verification history
- Returns `Flow<List<ClaimHistoryEntity>>` for reactive updates
- Queries ordered by timestamp (newest first)

#### **6. AppDatabase**
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
- `TrustedSources`: Pre-configured domain filter list
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
Perplexity Searches Trusted Sources:
├─ News: Reuters, AP News, BBC, NPR
├─ Encyclopedias: Britannica, Wikipedia
├─ Fact-Checkers: Snopes, FactCheck, PolitiFact
└─ Government/Scientific: CDC, NASA, WHO
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
Auto-save to HistoryScreen
```

### History Retrieval Flow

```
HistoryViewModel Observes History
    ↓
Repository.getHistory() → ClaimHistoryDao
    ↓
Room Query Results → Flow<List<ClaimHistoryEntity>>
    ↓
HistoryViewModel Transforms → Flow<List<VerificationResult>>
    ↓
HistoryScreenFull Observes & Renders List
```

---

## 🛠️ Technology Stack

### Core Android
- **Language**: Kotlin 1.9+
- **SDK**: Target SDK 34, Minimum SDK 24
- **UI Framework**: Jetpack Compose

### Architecture Components
- **ViewModel**: UI state management
- **StateFlow**: Reactive state holder
- **Room Database**: Local persistence

### Networking
- **Retrofit 2**: Type-safe HTTP client
- **OkHttp 3**: HTTP interceptor and configuration
- **Gson**: JSON serialization/deserialization

### Asynchronous Programming
- **Kotlin Coroutines**: Non-blocking operations
- **Flow**: Reactive data streams

### Design System
- **Material Design 3**: Modern UI components
- **Material Icons**: Icon library

---

## 🚀 Setup Instructions

### Prerequisites
- Android Studio (2023.1 or later)
- Android SDK (API 24+)
- Kotlin 1.9+
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
Create a `local.properties` file in the project root:
```properties
PERPLEXITY_API_KEY=your_api_key_here
```

**Important**: Never commit API keys to version control. Add `local.properties` to `.gitignore`.

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
│   │   ├── MainActivity.kt              # Entry point with Compose UI
│   │   ├── config/
│   │   │   └── TrustedSources.kt        # Pre-configured trusted domains
│   │   ├── data/
│   │   │   ├── api/
│   │   │   │   ├── SonarApiService.kt   # Retrofit API interface
│   │   │   │   ├── SonarApiRequest.kt   # Request data class
│   │   │   │   └── SonarApiResponse.kt  # Response data class
│   │   │   ├── database/
│   │   │   │   ├── AppDatabase.kt       # Room database instance
│   │   │   │   ├── ClaimHistoryDao.kt   # Database access object
│   │   │   │   └── ClaimHistoryEntity.kt# Database entity
│   │   │   ├── model/
│   │   │   │   ├── VerificationResult.kt# UI model
│   │   │   │   └── Rating.kt            # Enum for ratings
│   │   │   └── repository/
│   │   │       └── PerplexityRepository.kt # Data layer coordinator
│   │   ├── ui/
│   │   │   ├── QueryInputScreenFull.kt  # Query input composable
│   │   │   ├── ResultDisplayScreen.kt   # Results display composable
│   │   │   ├── HistoryScreenFull.kt     # History list composable
│   │   │   └── theme/                   # Material Design 3 theme
│   │   │       ├── Color.kt
│   │   │       ├── Type.kt
│   │   │       └── Theme.kt
│   │   └── viewmodel/
│   │       ├── QueryViewModel.kt        # Query screen state management
│   │       └── HistoryViewModel.kt      # History screen state management
│   └── res/
│       ├── values/
│       ├── xml/
│       └── mipmap/
└── build.gradle                         # Dependencies and build config
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
    "reuters.com",
    "apnews.com",
    "bbc.com",
    "snopes.com",
    "factcheck.org",
    "politifact.com",
    "cdc.gov",
    "nasa.gov",
    "who.int"
  ],
  "return_citations": true,
  "temperature": 0.2,
  "max_tokens": 1000
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

### Trusted Sources Configuration

Pre-defined domain filters in `TrustedSources.kt`:

**News Organizations**:
- reuters.com
- apnews.com
- npr.org
- bbc.com
- theguardian.com

**Encyclopedias**:
- britannica.com
- wikipedia.org

**Fact-Checkers**:
- snopes.com
- factcheck.org
- politifact.com

**Government/Scientific**:
- cdc.gov
- nasa.gov
- who.int

---

## 💾 Database Schema

### ClaimHistoryEntity Table

```kotlin
@Entity(tableName = "claim_history")
data class ClaimHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val query: String,              // User's original claim
    val result: String,             // Verification result
    val status: String,             // MOSTLY_TRUE, MIXED, MOSTLY_FALSE
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
3. Select target emulator (API 24+)

### Run on Physical Device
1. Enable Developer Mode on device
2. Connect via USB
3. Run from Android Studio or CLI

---

## 📝 External Libraries

| Library | Purpose | Version |
|---------|---------|---------|
| Retrofit 2 | Type-safe HTTP client | 2.9+ |
| OkHttp 3 | HTTP client and interceptor | 4.9+ |
| Gson | JSON serialization | 2.8+ |
| Room | Local database | 2.5+ |
| Jetpack Compose | UI framework | Latest |
| Material 3 | Design system | 1.0+ |
| Coroutines | Async programming | 1.6+ |
| ViewModel | State management | 2.6+ |
| LiveData/StateFlow | Reactive data | Latest |

---

## ⚙️ Configuration

### API Configuration
- **Temperature**: 0.2 (lower = more factual, deterministic)
- **Max Tokens**: 1000 (concise responses)
- **Model**: "sonar" (standard) or "sonar-pro" (better factuality)
- **Return Citations**: true (always enabled)

### Network Configuration
- **Connection Timeout**: 30 seconds
- **Read Timeout**: 30 seconds
- **Write Timeout**: 30 seconds
- **Retry Policy**: Enabled with exponential backoff

### Database Configuration
- **Type**: Room SQLite
- **Version**: 1
- **Export Schema**: Enabled

---

## 🐛 Error Handling

The app implements comprehensive error handling:

- **Network Errors**: "Unable to connect. Check your internet connection."
- **API Errors**: "API error. Please try again later."
- **Invalid Input**: "Please enter a valid claim (1-500 characters)."
- **Rate Limiting (429)**: "Too many requests. Please wait before trying again."
- **Authentication (401)**: "API key is invalid. Please check configuration."
- **Database Errors**: "Error saving to history. Please try again."

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
