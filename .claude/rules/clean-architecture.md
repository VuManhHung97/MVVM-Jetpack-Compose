---
description: Clean Architecture layer boundaries and dependency rules. Applied to all Kotlin source files across the project.
globs: **/*.kt
---

# Clean Architecture — Non-Negotiable Rules

Layer boundaries must never be violated. These rules take priority over all other considerations.

## Layer Map & Dependency Direction

```
Presentation  (feature/*)
      ↓
Domain        (core:domain, core:model)
      ↓                    ↑
Data          (core:data) ─┘
      ↓
Infrastructure (core:network, core:local)
```

| Layer | Module(s) | What lives here |
|---|---|---|
| Presentation | `feature/*` | ViewModel, UiState, SingleEvent, Composables |
| Domain | `core/domain`, `core/model` | Repository interfaces, domain models, `AppError` |
| Data | `core/data` | Repository implementations (`Default*`), DTO→model mappers |
| Infrastructure | `core/network`, `core/local` | DataSource interfaces + impls, Retrofit services, Room DAOs |

## Hard Dependency Rules

- `feature/*` may only import: `core:domain`, `core:model`, `core:ui`, `core:resource`, `core:common`. **Never** `core:data`, `core:network`, `core:local`.
- `core:data` depends on `core:domain` + `core:network`/`core:local`. Never the reverse.
- Domain models (`core:model`) must be **pure Kotlin** — zero Android framework, Retrofit, or Room annotations.
- DTOs live exclusively in `core:network`. Local entities live in `core:local`. Mappers live in `core:data`.
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
) : AuthRepository {
    override suspend fun signIn(email: String, password: String) = withContext(io) {
        coroutineBinding {
            val response = authRemoteDataSource.signIn(email, password).bind()
            authLocalDataSource.update { response.toLocalUser() }.bind()
        }
    }
}
```

## DataSource Layer

- `RemoteDataSource` interfaces in `core/network/` → return `Result<ResponseDto, AppError.ApiException>`
- `LocalDataSource` interfaces in `core/local/` → return `Result<LocalEntity?, AppError.LocalStorageException>` or `Flow<Result<...>>`

## Error Handling — `AppError` Hierarchy

All errors must be typed through `AppError`. **Never throw raw exceptions across layer boundaries.**

```
AppError
├── ApiException       → NetworkException, ServerException, TimeoutException, UnknownException
├── AuthException      → InvalidCredentialsException, UserCollisionException, NetworkException
├── LocalStorageException → FileException, DatabaseException, UnknownException
└── UnknownException
```

- Use `kotlin-result` (`Result<Value, AppError>`) for all suspend functions that can fail.
- Chain multiple Results with `coroutineBinding { val x = call().bind() }` — never nest `.fold()`.
- Map to the correct `AppError` subtype at the layer where the error originates (network → `ApiException`, local → `LocalStorageException`).

## No UseCase Layer

This project intentionally omits UseCases. ViewModels call Repository interfaces directly. Do not introduce a UseCase unless multiple ViewModels need to share non-trivial business logic that cannot live in the Repository itself.

## Không được claim Clean Architecture nếu chưa áp dụng

Nếu một feature/PR chưa tuân thủ đúng các layer trên, dùng cách diễn đạt:
- "MVVM with improved code organization"
- "focus on maintainability"

Không được nói "Clean Architecture" nếu layer boundary bị vi phạm.
