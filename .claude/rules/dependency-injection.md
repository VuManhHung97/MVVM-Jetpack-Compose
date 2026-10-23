---
paths:
  - "**/*.kt"
  - "**/build.gradle.kts"
---

# Dependency Injection (Hilt)

## Inject only what a class cannot own itself

Inject external dependencies:

- Repositories, Managers, DataSources, API services
- `AppCoroutineDispatchers`, app config
- `DataStore`, Room database / DAOs, file access
- `Context` (always `@ApplicationContext`)
- Network clients (OkHttp, Retrofit, Moshi)
- Third-party SDK clients (Firebase, analytics, ...)

Never inject internal data structures. The class creates and owns them:

```kotlin
// Wrong
@Inject lateinit var cache: MutableMap<String, Video>
@Inject lateinit var counter: AtomicInteger

// Right
internal class DefaultVideoRepository @Inject constructor(/* external deps */) : VideoRepository {
  private val cache = mutableMapOf<String, Video>()
  private val counter = AtomicInteger(0)
}
```

The DI graph should not know a class's implementation details, and nothing goes into the graph
"just because it's convenient".

## Events from non-UI layers

When a repository or service needs to broadcast an event (401, force update, ...), expose it from a
singleton, UI-independent source in `core:data` (e.g. `UnauthorizedErrorEventRepository`) and let
ViewModels observe it. Never inject a ViewModel into a non-UI class.

```kotlin
// Wrong
class SomeObserver @Inject constructor(
  private val appEventViewModel: AppEventViewModel,
)
```

## Module layout

Every DI module is `internal interface` + `@InstallIn(SingletonComponent::class)`, with `@Binds`
for interface → implementation and a `companion object` for `@Provides`:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {
  @Binds
  fun authRepository(impl: DefaultAuthRepository): AuthRepository
}
```

| Module | Holds |
|---|---|
| `core:data` → `di/DataModule` | Repository / Manager bindings |
| `core:network` → `remote/di/*` | Retrofit, OkHttp clients, API services, remote DataSource bindings |
| `core:local` → `di/*` | DataStore, Room, local DataSource bindings |

Put `@Singleton` on the implementation class only when it holds shared state (in-memory cache,
`MutableStateFlow`, `Mutex`).

## Convention plugins

Apply plugins by their version-catalog alias, e.g. `id(libs.plugins.android.hilt.get().pluginId)`.

| Plugin id | Use for |
|---|---|
| `android.feature.api` | `feature/<x>/api`: library + parcelize + kotlinx-serialization, no Hilt, no Compose |
| `android.feature.impl` | `feature/<x>/impl`: library + Compose + Hilt + flavors |
| `android.library` | Android core modules with Compose |
| `android.core` | Android core modules without Compose |
| `android.hilt` | Any module that needs Hilt (Hilt + KAPT) |
| `jvm.library` | Pure JVM modules (`core:model`, `core:domain`): no Android, no Hilt |

## Layering reminder

- Repositories handle data sources, mapping and library integration.
- ViewModels receive domain models; UI receives UiModels already mapped by the ViewModel.
- Don't call library or data logic from a Composable or ViewModel when it belongs in a repository.
