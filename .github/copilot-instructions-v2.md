# copilot-instructions.md

## Introduction

This document serves as a comprehensive guide for senior Android developers to standardize best
practices while leveraging AI agents (GitHub Copilot, Android Studio AI, Gemini Code Assist, etc.)
for code generation, refactoring, debugging, architecture enforcement, and code reviews.

The primary goal is to ensure scalability, testability, maintainability, and performance for
enterprise-level Android applications using Kotlin and modern Android development practices. AI
agents can accelerate development by generating boilerplate code, suggesting refactors, validating
architecture patterns, and performing automated code reviews while maintaining high code quality
standards.

This guide is designed for teams building production-ready applications that serve millions of
users, with emphasis on clean architecture, modular design, and sustainable development practices.

## Core Principles

### Single Responsibility Principle (SRP)

Each class should have one reason to change. Use AI to identify violations and suggest refactoring.

**AI Prompt**: "Review this class for SRP violations and suggest how to split responsibilities"

### DRY (Don't Repeat Yourself)

Eliminate code duplication through abstraction and reusable components.

**AI Prompt**: "Identify duplicate code patterns and suggest a common abstraction"

### KISS (Keep It Simple, Stupid)

Favor simple, readable solutions over complex ones.

**AI Prompt**: "Simplify this function while maintaining functionality"

### SOLID Principles

Apply all SOLID principles consistently across the codebase.

**AI Prompt**: "Analyze this module for SOLID principle violations"

### Composition over Inheritance

Prefer composition patterns to deep inheritance hierarchies.

**AI Prompt**: "Refactor this inheritance structure to use composition"

## Best Practices by Field

### 1. Language - Kotlin

**Description**: Kotlin is the official language for Android development, offering null safety,
coroutines, extension functions, and concise syntax.

**Best Practices**:

- Use Kotlin's null safety features (`?`, `?.`, `?:`, `!!` sparingly)
- Leverage data classes for model objects
- Utilize sealed classes for state representation
- Apply scope functions (`let`, `run`, `with`, `apply`, `also`) appropriately
- Prefer immutable collections and `val` over `var`

```kotlin
// Good: Using Kotlin idioms
data class User(val id: String, val name: String)
sealed class UiState {
    object Loading : UiState()
    data class Success(val data: List<User>) : UiState()
    data class Error(val message: String) : UiState()
}
```

**AI Agent Integration**:

- "Convert this Java code to idiomatic Kotlin with null safety"
- "Generate a sealed class hierarchy for handling API responses"
- "Review this Kotlin code for language-specific improvements"

### 2. Asynchronous Logic - Coroutines and Flows

**Description**: Coroutines provide structured concurrency for async operations, while Flows enable
reactive programming patterns.

**Best Practices**:

- Use `viewModelScope` for ViewModel operations
- Apply structured concurrency with proper scope management
- Distinguish between cold flows (`flow {}`) and hot flows (`StateFlow`, `SharedFlow`)
- Handle cancellation properly with `ensureActive()`
- Use appropriate dispatchers (`IO`, `Main`, `Default`)

```kotlin
class UserViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun loadUsers() {
        viewModelScope.launch {
            repository.getUsers()
                .flowOn(Dispatchers.IO)
                .catch { _uiState.value = UiState.Error(it.message) }
                .collect { _uiState.value = UiState.Success(it) }
        }
    }
}
```

**AI Agent Integration**:

- "Convert this callback-based code to coroutines with proper error handling"
- "Generate a Flow-based repository pattern with retry logic"
- "Add proper coroutine cancellation and exception handling to this function"

### 3. Clean Architecture - Data / Domain / Presentation

**Description**: Separation of concerns through layered architecture ensures testability and
maintainability.

**Best Practices**:

- **Data Layer**: Repositories, data sources (remote/local), DTOs
- **Domain Layer**: Use cases, business logic, domain models
- **Presentation Layer**: ViewModels, UI state, Compose UI
- Maintain unidirectional data flow
- Keep layers independent with clear interfaces

```kotlin
// Domain Layer
class GetUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<User> =
        repository.getUser(userId)
}

// Data Layer
class UserRepositoryImpl @Inject constructor(
    private val api: UserApi,
    private val dao: UserDao
) : UserRepository {
    override suspend fun getUser(userId: String): Result<User> =
    // Implementation
}
```

