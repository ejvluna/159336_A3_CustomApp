# Updated Implementation Checklist
## Android Fact-Checking App with Perplexity CLI Framework Integration

Based on the Fact Checker CLI framework design and your Android app requirements, here's a logical, sequential implementation plan:[1][2][3]

***

## Phase 1: Project Setup & Configuration

### Task 1.1: Create Android Project
- Create new Android Studio project with Kotlin
- Set minSdk 24, targetSdk 34
- Configure project structure with MVVM architecture

### Task 1.2: Add Dependencies
- Add to build.gradle:
    - Retrofit 2 and OkHttp (networking)
    - Gson/Moshi (JSON parsing)
    - Room Database (local storage)
    - Kotlin Coroutines and Flow
    - ViewModel and LiveData/StateFlow
    - Material Design 3 components
    - Navigation Component

### Task 1.3: Setup API Configuration
- Obtain Perplexity API key from their platform
- Create `local.properties` entry for API key (don't commit to git)
- Create `ApiConfig.kt` for base URL and constants
- Define trusted sources list in `TrustedSources.kt`

***

## Phase 2: Study CLI Framework & API Design

### Task 2.1: Analyze CLI Request Structure
- Review Fact Checker CLI's API request format[1]
- Document the JSON structure used for:
    - System prompts for fact-checking mode
    - User message formatting
    - `search_domain_filter` parameter usage[6]
    - Model selection (sonar vs sonar-pro)
    - Temperature and token settings

### Task 2.2: Analyze CLI Response Structure
- Study how CLI parses API responses[1]
- Identify key fields: content, citations, confidence
- Map JSON response to Kotlin data classes
- Plan error handling strategies

### Task 2.3: Create Data Models
- Create `SonarApiRequest.kt` data class (based on CLI request format)[1]
- Create `SonarApiResponse.kt` data class (based on CLI response format)[1]
- Create domain models:
    - `ClaimQuery`
    - `VerificationResult`
    - `Citation`
    - `FactCheckStatus` enum

***

## Phase 3: Data Layer Implementation

### Task 3.1: Create Retrofit API Service
- Create `SonarApiService.kt` interface[4]
- Define POST endpoint matching CLI's API calls[1]
- Add authentication header configuration
- Configure timeout and retry policies

### Task 3.2: Implement Repository Pattern
- Create `PerplexityRepository.kt`[7][4]
- Implement `verifyQuery()` method that:
    - Formats user query into CLI-style request[1]
    - Adds `search_domain_filter` with trusted sources[6]
    - Calls Sonar API via Retrofit
    - Parses response into domain model
    - Handles network errors gracefully

### Task 3.3: Setup Room Database
- Create `ClaimHistoryEntity.kt`[4]
- Create `ClaimHistoryDao.kt` with CRUD operations
- Create `AppDatabase.kt` singleton
- Add type converters for complex fields (citations JSON)

***

## Phase 4: Domain Layer Implementation

### Task 4.1: Create Use Case
- Create `VerifyClaimUseCase.kt`[8][9]
- Implement invoke function that:
    - Validates user input
    - Calls repository
    - Saves results to database
    - Returns Flow<Result<VerificationResult>>

### Task 4.2: Implement Business Logic
- Add query preprocessing (trim, validate length)
- Add result post-processing (format citations, status)
- Implement caching logic if needed
- Add offline detection

***

## Phase 5: Presentation Layer - ViewModels

### Task 5.1: Create QueryViewModel
- Setup StateFlow for UI states (Idle, Loading, Success, Error)[3][4]
- Implement `verifyQuery()` function that triggers use case
- Handle coroutine scope management
- Transform domain models to UI models

### Task 5.2: Create HistoryViewModel
- Setup Flow for database queries
- Implement delete and search functionality
- Handle empty states

***

## Phase 6: UI Implementation - Layouts

### Task 6.1: Create MainActivity Layout
- Setup Navigation Component structure[3]
- Add bottom navigation or tabs
- Configure fragment container

### Task 6.2: Create Query Input UI
- Design `fragment_query_input.xml`
- Add Material TextInputLayout
- Add submit button with loading state
- Add input validation hints
- Implement character counter

### Task 6.3: Create Results Display UI
- Design `fragment_result_display.xml`
- Add status indicator (color-coded card)
- Add scrollable explanation section
- Add expandable citations RecyclerView
- Add action buttons (Share, Save, New Query)

### Task 6.4: Create History List UI
- Design `fragment_history.xml`
- Create `item_history.xml` for RecyclerView
- Add empty state view
- Add swipe-to-delete functionality

***

## Phase 7: UI Implementation - Fragment Logic

### Task 7.1: Implement QueryInputFragment
- Setup data binding / view binding[3]
- Connect EditText to ViewModel
- Observe UI state and update views
- Handle button clicks
- Show/hide loading indicators
- Display error messages

### Task 7.2: Implement ResultDisplayFragment
- Receive result data via Navigation arguments
- Display formatted verification text
- Implement citations RecyclerView adapter
- Add click listeners for citations (open URLs)
- Implement share functionality

### Task 7.3: Implement HistoryFragment
- Setup RecyclerView with adapter
- Observe history Flow from ViewModel
- Implement item click navigation
- Implement swipe-to-delete with ItemTouchHelper
- Handle empty state

***

## Phase 8: API Integration & Testing

### Task 8.1: Test API Connection
- Create simple test function in repository
- Verify API key authentication works
- Test with sample query
- Log request and response for debugging

### Task 8.2: Test Domain Filtering
- Send requests with different `search_domain_filter` values[6][1]
- Verify responses only cite allowed sources
- Adjust trusted sources list as needed
- Test with various query types

### Task 8.3: Implement Error Handling
- Handle network timeout errors
- Handle API rate limiting (429 errors)
- Handle invalid API key (401 errors)
- Handle malformed responses
- Display user-friendly error messages

---

## Phase 9: Database Integration

### Task 9.1: Implement Save Functionality
- Save successful verifications to Room database
- Include timestamp and query text
- Store full response for offline viewing

### Task 9.2: Implement History Retrieval
- Query database for all past verifications
- Sort by timestamp (newest first)
- Implement search/filter if time permits

### Task 9.3: Implement Delete Functionality
- Delete individual history items
- Optional: Add "clear all" function
- Update UI reactively via Flow

***

## Phase 10: Polish & Optimization

### Task 10.1: Improve UI/UX
- Add smooth transitions between screens
- Add proper loading animations
- Implement Material Design elevation and colors
- Ensure accessibility (content descriptions, contrast)

### Task 10.2: Add Input Validation
- Minimum/maximum character limits
- Prevent empty submissions
- Show helpful validation messages
- Disable submit during processing

### Task 10.3: Optimize Performance
- Test on emulator for lag/crashes[2]
- Ensure app releases resources when not in use[2]
- Optimize database queries
- Cache recent results if appropriate

***

## Phase 11: Documentation & Code Quality

### Task 11.1: Add Code Comments
- Document all classes with purpose and responsibility[2]
- Explain architecture decisions
- Comment complex logic
- Note which code uses external libraries vs original code[2]

### Task 11.2: Create README
- Document app functionality
- List all external libraries used[2]
- Explain how to build and run
- Include API key setup instructions

### Task 11.3: Code Review & Refactoring
- Ensure proper separation of concerns
- Remove unused code and imports
- Follow Kotlin coding conventions
- Verify class count is within limits (≤10 for individual)[2]

***

## Phase 12: Testing & Submission Prep

### Task 12.1: Functional Testing
- Test complete user flow: input → verify → view results → save → view history
- Test edge cases: empty input, very long input, network failure
- Test on Android emulator (required)[2]
- Verify app doesn't crash on configuration changes

### Task 12.2: Final Verification
- Confirm all code written specifically for this assignment[2]
- Verify no plagiarism from tutorials or repositories[2]
- Double-check API key is not hardcoded in version control
- Test clean build from scratch

### Task 12.3: Prepare Submission
- Create submission package per assignment requirements[2]
- Include all source files
- Include README with setup instructions
- Document any known limitations
- Submit before deadline (October 24, 2025)[2]

***

## Priority Order Summary

**Critical Path** (Must complete):
1. Project setup & dependencies (Phase 1)
2. Study CLI framework & create data models (Phase 2)
3. API service & repository (Phase 3.1, 3.2)
4. Basic ViewModel & UseCase (Phase 4.1, 5.1)
5. Query input UI & fragment (Phase 6.2, 7.1)
6. Results display UI & fragment (Phase 6.3, 7.2)
7. API testing & integration (Phase 8)
8. Documentation & submission (Phase 11, 12)

**Important** (Should complete):
9. Database setup & history (Phase 3.3, 9)
10. History UI (Phase 6.4, 7.3)
11. Error handling & validation (Phase 8.3, 10.2)

**Nice-to-Have** (If time permits):
12. UI polish & animations (Phase 10.1)
13. Advanced features (search history, favorites)

***

This checklist provides a complete roadmap from project initialization through submission, with each task building logically on previous work and maintaining alignment with both the Fact Checker CLI framework design and your assignment requirements.[4][3][1][2]

[1](https://docs.perplexity.ai/cookbook/examples/fact-checker-cli/README)
[2](https://ppl-ai-file-upload.s3.amazonaws.com/web/direct-files/attachments/28284446/31c10c91-2d4e-4106-88b0-99eefcc809d9/Instructions.md)
[3](https://developer.android.com/topic/architecture)
[4](https://developer.android.com/topic/architecture/recommendations)
[5](https://www.youtube.com/watch?v=o8euh5GkUzg)
[6](https://docs.perplexity.ai/guides/search-domain-filters)
[7](https://cesarmauri.com/a-clean-architecture-implementation-for-android-in-kotlin/)
[8](https://proandroiddev.com/a-flexible-modern-android-app-architecture-complete-step-by-step-d76901e29993)
[9](https://cekrem.github.io/posts/a-use-case-for-usecases-in-kotlin/)