---
description: Data layer cho tính năng/dữ liệu mới chưa có API thật — DTO + ApiService + Fake data source đọc dummy JSON + mapper + repository. Bổ sung cho clean-architecture (biến thể Fake trước khi có backend).
globs: core/network/**/*.kt, core/data/**/*.kt, core/domain/**/*.kt, core/model/**/*.kt
---

# Data Layer — API contract + Fake data source + dummy JSON

Rule cho **tính năng/dữ liệu mới CHƯA có API thật**. Mục tiêu: dựng đủ tầng data thật (DTO → service → data source → mapper → repository) nhưng tạm chạy bằng **dummy JSON**, để khi có backend chỉ cần đổi 1 binding (Fake → Real), không đụng caller.

> Nguyên tắc: **KHÔNG** để mock data nằm trong ViewModel hay repository. Dữ liệu giả phải đi qua đúng tầng như thật.

## Sơ đồ

```
UI/ViewModel → Repository (domain interface)
                   ↓ (impl trong data layer)
             RemoteDataSource (interface)
                   ↓
        FakeRemoteDataSourceImpl  ── đọc ──→  src/main/resources/dummy/<name>.json
        (sau này: RealRemoteDataSourceImpl → ApiService/Retrofit)
```

Vị trí (điều chỉnh theo cấu trúc module của project):
- DTO + ApiService + DataSource + impl + DI → **network module**
- Mapper DTO→domain + Repository impl → **data module**
- Domain model + Repository interface → **domain/model module**

## 1. DTO (network module, package `response/<domain>/`)

Trường JSON dùng annotation JSON của thư viện parse (ví dụ Moshi). Dùng `@Keep` để R8 không xoá.

```kotlin
@Keep
data class XxxResponse(
  @param:Json(name = "id") val id: String,
  @param:Json(name = "display_name") val displayName: String,
  @param:Json(name = "amount") val amount: Long,
  @param:Json(name = "status") val status: String, // map sang enum ở mapper
)
```

## 2. ApiService — định nghĩa contract API THẬT (dù đang fake)

Viết interface Retrofit đúng như backend sẽ có. Chưa gọi cũng viết, để đổi sang thật là bind lại impl.

```kotlin
internal interface XxxApiService {
  @GET("v1/api/xxx")
  suspend fun getXxx(): BaseResponse.Data<List<XxxResponse>> // hoặc List<XxxResponse> tuỳ envelope của project

  @POST("v1/api/xxx/{id}/action")
  suspend fun doAction(@Path("id") id: String, @Body body: XxxRequestBody)

  companion object Factory {
    operator fun invoke(retrofit: Retrofit): XxxApiService = retrofit.create()
  }
}
```

## 3. RemoteDataSource interface (network module)

Trả `Result<Dto, AppError.ApiException>` (hoặc kiểu Result/error mà project dùng). Interface `public`, impl `internal`.

```kotlin
interface XxxRemoteDataSource {
  suspend fun getXxx(): Result<List<XxxResponse>, AppError.ApiException>
  suspend fun doAction(id: String, /* params */): Result<Unit, AppError.ApiException>
}
```

## 4. FakeRemoteDataSourceImpl — đọc dummy JSON

Cơ chế (tường minh, không phụ thuộc file mẫu):
- Đặt file JSON tĩnh ở **`<networkModule>/src/main/resources/dummy/<name>.json`**. `src/main/resources` được đóng gói vào APK và đọc được qua **classloader** — **không cần Android `Context`** (khác `assets/` cần Context).
- Nội dung JSON đúng **shape của response API thật** (để khi đổi sang Real cùng adapter).
- Parse bằng JSON adapter (ví dụ Moshi): với list cần `Types.newParameterizedType(List::class.java, Dto::class.java)`.
- Write action (POST/PUT) khi chưa có backend: trả `Ok(Unit)` (repository sẽ cập nhật optimistic).