**AI Agent Integration**:

- "Generate a complete clean architecture structure for a user feature"
- "Review this code for clean architecture violations and suggest fixes"
- "Create use case classes for these business requirements"

### 4. Clean Code and Modularization

**Description**: Modular design improves build times, enables feature teams, and enforces
boundaries.

**Best Practices**:

- Create feature modules (`:feature:login`, `:feature:profile`)
- Extract common code to core modules (`:core:ui`, `:core:network`)
- Use convention plugins for shared Gradle configuration
- Keep module APIs minimal and explicit
- Apply clean code principles (meaningful names, small functions)

```kotlin
// Module structure
:app
:feature:authentication
:feature:profile
:core:ui
:core:network
:core:database
```

**AI Agent Integration**:

- "Suggest a module structure for this monolithic app"
- "Generate a Gradle convention plugin for common Android module setup"
- "Identify code that should be extracted to a shared module"

### 5. Networking - Retrofit and OkHttp

**Description**: Retrofit provides type-safe HTTP client with OkHttp as the underlying network
layer.

**Best Practices**:

- Define API interfaces with suspend functions
- Use interceptors for authentication, logging, and caching
- Implement proper error handling with sealed classes
- Configure timeouts appropriately
- Use Moshi or Kotlinx.serialization for JSON parsing

```kotlin
interface UserApi {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: String): UserDto
}

@Provides
fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(AuthInterceptor())
    .addInterceptor(HttpLoggingInterceptor())
    .connectTimeout(30, TimeUnit.SECONDS)
    .build()
```

**AI Agent Integration**:

- "Generate a Retrofit service with proper error handling and interceptors"
- "Create a network module with Hilt injection for this API specification"
- "Add retry logic with exponential backoff to this network call"

### 6. Views - Jetpack Compose

**Description**: Modern declarative UI toolkit for building native Android interfaces.

**Best Practices**:

- Keep composables small and focused
- Use `remember` and `rememberSaveable` appropriately
- Apply `Modifier` parameters for flexibility
- Optimize with `@Stable` and `@Immutable` annotations
- Preview composables with `@Preview`
- Handle configuration changes properly

```kotlin
@Composable
fun UserCard(
    user: User,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        // Content
    }
}
```

**AI Agent Integration**:

- "Convert this XML layout to Jetpack Compose"
- "Generate a reusable Compose component with preview annotations"
- "Optimize this Compose UI for recomposition performance"

### 7. Navigation - Jetpack Navigation

**Description**: Type-safe navigation framework for Compose applications.

**Best Practices**:

- Use Navigation Compose with type-safe routes
- Handle deep links declaratively
- Manage backstack properly
- Pass only primitive types or IDs between screens
- Implement nested navigation for complex flows

```kotlin
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("profile/{userId}") { backStackEntry ->
            ProfileScreen(userId = backStackEntry.arguments?.getString("userId"))
        }
    }
}
```

**AI Agent Integration**:

- "Generate a type-safe navigation setup for these screens"
- "Add deep link support to this navigation graph"
- "Create a nested navigation structure for this feature module"

### 8. MVVM - Model - ViewModel - View

**Description**: Architectural pattern separating UI logic from business logic.

**Best Practices**:

- ViewModels should not reference Views or Context
- Expose UI state as StateFlow or LiveData
- Handle process death with SavedStateHandle
- Use ViewModelScope for coroutines
- Keep ViewModels testable with dependency injection

```kotlin
class ProfileViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val userId = savedStateHandle.get<String>("userId")!!

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun onEvent(event: ProfileEvent) {
        // Handle events
    }
}
```

**AI Agent Integration**:

- "Generate a ViewModel with StateFlow for this screen's requirements"
- "Refactor this ViewModel to follow MVVM best practices"
- "Add SavedStateHandle support to preserve state across process death"

### 9. Dependency Injection - Hilt

**Description**: Compile-time dependency injection framework built on Dagger.

**Best Practices**:

