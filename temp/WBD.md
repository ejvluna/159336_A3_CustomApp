# Work Breakdown Document

## Implementation Phases Overview

**Phase 1: Project Setup & Configuration**
- Create Android project with Kotlin
- Add Retrofit, Room, Compose, Coroutines dependencies
- Setup API configuration and trusted sources

**Phase 2: Data Models & API Design**
- Create API request/response data classes
- Create UI models and database entities
- Define trusted sources list

**Phase 3: Data Layer Implementation**
- Create Retrofit API service
- Implement Repository with CRUD operations
- Setup Room database and DAO

**Phase 4: Presentation Layer - ViewModels**
- Create QueryViewModel with StateFlow
- Create HistoryViewModel
- Handle coroutine scope management

**Phase 5: UI Implementation - Jetpack Compose**
- Create MainActivity with Compose
- Implement QueryInputScreen, ResultDisplayScreen, HistoryScreen
- Setup bottom navigation

**Phase 6: API Integration & Testing**
- Test API connection and authentication
- Test domain filtering
- Implement error handling

**Phase 7: Database Integration**
- Implement save, retrieve, delete functionality
- Wire database to UI via Flow

**Phase 8: Polish & Optimization**
- Improve UI/UX with animations and Material Design 3
- Add input validation
- Optimize performance

**Phase 9: Documentation & Code Quality**
- Add comprehensive code comments
- Create README with setup instructions
- Code review and refactoring

**Phase 10: Testing & Submission Prep**
- Functional testing of complete user flow
- Final verification and clean build
- Prepare submission package

***

## ✅ Phase 1: Project Setup & Configuration

### ✅ Task 1.1: Create Android Project
- [x] Create new Android Studio project with Kotlin
- [x] Set minSdk 24, targetSdk 34
- [x] Configure project structure with MVVM architecture

### ✅ Task 1.2: Add Dependencies
- [x] Add to build.gradle:
  - [x] Retrofit 2 and OkHttp (networking)
  - [x] Gson (JSON parsing)
  - [x] Room Database (local storage)
  - [x] Kotlin Coroutines and Flow
  - [ ] ViewModel and StateFlow
  - [ ] Jetpack Compose and Material Design 3
  - [ ] Compose Navigation

