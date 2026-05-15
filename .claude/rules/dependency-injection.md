---
description: Dependency Injection rules với Hilt — những gì nên và không nên inject, module organization.
globs: **/*.kt
---

# Dependency Injection Rules (Hilt)

## Nguyên tắc cốt lõi: Chỉ inject thứ class không tự kiểm soát được

### Nên inject (external dependencies)

- Repository, Service, DataSource
- `AppConfig`, `AppCoroutineDispatchers`
- Logger (Timber wrapper)
- SharedPreferences / DataStore
- `Context` (`@ApplicationContext`)
- Database (Room), File access
- Network client (OkHttp, Retrofit)
- Third-party SDK clients (ví dụ: `PhoneNumberUtil`, RevenueCat)

### Không inject (internal data structures)

```kotlin
// Sai — inject data structure nội bộ
@Inject lateinit var cache: MutableMap<String, Video>
@Inject lateinit var counter: AtomicInteger
@Inject lateinit var buffer: ArrayList<Event>

// Đúng — class tự tạo và quản lý cấu trúc nội bộ
class VideoRepository @Inject constructor(...) {
    private val cache = mutableMapOf<String, Video>()
    private val counter = AtomicInteger(0)
}
```

Lý do: DI container không nên biết về implementation detail của class.

## Lifecycle Ownership

- Class tự tạo và quản lý dữ liệu nội bộ của chính nó.
- Không đưa mọi thứ vào DI container chỉ vì tiện.

## Event Bus

- Nếu event phát ra từ non-UI layer (repository, service), dùng singleton UI-independent event bus.
- **Không inject** `AppEventViewModel` hay bất kỳ ViewModel nào vào observer/app-level class.

```kotlin
// Đúng — event bus independent với UI
class AppEventBus @Inject constructor() {
    private val _events = MutableSharedFlow<AppEvent>()
    val events: SharedFlow<AppEvent> = _events.asSharedFlow()
    suspend fun emit(event: AppEvent) { _events.emit(event) }
}

// Sai — inject ViewModel vào non-UI class
class SomeObserver @Inject constructor(
    private val appEventViewModel: AppEventViewModel  // WRONG
)
```

## Module Organization

### Convention Plugin

```kotlin
// Áp dụng Hilt cho bất kỳ module nào cần DI
plugins {
    id("convention.android.hilt")  // tự động config Hilt + Kapt
}
```

### Module Layering

| Module | @InstallIn |
|---|---|
| `core:data` → `DataModule` | `SingletonComponent` |
| `core:network` → `NetworkModule` | `SingletonComponent` |
| `core:local` → `LocalModule` | `SingletonComponent` |

```kotlin
// Bind interface → implementation trong DataModule
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: DefaultAuthRepository): AuthRepository
}
```

### Capability-Sliced Modules

Ưu tiên tách module theo capability khi phù hợp:

```
:core:phoneNumbers:domain   (pure JVM nếu có thể)
:core:phoneNumbers:data     (Android/lib-specific implementation)
```

- Domain module: pure JVM, không có Android dependencies.
- Data module: chứa Android/library-specific code.

## Convention Plugins Reference

| Plugin | Áp dụng cho |
|---|---|
| `convention.android.feature` | Feature modules (tự động include Hilt + flavors) |
| `convention.android.hilt` | Bất kỳ module nào cần Hilt |
| `convention.android.library` | Core library modules |
| `convention.jvm.library` | Pure JVM modules (không có Android, không có Hilt) |

## Layering Rule (nhắc lại từ Clean Architecture)

- Data/Repository xử lý data source, mapping, lib integration.
- UI nhận UiModel đã được map sẵn.
- **Không gọi** library/data logic trực tiếp trong Composable hoặc ViewModel nếu logic đó nên nằm trong Repository.