- Use `@HiltAndroidApp`, `@AndroidEntryPoint` annotations
- Scope dependencies appropriately (`@Singleton`, `@ViewModelScoped`)
- Prefer constructor injection over field injection
- Use `@Provides` and `@Binds` in modules correctly
- Test with `@HiltAndroidTest`

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository
}

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel()
```

**AI Agent Integration**:

- "Generate Hilt modules for this feature's dependencies"
- "Convert this manual DI to Hilt with proper scoping"
- "Create a test configuration with Hilt for this module"

### 10. State Management

**Description**: Efficient state handling ensures predictable UI updates and data consistency.

**Best Practices**:

- Use immutable state objects
- Implement single source of truth pattern
- Handle loading, success, and error states explicitly
- Apply state hoisting in Compose
- Use `rememberSaveable` for configuration changes

```kotlin
@Immutable
data class ScreenState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val error: String? = null
)

@Composable
fun Screen(
    state: ScreenState,
    onEvent: (ScreenEvent) -> Unit
) {
    // UI implementation
}
```

**AI Agent Integration**:

- "Generate a complete state management solution for this feature"
- "Add loading and error states to this ViewModel"
- "Implement state restoration for this Compose screen"

### 11. Data Persistence - Room and Paging

**Description**: Local database management with Room and efficient large dataset handling with
Paging.

**Best Practices**:

- Use Room with coroutines and Flow
- Implement migrations for schema changes
- Apply database views for complex queries
- Use Paging 3 for infinite scrolling
- Combine RemoteMediator for network + database

```kotlin
@Entity
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String
)

@Dao
interface UserDao {
    @Query("SELECT * FROM UserEntity")
    fun getUsers(): PagingSource<Int, UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)
}
```

**AI Agent Integration**:

- "Generate Room entities and DAOs for this data model"
- "Implement Paging 3 with RemoteMediator for this list"
- "Create a database migration from version 1 to 2"

### 12. Offline Support and Synchronization

**Description**: Ensuring app functionality without network connectivity.

**Best Practices**:

- Design offline-first architecture
- Use WorkManager for background sync
- Implement conflict resolution strategies
- Cache data with appropriate TTL
- Show sync status in UI

```kotlin
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: Repository
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            repository.syncData()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
```

**AI Agent Integration**:

- "Design an offline-first sync strategy for this feature"
- "Generate WorkManager setup for periodic data synchronization"
- "Implement conflict resolution for offline data changes"

### 13. Testing

**Description**: Comprehensive testing ensures code reliability and maintainability.

**Best Practices**:

- Write unit tests for ViewModels and use cases
- Use test doubles (mocks, fakes) appropriately
- Implement UI tests with Compose Testing
- Apply TDD when possible
- Aim for 80%+ code coverage
- Use Turbine for Flow testing

```kotlin
@Test
fun `when load users succeeds, state contains users`() = runTest {
        val users = listOf(User("1", "John"))
        coEvery { repository.getUsers() } returns flow { emit(users) }

        viewModel.loadUsers()

        viewModel.uiState.test {
            assertEquals(UiState.Loading, awaitItem())
            assertEquals(UiState.Success(users), awaitItem())
        }
    }
```

**AI Agent Integration**:

- "Generate unit tests for this ViewModel with 100% coverage"
- "Create UI tests for this Compose screen"
- "Add test doubles for these dependencies"

### 14. Error Handling and Logging

**Description**: Robust error handling and logging for debugging and monitoring.

**Best Practices**:

- Use sealed classes for Result types
- Implement global error handling
- Log with appropriate levels
- Avoid logging sensitive information
- Use Timber for logging abstraction

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}
```

**AI Agent Integration**:

- "Add comprehensive error handling to this repository"
- "Implement a global error handler for coroutines"
- "Add appropriate logging without exposing sensitive data"

### 15. Security Best Practices

**Description**: Protecting user data and preventing security vulnerabilities.

**Best Practices**:

- Use EncryptedSharedPreferences for sensitive data
- Implement certificate pinning for APIs
- Validate all inputs
- Use ProGuard/R8 for obfuscation
- Follow OWASP Mobile Top 10

```kotlin
val masterKey = MasterKey.Builder(context)
    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
    .build()

val encryptedPrefs = EncryptedSharedPreferences.create(
    context,
    "secure_prefs",
    masterKey,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
)
```

**AI Agent Integration**:

