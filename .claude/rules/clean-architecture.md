---
paths:
  - "**/*.kt"
  - "**/build.gradle.kts"
---

# Clean Architecture — Non-Negotiable Rules

Layer boundaries must never be violated. These rules take priority over all other considerations.

## Layer Map & Dependency Direction

An arrow `A → B` means **A depends on B**. Domain depends on no other layer; Data points *into* Domain (Dependency Inversion).

```
Presentation (feature/*) ──→ Domain (core:domain, core:model) ←── Data (core:data)
                                                                       │
                                                                       ↓
                                                  Infrastructure (core:network ──→ core:local)
```

| Layer | Module(s) | What lives here |
|---|---|---|
| Presentation | `feature/*` | ViewModel, UiState, SingleEvent, Composables |
| Domain | `core/domain`, `core/model` | Repository interfaces, domain models, `AppError` |
| Data | `core/data` | Repository implementations (`Default*`), DTO→model mappers |
| Infrastructure | `core/network`, `core/local` | DataSource interfaces + impls, Retrofit services, Room DAOs |

## Hard Dependency Rules

- `feature/*` must **never** depend on `core:data`, `core:network`, `core:local`, or another feature's `:impl`. To navigate to another feature, depend on its `:api` only (see [`navigation.md`](navigation.md)). Any other `core:*` module (`domain`, `model`, `ui`, `resource`, `common`, `analytics`, `navigation`, …) is allowed when actually used.
- In a feature `:impl` `build.gradle.kts`, `api(...)` is reserved for the feature's **own** `:api` module. Every other dependency — `core:*`, other features' `:api`, libraries — uses `implementation(...)` so it does not leak to consumers.
- `core:data` depends on `core:domain` + `core:network`/`core:local`. Never the reverse.
- `core:network` → `core:local` is allowed **only** so the OkHttp interceptors (`AuthInterceptor`, `RefreshTokenInterceptor`) can read/write tokens via `AuthLocalDataSource`. Any other use of `core:local` from `core:network` goes through `core:data` instead. The reverse (`core:local` → `core:network`) is forbidden.
- `core:model` and `core:domain` must be **pure JVM** — no Android framework, Retrofit, Room, or Hilt. JDK types (`RuntimeException`, `java.io.Serializable`, `java.util.Locale`) are fine. Both modules apply the `jvm.library` plugin, so an Android import fails at compile time — never switch them to an Android plugin.
- Domain interfaces live in `core:domain` (package `repository`). Name them by role: `*Repository` for data access, `*Manager` for coordination that spans several data sources (e.g. `FcmTokenManager`).
- DTOs live exclusively in `core:network`. Local entities live in `core:local`.
- Mappers live where both their input and output types are visible:
  - **DTO / local entity → domain model** (and DTO → local entity): `core:data`, e.g. `UserMapper`, `SearchMapper`.
  - **`Throwable` → `AppError`**: the infrastructure module where the error originates — `RemoteErrorMapper` in `core:network`, `LocalErrorMapper` in `core:local`.
  - **Proto / primitive helpers** (e.g. `Locale` ↔ `LocalLocale`, `Instant` ↔ `Timestamp`): `core:local`, next to the proto types.
- All repository implementations are `internal`. Only interfaces are `public`.
- All DataSource implementations are `internal`. Only interfaces are `public` (consumed by `core:data`).

## Repository Pattern

```kotlin
// core:domain — interface with domain types only
interface AuthRepository {
  suspend fun signIn(email: String, password: String): Result<Unit, AppError>
  fun observeAuthenticationState(): Flow<Result<AuthenticationState, AppError.LocalStorageException>>
}

// core:data — internal implementation, uses coroutineBinding to chain Results
internal class DefaultAuthRepository @Inject constructor(
  private val authRemoteDataSource: AuthRemoteDataSource,
  private val authLocalDataSource: AuthLocalDataSource,
  private val appCoroutineDispatchers: AppCoroutineDispatchers,
) : AuthRepository {
  override suspend fun signIn(email: String, password: String) = withContext(appCoroutineDispatchers.io) {
    coroutineBinding {
      val response = authRemoteDataSource.signIn(email, password).bind()
      authLocalDataSource.update { response.toLocalUser() }.bind()
    }
  }
}
```

