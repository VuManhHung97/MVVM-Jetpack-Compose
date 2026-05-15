---
description: Local storage rules — DataStore Proto, Room, mapper patterns và error handling.
globs: core/local/**/*.kt, core/data/**/*.kt
---

# Local Storage Rules

## DataStore Proto

### Error Handling khi đọc

```kotlin
// Đúng — IOException khi đọc → emit default instance thay vì crash
val dataFlow: Flow<Result<UserProto, AppError.LocalStorageException>> = dataStore.data
    .map { proto -> Result.success(proto) }
    .catch { throwable ->
        if (throwable is IOException) {
            emit(Result.success(UserProto.getDefaultInstance()))
        } else {
            emit(Result.failure(AppError.LocalStorageException.FileException(throwable)))
        }
    }
```

### update() với Result

```kotlin
// Đúng — wrap bằng Result khi project dùng kotlin-result
suspend fun update(transform: (LocalUser?) -> LocalUser?): Result<Unit, AppError.LocalStorageException> =
    catchingLocalStorageException {
        dataStore.updateData { current ->
            transform(current.toLocalUser())?.toProto() ?: UserProto.getDefaultInstance()
        }
    }
```

### Proto Wrapper cho Tri-state

```kotlin
// Dùng BoolValue khi cần phân biệt "chưa set" vs "set = false"
// BoolValue.newBuilder().setValue(true).build()  → explicitly true
// null → chưa từng set (default override behavior)
```

### Default Constants — đặt đúng chỗ

- Default values thuộc về mapper hoặc domain layer, không hard-code trong Proto schema.
- Ví dụ: `autoPlayDefault` có thể khác nhau theo `horizontal`/`vertical`/`short` → đặt trong mapper.

```kotlin
// core/data/mapper/SettingsMapper.kt
fun SettingsProto.toSettings(): Settings = Settings(
    autoPlay = when (orientation) {
        Orientation.HORIZONTAL -> hasAutoPlay && autoPlay.value
        Orientation.VERTICAL -> true  // default khác nhau
        Orientation.SHORT -> false
    }
)
```

## Mapper Pattern — Không ghi đè Token

```kotlin
// Khi map ProfileResponse → LocalUser: KHÔNG ghi đè accessToken/refreshToken
// vì profile API response không có token fields

// Sai
fun ProfileResponse.toLocalUser(): LocalUser = LocalUser(
    id = id,
    email = email,
    accessToken = null,     // XÓA MẤT TOKEN CŨ!
    refreshToken = null,
)

// Đúng — update profile fields, giữ nguyên token
fun LocalUser.updateProfile(profile: ProfileResponse): LocalUser = copy(
    email = profile.email,
    fullName = profile.fullName,
    avatar = profile.avatar,
    // accessToken và refreshToken không được touch
)
```

## LocalDataSource Interface

```kotlin
interface AuthLocalDataSource {
    fun observeLocalUser(): Flow<Result<LocalUser?, AppError.LocalStorageException>>
    
    suspend fun readLocalUser(): Result<LocalUser?, AppError.LocalStorageException> =
        observeLocalUser().first()  // default implementation
    
    suspend fun update(
        transform: (LocalUser?) -> LocalUser?
    ): Result<Unit, AppError.LocalStorageException>
}
```

- Interface `public`, implementation `internal`.
- `observeLocalUser()` là source of truth — `readLocalUser()` consume nó.
- `update()` nhận transform function, không nhận value trực tiếp → đảm bảo atomic update.

## Room DAO (nếu có)

- DAO `interface` trong `core:local`, implementation do Room generate.
- Entity class: `@Entity`, `@PrimaryKey`, `@ColumnInfo` — chỉ nằm trong `core:local`.
- Không expose Room entity ra ngoài `core:local`; chỉ expose `LocalUser` hoặc domain model sau khi map.

## AppError Mapping cho Local Storage

```kotlin
// Luôn wrap exception thành AppError.LocalStorageException
private suspend fun <T> catchingLocalStorageException(
    block: suspend () -> T
): Result<T, AppError.LocalStorageException> = runCatching { block() }
    .fold(
        onSuccess = { Result.success(it) },
        onFailure = { throwable ->
            val error = when (throwable) {
                is IOException -> AppError.LocalStorageException.FileException(throwable)
                is SQLiteException -> AppError.LocalStorageException.DatabaseException(throwable)
                else -> AppError.LocalStorageException.UnknownException(throwable)
            }
            Result.failure(error)
        }
    )
```
