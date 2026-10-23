---
paths:
  - "core/network/**"
  - "core/data/**/*.kt"
  - "core/domain/**/*.kt"
  - "core/model/**/*.kt"
---

# Data Layer: new data without a backend yet

For a **new feature or data set that has no real API yet**. Build the full real data stack
(DTO → ApiService → DataSource → mapper → repository) but run it on **dummy JSON**, so that when
the backend arrives you change one binding (Fake → Real) and no caller.

> **Never** put mock data in a ViewModel or repository. Fake data goes through the same layers as
> real data.

```
UI / ViewModel → Repository (domain interface)
                     ↓ impl in core:data
               RemoteDataSource (interface)
                     ↓
       FakeXxxRemoteDataSourceImpl ── reads ──→ core/network/src/main/resources/dummy/<name>.json
       (later: RealXxxRemoteDataSourceImpl → XxxApiService / Retrofit)
```

| What | Module | Package |
|---|---|---|
| DTO, ApiService, DataSource + impls, DI | `core:network` | `remote/response/<domain>/`, `remote/service/`, `remote/datasource/`, `remote/datasourceimpl/`, `remote/di/` |
| Mapper DTO → domain, repository impl | `core:data` | `mapper/<domain>/`, `repository/` |
| Domain model, repository interface | `core:model`, `core:domain` | `<domain>/`, `repository/` |

## 1. DTO

Moshi annotations + `@Keep` so R8 keeps the fields.

```kotlin
@Keep
data class XxxResponse(
  @param:Json(name = "id") val id: String,
  @param:Json(name = "display_name") val displayName: String,
  @param:Json(name = "amount") val amount: Long,
  @param:Json(name = "status") val status: String, // mapped to an enum in the mapper
)
```

## 2. ApiService: write the real contract now

Define the Retrofit interface as the backend will expose it, even though nothing calls it yet.

```kotlin
internal interface XxxApiService {
  @GET("v1/api/xxx")
  suspend fun getXxx(): BaseResponse.Data<List<XxxResponse>>

  @POST("v1/api/xxx/{id}/action")
  suspend fun doAction(@Path("id") id: String, @Body body: XxxRequestBody)

  companion object Factory {
    operator fun invoke(retrofit: Retrofit): XxxApiService = retrofit.create()
  }
}
```

## 3. RemoteDataSource interface

Returns `Result<Dto, AppError.ApiException>`. Interface `public`, impls `internal`.

```kotlin
interface XxxRemoteDataSource {
  suspend fun getXxx(): Result<List<XxxResponse>, AppError.ApiException>
  suspend fun doAction(id: String): Result<Unit, AppError.ApiException>
}
```

## 4. Fake impl reading dummy JSON

- Put the JSON in **`core/network/src/main/resources/dummy/<name>.json`**. `src/main/resources` is
  packaged into the APK and readable through the **classloader**, so no Android `Context` is needed
  (unlike `assets/`).
- The JSON has **the exact shape of the real API response**, so the same adapter works later.
- Write actions (POST / PUT) return `Ok(Unit)`; the repository updates optimistically.

```kotlin
internal class FakeXxxRemoteDataSourceImpl @Inject constructor(
  private val moshi: Moshi,
  private val appCoroutineDispatchers: AppCoroutineDispatchers,
) : XxxRemoteDataSource {

  override suspend fun getXxx() = withContext(appCoroutineDispatchers.io) {
    Ok(readJsonList("dummy/xxx.json", XxxResponse::class.java))
  }

  override suspend fun doAction(id: String) = Ok(Unit)

  private fun <T> readJsonList(path: String, itemType: Class<T>): List<T> {
    val listType = Types.newParameterizedType(List::class.java, itemType)
    val adapter = moshi.adapter<List<T>>(listType)
    val json = requireNotNull(javaClass.classLoader?.getResourceAsStream(path))
      .bufferedReader()
      .use { it.readText() }
    return adapter.fromJson(json).orEmpty()
  }
}
```

Write the Real impl at the same time, wrapping errors with `catchingApiException(remoteErrorMapper)`:

```kotlin
internal class RealXxxRemoteDataSourceImpl @Inject constructor(
  private val xxxApiService: XxxApiService,
  private val appCoroutineDispatchers: AppCoroutineDispatchers,
  private val remoteErrorMapper: RemoteErrorMapper,
) : XxxRemoteDataSource {
  override suspend fun getXxx() = withContext(appCoroutineDispatchers.io) {
    catchingApiException(remoteErrorMapper) { xxxApiService.getXxx().data }
  }
  // ...
}
```

## 5. DI module

```kotlin
@Module
@InstallIn(SingletonComponent::class)
internal interface XxxRemoteModule {
  // Fake (dummy JSON) for now. When the backend is ready, switch to RealXxxRemoteDataSourceImpl here.
  @Binds
  fun xxxRemoteDataSource(impl: FakeXxxRemoteDataSourceImpl): XxxRemoteDataSource

  companion object {
    @Provides
    @Singleton
    fun xxxApiService(@SharedRetrofit retrofit: Retrofit): XxxApiService = XxxApiService(retrofit)
  }
}
```

## 6. Mapper

`internal fun`, DTO → domain. String → enum conversion happens here.

```kotlin
internal fun XxxResponse.toXxx(): Xxx = Xxx(
  id = id,
  name = displayName,
  amount = amount,
  status = if (status == "active") XxxStatus.Active else XxxStatus.Inactive,
)
```

## 7. Repository impl

Interface in `core:domain`, impl `internal` in `core:data`, bound in `DataModule`. Use `@Singleton`
when several screens share the state. Lazy load + optimistic write:

```kotlin
@Singleton
internal class DefaultXxxRepository @Inject constructor(
  private val xxxRemoteDataSource: XxxRemoteDataSource,
  private val appCoroutineDispatchers: AppCoroutineDispatchers,
) : XxxRepository {

  private val itemsFlow = MutableStateFlow<List<Xxx>>(emptyList())
  private val loadMutex = Mutex()
  private var isLoaded = false

  override fun observeItems(): Flow<List<Xxx>> = itemsFlow.onStart { ensureLoaded() }

  override suspend fun doAction(id: String): Result<Unit, AppError> = withContext(appCoroutineDispatchers.io) {
    // No backend yet: update the flow optimistically so every screen stays in sync.
    itemsFlow.update { items -> items.map { if (it.id == id) it.copy(/* ... */) else it } }
    Ok(Unit)
    // With a backend: call xxxRemoteDataSource.doAction(id) first, then refresh from remote.
  }

  private suspend fun ensureLoaded() = loadMutex.withLock {
    if (isLoaded) return
    itemsFlow.value = xxxRemoteDataSource.getXxx()
      .map { items -> items.map(XxxResponse::toXxx) }
      .getOrElse { emptyList() }
    isLoaded = true
  }
}
```

## Switching to the real API

1. Change the `@Binds` in `XxxRemoteModule`: `FakeXxx…` → `RealXxx…`.
2. Repository: call remote first, then refresh; drop the optimistic update if the server is the
   source of truth.
3. Delete `resources/dummy/<name>.json`. ViewModels and UI stay untouched.

> Note: `FakeLanguageRemoteDataSourceImpl` predates this rule and still returns a hardcoded list.
> New Fakes follow the dummy-JSON approach above.