### Implementation Naming

| Kind | Convention | Example |
|---|---|---|
| Repository implementation | `Default<Interface>` | `DefaultAuthRepository`, `DefaultFcmTokenManager` |
| Repository decorator (wraps another impl of the same interface) | `<Behavior><Interface>` + qualifier on the delegate | `SynchronizedFcmTokenManager` (`@DelegateFcmTokenManager`) |
| DataSource implementation | `<Interface>Impl` | `AuthLocalDataSourceImpl` |
| DataSource with a real/fake pair | `Real<Interface>Impl` / `Fake<Interface>Impl` | `RealSearchRemoteDataSourceImpl`, `FakeLanguageRemoteDataSourceImpl` |

> `Real`/`Fake` prefixes belong to DataSources only — never use them on a Repository.

## DataSource Layer

- `RemoteDataSource` interfaces in `core/network/` → return `Result<ResponseDto, AppError.ApiException>`
- `LocalDataSource` interfaces in `core/local/` → return `Result<LocalEntity?, AppError.LocalStorageException>` or `Flow<Result<...>>`

## Error Handling — `AppError` Hierarchy

All errors must be typed through `AppError`. **Never throw raw exceptions across layer boundaries.**

The source of truth for the hierarchy is [`AppError.kt`](../../core/model/src/main/kotlin/com/vmh/mvvmjetpackcompose/core/model/error/AppError.kt) — read it instead of relying on a copy here. Pick the top-level branch by where the error originates:

| Branch | Raised by | Mapped in |
|---|---|---|
| `ApiException` | HTTP / Retrofit / OkHttp failures | `core:network` (`RemoteErrorMapper`) |
| `AuthException` | Third-party auth provider failures (e.g. Firebase Auth, Google Sign-In) | Predefined for future use — map provider errors to it with a dedicated mapper in the data source wrapping that provider |
| `LocalStorageException` | DataStore / Room / file I/O | `core:local` (`LocalErrorMapper`) |
| `UnknownException` | Anything that fits none of the above — last resort | wherever it is caught |

Within a branch, use the most specific subtype available; fall back to that branch's `UnknownException` rather than the top-level one.

- Use `kotlin-result` (`Result<Value, AppError>`) for all suspend functions that can fail.
- Chain multiple Results with `coroutineBinding { val x = call().bind() }` — never nest `.fold()`.
- Map to the correct `AppError` subtype at the layer where the error originates (network → `ApiException`, local → `LocalStorageException`).
- **Never wrap fatal throwables.** `CancellationException` (and other fatal errors) must be rethrown, not turned into `AppError` — otherwise coroutine cancellation breaks. Pass the caught throwable through `nonFatalOrThrow()` (`core:common`) before mapping, as `RemoteErrorMapper` and `LocalErrorMapper` do. Plain `runCatching { }` catches `CancellationException` — don't use it without this step.

## Network Interceptors

- `AuthInterceptor` refreshes the token under a `Mutex` so concurrent 401s trigger one refresh only:
  `runBlocking(appCoroutineDispatchers.io) { mutex.withLock { executeRefreshTokenIfNeeded(localUser) } }`.
- `RefreshTokenInterceptor` runs on a **separate** OkHttp client from `AuthInterceptor`
  (see `DataRemoteModule`). Never make the two clients depend on each other.

## No UseCase Layer

This project intentionally omits UseCases. ViewModels call Repository interfaces directly. Do not introduce a UseCase unless multiple ViewModels need to share non-trivial business logic that cannot live in the Repository itself.
