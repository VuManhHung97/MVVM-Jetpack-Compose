---
paths:
  - "core/local/**"
  - "core/data/**/*.kt"
---

# Local Storage Rules

## Error mapping

Every local failure becomes an `AppError.LocalStorageException` through the helpers in
`core:local` `mapper/LocalErrorMapper.kt`. Don't hand-roll `runCatching` or `try/catch`.

- `catchingLocalStorageException(localErrorMapper) { ... }` for one-shot reads and writes.
- `Flow<T>.catchingLocalStorageException(localErrorMapper)` for observed data.
- `LocalErrorMapperImpl` runs the throwable through `nonFatalOrThrow()` first, so
  `CancellationException` is rethrown, then maps `IOException` / `SQLException` / others to
  `FileException` / `DatabaseException` / `UnknownException`.

## DataStore (Proto)

### Reading: an `IOException` means "no data", not a crash

```kotlin
override fun observeLocalUser() = localUserDataStore
  .data
  .map { localUser -> localUser.takeIf { it.isNotDefault() } }
  .catch {
    // dataStore.data throws an IOException when reading fails
    if (it is IOException) emit(null) else throw it
  }
  .catchingLocalStorageException(localErrorMapper)
  .distinctUntilChanged()
```

### Writing: `update()` takes a transform, returns a Result

```kotlin
override suspend fun update(transform: (LocalUser?) -> LocalUser?) =
  catchingLocalStorageException(localErrorMapper) {
    localUserDataStore.updateData { current ->
      transform(current.takeIf { it.isNotDefault() }) ?: LocalUser.getDefaultInstance()
    }
    Unit
  }
```

### Tri-state values

Use proto wrappers (`BoolValue`, ...) when "never set" must differ from "set to false". A missing
wrapper means "not set, use the default".

### Defaults belong in the mapper

Default values live in the mapper or domain layer, not in the proto schema. If a default depends on
context (e.g. autoplay differs by orientation), compute it in the mapper.

## LocalDataSource interfaces

```kotlin
interface AuthLocalDataSource {
  fun observeLocalUser(): Flow<Result<LocalUser?, AppError.LocalStorageException>>

  suspend fun readLocalUser(): Result<LocalUser?, AppError.LocalStorageException> = observeLocalUser().first()

  suspend fun update(transform: (LocalUser?) -> LocalUser?): Result<Unit, AppError.LocalStorageException>
}
```

- Interface `public` (in `datasource/`), implementation `internal` (in `datasourceimpl/`).
- `observeX()` is the source of truth; `readX()` consumes it.
- `update()` takes a transform, never a value, so the read-modify-write is atomic.

## Never overwrite tokens when mapping a profile

Profile responses carry no tokens. Building a fresh `LocalUser` from one would erase them.
Start from the stored proto with `toBuilder()` and set only profile fields (see
`core:data` `mapper/user/UserMapper.kt`):

```kotlin
// Wrong: a new builder drops the stored accessToken / refreshToken
internal fun ProfileResponse.toLocalUser(): LocalUser = LocalUser.newBuilder()
  .setId(UInt64Value.of(id))
  .setEmail(StringValue.of(email))
  .build()

// Right: keep everything, overwrite profile fields only
internal fun LocalUser.updateProfile(profile: ProfileResponse): LocalUser = toBuilder()
  .setId(UInt64Value.of(profile.id))
  .setEmail(StringValue.of(profile.email))
  .setFullName(profile.fullName.toProtoStringValue())
  .build()
```

## Room (if used)

- DAO interfaces and `@Entity` classes live only in `core:local`.
- Never expose a Room entity outside `core:local`; expose a local model or map to a domain model.