- "Review this code for security vulnerabilities"
- "Implement secure storage for authentication tokens"
- "Add input validation to prevent injection attacks"

### 16. Performance Optimization

**Description**: Ensuring smooth user experience through performance best practices.

**Best Practices**:

- Use Baseline Profiles for startup optimization
- Implement lazy loading for lists
- Optimize Compose with stable classes
- Profile with Android Studio Profiler
- Minimize overdraw and layout complexity

```kotlin
@Stable
class StableWrapper<T>(val value: T)

@Composable
fun OptimizedList(items: List<Item>) {
    LazyColumn {
        items(items, key = { it.id }) { item ->
            ItemRow(item)
        }
    }
}
```

**AI Agent Integration**:

- "Optimize this Compose code for recomposition performance"
- "Generate a Baseline Profile for app startup"
- "Identify performance bottlenecks in this code"

### 17. Accessibility (A11y)

**Description**: Making apps usable for all users, including those with disabilities.

**Best Practices**:

- Add content descriptions to all interactive elements
- Ensure proper contrast ratios
- Support keyboard navigation
- Test with TalkBack
- Use semantic properties in Compose

```kotlin
@Composable
fun AccessibleButton(
    onClick: () -> Unit,
    text: String
) {
    Button(
        onClick = onClick,
        modifier = Modifier.semantics {
            contentDescription = text
            role = Role.Button
        }
    ) {
        Text(text)
    }
}
```

**AI Agent Integration**:

- "Add accessibility support to this Compose UI"
- "Review this screen for accessibility issues"
- "Generate content descriptions for these UI elements"

### 18. Internationalization and Localization

**Description**: Supporting multiple languages and regions.

**Best Practices**:

- Extract all strings to resources
- Use plurals for quantity strings
- Support RTL layouts
- Format dates/numbers per locale
- Test with pseudolocalization

```kotlin
Text(
    text = pluralStringResource(
        R.plurals.items_count,
        count,
        count
    )
)

// In strings.xml
<plurals name ="items_count">
<item quantity ="one">%d item</item>
<item quantity ="other">%d items</item>
</plurals>
```

**AI Agent Integration**:

- "Extract hardcoded strings to resources"
- "Add RTL support to this layout"
- "Generate string resources with proper pluralization"

### 19. Background Tasks - WorkManager

**Description**: Reliable background task execution with constraints.

**Best Practices**:

- Use appropriate work types (OneTime, Periodic)
- Set constraints (network, battery, etc.)
- Chain work for complex workflows
- Handle work cancellation
- Test with WorkManagerTestInitHelper

```kotlin
val constraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .setRequiresBatteryNotLow(true)
    .build()

val syncWork = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
    .setConstraints(constraints)
    .build()

WorkManager.getInstance(context).enqueueUniquePeriodicWork(
    "sync",
    ExistingPeriodicWorkPolicy.KEEP,
    syncWork
)
```

**AI Agent Integration**:

- "Generate WorkManager setup for this background task"
- "Create a complex work chain with error handling"
- "Add retry policy with exponential backoff"

### 20. CI/CD and Build Tools

**Description**: Automated build, test, and deployment pipelines.

**Best Practices**:

- Use Gradle Kotlin DSL
- Implement convention plugins
- Cache dependencies
- Run tests on PR
- Automate release builds

```kotlin
// build.gradle.kts
plugins {
    id("android.application.convention")
    id("android.hilt.convention")
}

android {
    defaultConfig {
        applicationId = "com.example.app"
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
    }
}
```

**AI Agent Integration**:

- "Generate GitHub Actions workflow for Android CI/CD"
- "Create Gradle convention plugins for multi-module setup"
- "Add pre-commit hooks for code quality checks"

### 21. Monitoring and Analytics

**Description**: Tracking app performance and user behavior.

**Best Practices**:

- Implement crash reporting with Crashlytics
- Track key user actions
- Monitor app performance metrics
- Use custom keys for debugging
- Respect user privacy

```kotlin
class AnalyticsImpl @Inject constructor() : Analytics {
    override fun logEvent(event: String, params: Map<String, Any>) {
        Firebase.analytics.logEvent(event) {
            params.forEach { (key, value) ->
                param(key, value.toString())
            }
        }
    }
}
```