```kotlin
internal class FakeXxxRemoteDataSourceImpl @Inject constructor(
  private val moshi: Moshi,
  private val dispatchers: AppCoroutineDispatchers,
) : XxxRemoteDataSource {

  override suspend fun getXxx() = withContext(dispatchers.io) {
    Ok(readJsonList("dummy/xxx.json", XxxResponse::class.java))
  }

  override suspend fun doAction(id: String) = Ok(Unit)

  private fun <T> readJsonList(path: String, itemType: Class<T>): List<T> {
    val listType = Types.newParameterizedType(List::class.java, itemType)
    val adapter = moshi.adapter<List<T>>(listType)
    val json = requireNotNull(javaClass.classLoader?.getResourceAsStream(path))
      .bufferedReader().use { it.readText() }
    return adapter.fromJson(json).orEmpty()
  }
}
```

Ví dụ `resources/dummy/xxx.json`:
```json
[
  { "id": "X1", "display_name": "Tên A", "amount": 1250000, "status": "active" }
]
```

## 5. DI module (network module)

```kotlin
@Module @InstallIn(SingletonComponent::class)
internal interface XxxRemoteModule {
  @Binds fun xxxRemoteDataSource(impl: FakeXxxRemoteDataSourceImpl): XxxRemoteDataSource
  // Khi có backend: đổi FakeXxx... → RealXxx... ở đúng dòng này.

  companion object {
    @Provides @Singleton
    fun xxxApiService(@SharedRetrofit retrofit: Retrofit): XxxApiService = XxxApiService(retrofit)
  }
}
```

## 6. Mapper (data module, package `mapper/<domain>/`)

`internal fun`, DTO → domain. Map string→enum ở đây.

```kotlin
internal fun XxxResponse.toXxx(): Xxx = Xxx(
  id = id,
  name = displayName,
  amount = amount,
  status = if (status == "active") XxxStatus.Active else XxxStatus.Inactive,
)
```

## 7. Repository impl (data module)

Domain interface ở domain module; impl `internal`, `@Singleton` nếu cần giữ state chia sẻ (vd nhiều màn cùng đọc). Load lazy + optimistic write:

```kotlin
@Singleton
internal class DefaultXxxRepository @Inject constructor(
  private val remote: XxxRemoteDataSource,
  private val dispatchers: AppCoroutineDispatchers,
) : XxxRepository {

  private val itemsFlow = MutableStateFlow<List<Xxx>>(emptyList())
  private val loadMutex = Mutex()
  private var loaded = false

  override fun observeItems(): Flow<List<Xxx>> = itemsFlow.onStart { ensureLoaded() }

  override suspend fun doAction(id: String): Result<Unit, AppError> = withContext(dispatchers.io) {
    // Chưa có backend: cập nhật optimistic ngay vào flow (mọi màn tự đồng bộ).
    itemsFlow.update { list -> list.map { if (it.id == id) it.copy(/* ... */) else it } }
    Ok(Unit)
    // Khi có backend: gọi remote.doAction(id) trước, thành công thì refresh itemsFlow từ remote.
  }

  private suspend fun ensureLoaded() = loadMutex.withLock {
    if (loaded) return
    itemsFlow.value = remote.getXxx().map { it.map(XxxResponse::toXxx) }.getOrElse { emptyList() }
    loaded = true
  }
}
```

Bind interface→impl trong DI của data module (`@Binds @Singleton`).

## Chuyển sang API thật (sau này)
1. Viết `RealXxxRemoteDataSourceImpl` gọi `XxxApiService` (bọc lỗi thành `AppError.ApiException`).
2. Đổi `@Binds` trong `XxxRemoteModule`: `FakeXxx…` → `RealXxx…`.
3. Repository: `doAction` gọi remote trước rồi refresh; bỏ optimistic nếu muốn nguồn-thật-là-chân-lý.
4. Xoá file `resources/dummy/*.json`. Không đụng ViewModel/UI.

## Checklist
- [ ] Không còn mock/seed hardcode trong ViewModel/Repository.
- [ ] DTO có annotation JSON + `@Keep`; JSON dummy đúng shape response.
- [ ] DataSource trả `Result<Dto, ApiException>`; impl `internal`.
- [ ] Repository interface ở domain, impl ở data (`internal`), bind qua Hilt.
- [ ] Fake đọc JSON từ `resources/` qua classloader (không cần Context).

---
*Ví dụ trong repo hiện tại (tham khảo — có thể không tồn tại ở project khác): fake data source + remote module của tính năng `language`; `Default*Repository` của `auth`/`search`; envelope `BaseResponse.Data`.*