### ✅ Task 1.3: Setup API Configuration
- [x] Obtain Perplexity API key from their platform
- [x] Create `secrets.properties` entry for API key (don't commit to git)
- [x] Create `ApiConfig.kt` for base URL and constants

## ✅ Phase 2: Data Models & API Design 

### Task 2.1: Create Data Models
- [x] Create `SonarApiRequest.kt` data class for API requests
- [x] Create `SonarApiResponse.kt` data class for API responses
- [x] Create UI models:
  - [x] `VerificationResult` (claim, rating, summary, explanation, citations)
  - [x] `Rating` enum (MOSTLY_TRUE, MIXED, MOSTLY_FALSE)
- [x] Create database entity:
  - [x] `ClaimHistoryEntity` (id, query, result, status, citations, timestamp)

### Task 2.2: Define Trusted Sources
- [x] Create `TrustedSources.kt` with domain filter list
- [x] Include: encyclopedias, academic, fact-checking orgs, news, government/scientific

***

## ✅ Phase 3: Data Layer Implementation

### ✅ Task 3.1: Create Retrofit API Service 
- ✅ Create `SonarApiService.kt` interface[4]
- ✅ Define POST endpoint: `chat/completions`
- ✅ Add authentication header configuration (Bearer token)
- ✅ Configure timeout and retry policies (30s timeouts, retry enabled)
- ✅ Created `RetrofitClient.kt` singleton with OkHttpClient configuration

### ✅ Task 3.2: Setup Room Database 
- ✅ Create `ClaimHistoryDao.kt` with CRUD operations (insert, query, delete)
- ✅ Create `AppDatabase.kt` singleton with thread-safe initialization
- ✅ Configured for JSON fields (citations stored as JSON string)

***

## ✅ Phase 4: Presentation Layer - ViewModels

### ✅ Task 4.1: Create QueryViewModel
- ✅ Setup StateFlow for UI states (Idle, Loading, Success, Error)[3][4]
- ✅ Implement `verifyQuery(query: String)` function
- ✅ Call repository directly (no use case)
- ✅ Handle coroutine scope management with viewModelScope
- ✅ Transform API response to UI models
- ✅ Added `resetState()` for state management
- ✅ Input validation (non-empty check)
- ✅ Auto-save verified queries to database

### ✅ Task 4.2: Create HistoryViewModel
- ✅ Setup StateFlow for database queries (using stateIn())
- ✅ Implement delete functionality with error handling
- ✅ Handle empty states (initialValue: emptyList())
- ✅ Reactive updates via SharingStarted.Lazily

***

## ✅ Phase 5: UI Implementation - Jetpack Compose

### ✅ Task 5.1: Create MainActivity with Compose
- ✅ Setup Compose in MainActivity
- ✅ Implement bottom navigation with Compose
- ✅ Setup navigation between screens

### ✅ Task 5.2: Create QueryInputScreen Composable
- ✅ Material Design text input field
- ✅ Character counter (500 char limit)
- ✅ Submit button with loading state
- ✅ Error message display
- ✅ Input validation

### ✅ Task 5.3: Create ResultDisplayScreen Composable
- ✅ Status indicator (color-coded)
- ✅ Scrollable result display
- ✅ Summary and explanation text
- ✅ Citations list with clickable URLs
- ✅ New Query button

### ✅ Task 5.4: Create HistoryScreen Composable
- ✅ LazyColumn list of past queries
- ✅ Timestamp display
- ✅ Click to view full results
- ✅ Swipe to delete functionality
- ✅ Empty state handling

***

## ✅  Phase 6: API Integration & Testing 

### ✅  Task 6.1: Test API Connection
- ✅ Create test function in repository
- ✅ Verify API key authentication
- ✅ Test with sample query
- ✅ Log request/response for debugging

### ✅ Task 6.2: Test Domain Filtering
- ✅ Verify `search_domain_filter` parameter works
- ✅ Confirm responses cite only trusted sources
- ✅ Adjust sources list if needed

### ✅ Task 6.3: Implement Error Handling
- ✅ Handle network timeouts
- ✅ Handle API rate limiting (429 errors)
- ✅ Handle invalid API key (401 errors)
- ✅ Handle malformed responses
- ✅ Display user-friendly error messages in UI

### Implementation Details:
- Added comprehensive error handling in `PerplexityRepository.kt`
- Implemented structured JSON responses with Sonar API
- Added domain filtering using `DEFAULT_SEARCH_DOMAIN_FILTER`
- Created user-friendly error messages for all failure cases
- Added detailed logging for debugging

***

## ✅ Phase 7: Database Integration 

### Task 7.1: Implement Save Functionality ✅
- Save successful verifications to Room database
- Include timestamp and query text
- Store full response

### Task 7.2: Implement History Retrieval ✅
- Query database for all past verifications
- Sort by timestamp (newest first)

### Task 7.3: Implement Delete Functionality ✅
- Delete individual history items
- Update UI reactively via Flow

***

## Phase 8: Polish & Optimization

### Task 8.1: Improve UI/UX
- Add smooth transitions between Compose screens
- Add proper loading animations
- Implement Material Design 3 colors and styling
- Ensure accessibility (content descriptions, contrast)

### Task 8.2: Add Input Validation
- Enforce 500 character limit
- Prevent empty submissions
- Show helpful validation messages
- Disable submit during processing

### Task 8.3: Optimize Performance
- Test on emulator for lag/crashes[2]
- Ensure app releases resources when not in use[2]
- Optimize database queries

***

## Phase 9: Documentation & Code Quality

### Task 9.1: Add Code Comments
- Document all classes with purpose and responsibility[2]
- Explain architecture decisions
- Comment complex logic
- Note which code uses external libraries vs original code[2]

### Task 9.2: Create README
- Document app functionality
- List all external libraries used[2]
- Explain how to build and run
- Include API key setup instructions

### Task 9.3: Code Review & Refactoring
- Ensure proper separation of concerns
- Remove unused code and imports
- Follow Kotlin coding conventions
- Verify class count is within limits (≤6 for individual)[2]

***

## Phase 10: Testing & Submission Prep

### Task 10.1: Functional Testing
- Test complete user flow: input → verify → view results → save → view history
- Test edge cases: empty input, very long input, network failure
- Test on Android emulator (required)[2]
- Verify app doesn't crash on configuration changes

### Task 10.2: Final Verification
- Confirm all code written specifically for this assignment[2]
- Verify no plagiarism from tutorials or repositories[2]
- Double-check API key is not hardcoded in version control
- Test clean build from scratch

### Task 10.3: Prepare Submission
- Create submission package per assignment requirements[2]
- Include all source files
- Include README with setup instructions
- Document any known limitations
- Submit before deadline (October 24, 2025)[2]

***

## Priority Order Summary

**Critical Path** (Must complete):
1. Project setup & dependencies (Phase 1)
2. Create data models (Phase 2)
3. API service & repository (Phase 3.1, 3.2)
4. QueryViewModel (Phase 4.1)
5. Query input & results screens (Phase 5.2, 5.3)
6. API testing & integration (Phase 6)
7. Documentation & submission (Phase 9, 10)

**Important** (Should complete):
8. Database setup & history (Phase 3.3, 7)
9. History screen (Phase 5.4)
10. Error handling & validation (Phase 6.3, 8.2)

**Nice-to-Have** (If time permits):
11. UI polish & animations (Phase 8.1)
12. Advanced search/filtering

***

This checklist provides a complete roadmap from project initialization through submission, with each task building logically on previous work and maintaining alignment with the simplified 2-layer Jetpack Compose architecture and assignment requirements.[4][3][1][2]

[1](https://docs.perplexity.ai/cookbook/examples/fact-checker-cli/README)
[2](https://ppl-ai-file-upload.s3.amazonaws.com/web/direct-files/attachments/28284446/31c10c91-2d4e-4106-88b0-99eefcc809d9/Instructions.md)
[3](https://developer.android.com/topic/architecture)
[4](https://developer.android.com/topic/architecture/recommendations)
[6](https://docs.perplexity.ai/guides/search-domain-filters)
[7](https://cesarmauri.com/a-clean-architecture-implementation-for-android-in-kotlin/)
[8](https://proandroiddev.com/a-flexible-modern-android-app-architecture-complete-step-by-step-d76901e29993)
[9](https://cekrem.github.io/posts/a-use-case-for-usecases-in-kotlin/)