**AI Agent Integration**:

- "Implement analytics tracking for this user flow"
- "Add crash reporting with custom keys"
- "Generate privacy-compliant analytics implementation"

### 22. Modularization Advanced - Multi-Module Setup

**Description**: Advanced modularization strategies for large-scale apps.

**Best Practices**:

- Use dynamic feature modules for on-demand delivery
- Implement api/implementation dependencies correctly
- Create buildSrc or convention plugins
- Use internal visibility modifiers
- Apply module graph assertions

```
:app
:feature:home (api :core:ui, implementation :core:network)
:feature:profile (api :core:ui)
:core:ui
:core:network (api :core:common)
:core:common
```

**AI Agent Integration**:

- "Design a scalable module architecture for 50+ developers"
- "Generate module dependency graph visualization"
- "Create build logic for feature toggles across modules"

### 23. UI Theming and Animations

**Description**: Consistent visual design and smooth animations.

**Best Practices**:

- Use Material 3 design system
- Support dynamic color (Material You)
- Implement smooth transitions
- Use animation specs appropriately
- Optimize animation performance

```kotlin
@Composable
fun AnimatedContent(visible: Boolean) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically()
    ) {
        Content()
    }
}

val animatedProgress by animateFloatAsState(
    targetValue = if (isLoading) 1f else 0f,
    animationSpec = tween(durationMillis = 600)
)
```

**AI Agent Integration**:

- "Generate Material 3 theme with dynamic color support"
- "Create smooth page transition animations"
- "Optimize this animation for 60fps performance"

### 24. Permissions and Runtime Handling

**Description**: Managing Android permissions gracefully.

**Best Practices**:

- Request permissions contextually
- Explain permission needs clearly
- Handle denial gracefully
- Use Accompanist permissions library
- Test permission flows thoroughly

```kotlin
@Composable
fun CameraFeature() {
    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )

    when {
        cameraPermissionState.status.isGranted -> {
            CameraView()
        }
        cameraPermissionState.status.shouldShowRationale -> {
            RationaleDialog { cameraPermissionState.launchPermissionRequest() }
        }
        else -> {
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text("Enable Camera")
            }
        }
    }
}
```

**AI Agent Integration**:

- "Implement runtime permission flow for location access"
- "Generate permission handling with proper rationale"
- "Add fallback UI for denied permissions"

### 25. Integration with Emerging Tech - ML Kit and AI/ML

**Description**: Incorporating machine learning capabilities into Android apps.

**Best Practices**:

- Use ML Kit for common ML tasks
- Implement on-device inference when possible
- Handle model updates gracefully
- Optimize model size and performance
- Provide fallbacks for unsupported devices

```kotlin
class TextRecognitionUseCase @Inject constructor() {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun recognizeText(bitmap: Bitmap): Result<String> = suspendCoroutine { cont ->
        val image = InputImage.fromBitmap(bitmap, 0)
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                cont.resume(Result.success(visionText.text))
            }
            .addOnFailureListener { e ->
                cont.resume(Result.failure(e))
            }
    }
}
```

**AI Agent Integration**:

- "Integrate ML Kit text recognition into this feature"
- "Generate TensorFlow Lite model integration code"
- "Implement on-device image classification with error handling"

## Edge Cases and Advanced Topics

### Foldable and Large Screen Support

**Description**: Optimizing for diverse screen sizes and form factors.

**Best Practices**:

- Use WindowSizeClass for adaptive layouts
- Handle fold states appropriately
- Test on various device configurations
- Implement responsive navigation patterns

```kotlin
@Composable
fun AdaptiveLayout() {
    val windowSizeClass = calculateWindowSizeClass()

    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> CompactLayout()
        WindowWidthSizeClass.Medium -> MediumLayout()
        WindowWidthSizeClass.Expanded -> ExpandedLayout()
    }
}
```

**AI Agent Integration**:

- "Generate adaptive layout for phone, tablet, and foldable"
- "Add fold-aware features to this screen"

### Wear OS Support

**Description**: Building companion apps for Wear OS devices.

**Best Practices**:

- Use Wear Compose for UI
- Optimize for small screens
- Implement rotary input support
- Minimize battery usage

**AI Agent Integration**:

- "Generate Wear OS companion app structure"
- "Convert this mobile UI to Wear OS format"

### Multi-Window and PiP Support

**Description**: Supporting multi-window environments and picture-in-picture.

**Best Practices**:

- Handle configuration changes properly
- Save and restore state
- Adjust UI for different window sizes
- Implement PiP for video playback

**AI Agent Integration**:

- "Add multi-window support with proper state handling"
- "Implement PiP mode for video player"

## Conclusion

### Sample Project Structure

```
MyAndroidApp/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/app/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── MainApplication.kt
│   │   │   │   └── navigation/
│   │   │   │       └── AppNavigation.kt
│   │   └── test/
├── feature/
│   ├── home/
│   │   ├── src/main/java/com/example/home/
│   │   │   ├── presentation/
│   │   │   │   ├── HomeScreen.kt
│   │   │   │   └── HomeViewModel.kt
│   │   │   ├── domain/
│   │   │   │   └── GetHomeDataUseCase.kt
│   │   │   └── data/
│   │   │       └── HomeRepository.kt
│   ├── profile/
│   └── settings/
├── core/
│   ├── ui/
│   │   └── src/main/java/com/example/ui/
│   │       ├── theme/
│   │       └── components/
│   ├── network/
│   │   └── src/main/java/com/example/network/
│   │       ├── di/
│   │       └── interceptors/
│   ├── database/
│   │   └── src/main/java/com/example/database/
│   │       ├── dao/
│   │       └── entities/
│   └── common/
├── buildSrc/
│   └── src/main/kotlin/
│       ├── AndroidLibraryConventionPlugin.kt
│       └── AndroidFeatureConventionPlugin.kt
├── gradle/
│   └── libs.versions.toml
└── build.gradle.kts
```

### Scaling for Large Teams (50+ Developers)

1. **Module Ownership**: Assign clear ownership for each feature module
2. **API Boundaries**: Define explicit public APIs for modules
3. **Code Review Process**: Implement PR templates with architecture checklist
4. **Documentation**: Maintain ADRs (Architecture Decision Records)
5. **Tooling**: Use custom lint rules and pre-commit hooks
6. **Performance Monitoring**: Implement module-level build time tracking

### AI-Powered Development Workflow

1. **Feature Development**: "Generate complete feature module from these requirements: [specs]"
2. **Code Review**: "Review this PR for architecture violations and suggest improvements"
3. **Refactoring**: "Identify technical debt in this module and create a refactoring plan"
4. **Testing**: "Generate comprehensive test suite for this feature with edge cases"
5. **Documentation**: "Generate KDoc documentation for all public APIs"

### Integration Tips

- Start with core modules and gradually migrate features
- Implement feature flags for gradual rollout
- Use dependency injection to maintain testability
- Monitor app metrics after each major change
- Maintain backward compatibility during migration

## References

### Official Documentation

- [Android Developer Documentation](https://developer.android.com/)
- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Android Architecture Guide](https://developer.android.com/topic/architecture)

### Sample Projects

- [Now in Android](https://github.com/android/nowinandroid) - Official Android sample
- [Sunflower](https://github.com/android/sunflower) - Jetpack best practices
- [Tivi](https://github.com/chrisbanes/tivi) - Production-ready architecture

### Books

- "Kotlin in Action" by Dmitry Jemerov and Svetlana Isakova
- "Jetpack Compose by Tutorials" by Raywenderlich
- "Android Programming: The Big Nerd Ranch Guide"

### Tools

- [Android Studio](https://developer.android.com/studio)
- [GitHub Copilot](https://github.com/features/copilot)
- [Gemini Code Assist](https://cloud.google.com/gemini/docs/codeassist)
- [Detekt](https://detekt.dev/) - Static code analysis
- [Konsist](https://github.com/LemonAppDev/konsist) - Architecture testing

### Community Resources

- [Android Developers Blog](https://android-developers.googleblog.com/)
- [r/androiddev](https://www.reddit.com/r/androiddev/)
- [Android Weekly](https://androidweekly.net/)
- [Kotlin Weekly](http://kotlinweekly.net/)

---

*This document is a living guide and should be updated regularly to reflect the latest Android
development best practices and tooling improvements. Last updated: 2025*