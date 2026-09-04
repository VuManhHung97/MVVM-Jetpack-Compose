# Feature Vertical Slice

Tạo một feature module **đầy đủ vertical-slice** đi qua mọi layer Clean Architecture của project: domain model → repository interface → network (DTO + ApiService contract + DataSource + **Fake đọc dummy JSON + Real viết sẵn** + DI) → data (mapper + `Default*Repository` + bind) → presentation MVI → UI Compose → wiring vào `entryProvider` → hygiene.

Bổ khuyết `/new-feature-module` (chỉ scaffold presentation rỗng, không có data layer): command này lo trọn slice có dữ liệu, tạm chạy bằng dummy JSON để khi có backend chỉ đổi **1 dòng `@Binds`**.

## Usage

```
/feature-vertical-slice <domain>        # vd: /feature-vertical-slice history
```

Quy ước placeholder dùng xuyên suốt (thay bằng tên thật của domain):

| Placeholder | Nghĩa | Ví dụ |
|---|---|---|
| `<domain>` | lowercase, tên package | `history` |
| `<Domain>` | PascalCase, tên class/type (singular) | `History` |
| `<domainPlural>` | plural cho tên feature/package nếu ngữ nghĩa là tập hợp (`kotlin-style.md`) | `histories` |

## Nguồn chân lý — đọc rule TRƯỚC khi sinh code

Command này **không lặp lại** convention. Nạp và tuân theo các rule (feature-agnostic) làm nguồn chân lý:

- [`.claude/rules/clean-architecture.md`](../rules/clean-architecture.md) — layer boundary, Repository/DataSource, `AppError`, `coroutineBinding`.
- [`.claude/rules/data-layer.md`](../rules/data-layer.md) — DTO → ApiService → Fake(dummy JSON)/Real DataSource → mapper → repository.
- [`.claude/rules/mvi-pattern.md`](../rules/mvi-pattern.md) — UiState/SingleEvent/ViewModel/`emitState`/mapper extension.
- [`.claude/rules/compose-rules.md`](../rules/compose-rules.md) — Composable API, state, side-effect, modal, contentType.
- [`.claude/rules/kotlin-style.md`](../rules/kotlin-style.md) — naming (boolean/count/callback, feature=plural class=singular), Instant, log.
- [`.claude/rules/theming-strings-resources.md`](../rules/theming-strings-resources.md) — string qua `stringResource`, enum dùng `@StringRes`/`@DrawableRes`, màu vào palette trung tâm.
- [`.claude/rules/local-storage.md`](../rules/local-storage.md) — nếu domain cần cache/DataStore/Room.
- [`.claude/rules/navigation.md`](../rules/navigation.md) — mô hình hai lớp, NavKey/entry provider, vòng đời ViewModel.
- [`.claude/rules/detekt-hygiene.md`](../rules/detekt-hygiene.md) — `ImmutableList` trong `@Immutable`, unused import, MaxLineLength, MagicNumber, `@Suppress` kèm comment.

**Portable**: các đường dẫn file cụ thể bên dưới chỉ là **ví dụ tham chiếu trong repo hiện tại**. Nếu project khác không có chúng, tìm symbol tương đương (ưu tiên skill graph `explore-codebase` để tiết kiệm token) hoặc theo mô tả convention + rule ở trên.

## Default quyết định (đổi nếu domain yêu cầu khác)

- Fake data source đọc **dummy JSON qua Moshi + classloader** (`data-layer.md` §4). **Viết luôn cả Real impl** để sau chỉ đổi 1 `@Binds`.
- `UiState` là `@Immutable` **LCE sealed interface** (Loading/Content/Error), **không** `@Parcelize`/`StateSaver` — trừ khi feature có form input/filter cần sống qua process death.
- Thực thi **inline tuần tự** theo dependency order; **không** fan-out subagent song song (layer phụ thuộc nhau, agent lạnh sẽ re-derive context + dễ lệch naming/return type).

---

## Layer templates (thay placeholder, field chỉ mang tính minh hoạ)

### 1. Domain model — `core:model`

`core/model/src/main/kotlin/.../core/model/<domain>/<Domain>Item.kt` — **pure Kotlin**, zero annotation Android/Retrofit/Room. Timestamp dùng `java.time.Instant`. Enum thuần; **không** giữ icon/label resId ở domain (để ở UiModel).

```kotlin
data class <Domain>Item(
  val id: String,
  val title: String,
  val description: String,
  val occurredAt: Instant,
  val type: <Domain>Type,
) {
  enum class <Domain>Type { /* các giá trị thật của domain */ }
}
```

### 2. Repository interface — `core:domain`

`core/domain/src/main/kotlin/.../core/domain/repository/<Domain>Repository.kt` — trả `Result<..., AppError>` (kotlin-result).

```kotlin
interface <Domain>Repository {
  suspend fun get<Domain>Items(): Result<List<<Domain>Item>, AppError>
}
```

### 3. Network — `core:network` (theo `data-layer.md`)

**DTO** `remote/response/<domain>/<Domain>Response.kt` — `@Keep` + `@param:Json`, shape khớp JSON API thật:
```kotlin
@Keep
data class <Domain>Response(
  @param:Json(name = "id") val id: String,
  @param:Json(name = "occurred_at") val occurredAtEpochMillis: Long,
  @param:Json(name = "type") val type: String, // map → enum ở mapper
  // ... field khác
)
```

**ApiService** `remote/service/<Domain>ApiService.kt` — `internal interface`, contract API thật (dù đang fake):
```kotlin
internal interface <Domain>ApiService {
  @GET("v1/api/<domain>")
  suspend fun get<Domain>Items(): BaseResponse.Data<List<<Domain>Response>> // envelope chung nếu project có; hoặc List<...>

  companion object Factory {
    operator fun invoke(retrofit: Retrofit): <Domain>ApiService = retrofit.create()
  }
}
```

**DataSource interface** `remote/datasource/<Domain>RemoteDataSource.kt` — `public`, impl `internal`:
```kotlin
interface <Domain>RemoteDataSource {
  suspend fun get<Domain>Items(): Result<List<<Domain>Response>, AppError.ApiException>
}
```

**Fake impl** `remote/datasourceimpl/Fake<Domain>RemoteDataSourceImpl.kt` — đọc dummy JSON qua classloader (không cần Context):
```kotlin
internal class Fake<Domain>RemoteDataSourceImpl @Inject constructor(
  private val moshi: Moshi,
  private val dispatchers: AppCoroutineDispatchers,
) : <Domain>RemoteDataSource {
  override suspend fun get<Domain>Items() = withContext(dispatchers.io) {
    Ok(readJsonList("dummy/<domain>.json", <Domain>Response::class.java))
  }

  private fun <T> readJsonList(path: String, itemType: Class<T>): List<T> {
    val listType = Types.newParameterizedType(List::class.java, itemType)
    val adapter = moshi.adapter<List<T>>(listType)
    val json = requireNotNull(javaClass.classLoader?.getResourceAsStream(path))
      .bufferedReader().use { it.readText() }
    return adapter.fromJson(json).orEmpty()
  }
}
```

**Real impl** `remote/datasourceimpl/Real<Domain>RemoteDataSourceImpl.kt` — viết sẵn cho backend; bọc lỗi thành `AppError.ApiException` bằng helper chung của project:
```kotlin
internal class Real<Domain>RemoteDataSourceImpl @Inject constructor(
  private val <domain>ApiService: <Domain>ApiService,
  private val appCoroutineDispatchers: AppCoroutineDispatchers,
  private val remoteErrorMapper: RemoteErrorMapper,
) : <Domain>RemoteDataSource {
  override suspend fun get<Domain>Items() = withContext(appCoroutineDispatchers.io) {
    catchingApiException(remoteErrorMapper) { <domain>ApiService.get<Domain>Items().data }
  }
}
```

**Dummy JSON** `core/network/src/main/resources/dummy/<domain>.json` — mảng đúng shape DTO (nếu file dài/lặp: `@Suppress` cho detekt kèm comment, xem `detekt-hygiene.md`).

**DI module** `remote/di/<Domain>RemoteModule.kt`:
```kotlin
@Module @InstallIn(SingletonComponent::class)
internal interface <Domain>RemoteModule {
  // Đang dùng Fake (dummy JSON). Có backend: đổi thành Real<Domain>RemoteDataSourceImpl — chỉ 1 dòng này.
  @Binds fun <domain>RemoteDataSource(impl: Fake<Domain>RemoteDataSourceImpl): <Domain>RemoteDataSource

  companion object {
    @Provides @Singleton
    fun <domain>ApiService(@SharedRetrofit retrofit: Retrofit): <Domain>ApiService = <Domain>ApiService(retrofit)
  }
}
```

### 4. Data — `core:data`

**Mapper** `mapper/<domain>/<Domain>Mapper.kt` — `internal fun`, map string→enum tại đây:
```kotlin
internal fun <Domain>Response.to<Domain>Item(): <Domain>Item = <Domain>Item(
  id = id,
  occurredAt = Instant.ofEpochMilli(occurredAtEpochMillis),
  type = type.to<Domain>Type(),
  // ...
)
```

**Repository impl** `repository/Default<Domain>Repository.kt` — `internal`, `withContext(io)`, map DTO→domain trên value của Result (không đổi error type; `ApiException` là con của `AppError`):
```kotlin
internal class Default<Domain>Repository @Inject constructor(
  private val <domain>RemoteDataSource: <Domain>RemoteDataSource,
  private val appCoroutineDispatchers: AppCoroutineDispatchers,
) : <Domain>Repository {
  override suspend fun get<Domain>Items() = withContext(appCoroutineDispatchers.io) {
    <domain>RemoteDataSource.get<Domain>Items().map { items -> items.map { it.to<Domain>Item() } }
  }
}
```

**Bind** thêm vào `di/DataModule.kt`:
```kotlin
@Binds fun <domain>Repository(impl: Default<Domain>Repository): <Domain>Repository
```

### 5. Presentation — `feature/<domain>` (theo `mvi-pattern.md`, `compose-rules.md`)

**`feature/<domain>/api/build.gradle.kts`** — chỉ contract điều hướng:
```kotlin
plugins { id(libs.plugins.android.feature.api.get().pluginId) }
android { namespace = "com.vmh.mvvmjetpackcompose.feature.<domain>.api" }
```

**`feature/<domain>/impl/build.gradle.kts`** — **không** phụ thuộc `core:data/network/local`:
```kotlin
plugins { id(libs.plugins.android.feature.impl.get().pluginId) }
android { namespace = "com.vmh.mvvmjetpackcompose.feature.<domain>" }
dependencies {
  api(projects.feature.<domain>.api)

  implementation(projects.core.ui)
  implementation(projects.core.resource)
  implementation(projects.core.common)
  api(projects.core.domain)
  api(libs.kotlinx.collections.immutable)
}
```

**`<Domain>Contract.kt`** — LCE sealed interface + UiModel (dùng `@StringRes`/`@DrawableRes` cho type) + SingleEvent + mapper cuối file:
```kotlin
@Immutable
sealed interface <Domain>UiState {
  @Immutable data object Loading : <Domain>UiState
  @Immutable data class Content(val items: PersistentList<<Domain>ItemUiModel>) : <Domain>UiState // items rỗng → empty state
  @Immutable data class Error(val appError: AppError) : <Domain>UiState
}

@Immutable
data class <Domain>ItemUiModel(
  val id: String,
  val title: String,
  val occurredAt: Instant,            // format ở Composable theo ZoneId.systemDefault()
  @param:DrawableRes val iconResId: Int,
  @param:StringRes val typeLabelResId: Int,
)

@Immutable
sealed interface <Domain>SingleEvent {
  @Immutable data class LoadFailure(val error: AppError) : <Domain>SingleEvent
}

fun <Domain>Item.to<Domain>ItemUiModel(): <Domain>ItemUiModel = /* map type → iconResId/typeLabelResId */
```

**`<Domain>ViewModel.kt`**:
```kotlin
@HiltViewModel
internal class <Domain>ViewModel @Inject constructor(
  private val eventChannel: EventChannel<<Domain>SingleEvent>,
  private val <domain>Repository: <Domain>Repository,
) : ViewModel(eventChannel), HasEventFlow<<Domain>SingleEvent> by eventChannel {
  private val _uiStateFlow = MutableStateFlow<<Domain>UiState>(<Domain>UiState.Loading)
  val uiStateFlow: StateFlow<<Domain>UiState> = _uiStateFlow.asStateFlow()
  private inline fun emitState(f: (<Domain>UiState) -> <Domain>UiState) = _uiStateFlow.update(f)

  init { load() }
  fun onRetry() = load()

  private fun load() {
    emitState { <Domain>UiState.Loading }
    viewModelScope.launch {
      <domain>Repository.get<Domain>Items().fold(
        success = { items -> emitState { <Domain>UiState.Content(items.mapToPersistentList { it.to<Domain>ItemUiModel() }) } },
        failure = { error ->
          Timber.e(error, "Failed to load <domain>")
          emitState { <Domain>UiState.Error(error) }
        },
      )
    }
  }
}
```

**`<Domain>Screen.kt`** — `<Domain>Route` (stateful) + `<Domain>Content` (stateless):
- Route: `hiltViewModel()`, `collectAsStateWithLifecycle()`, `viewModel.eventFlow.collectInLaunchedEffectWithLifecycle { ... }` → snackbar cho `LoadFailure`.
- Content: `when(uiState)` → Loading spinner / Content(`LazyColumn` với `key { it.id }` + `contentType` bằng **private enum** + empty state khi rỗng) / Error + nút retry.
- Extract item row ra `ui/component/<Domain>ItemRow.kt`. Format `occurredAt` bằng `DateTimeFormatter` + `ZoneId.systemDefault()` tại Composable.

**`<Domain>NavKey.kt`** (ở `api`):
```kotlin
@Serializable
data object <Domain>NavKey : NavKey

fun Navigator.navigateTo<Domain>() = navigate(<Domain>NavKey)
```

**`<Domain>EntryProvider.kt`** (ở `impl`, cùng package):
```kotlin
fun EntryProviderScope<NavKey>.<domain>Entry(navigator: Navigator) {
  entry<<Domain>NavKey> {
    <Domain>Route(onNavigateBack = { navigator.goBack() })
  }
}
```

### 6. Resources — `core:resource` (theo `theming-strings-resources.md`)

- String hiển thị vào `res/values/strings.xml` (`<domain>_title`, `<domain>_empty`, label cho từng enum type…), dùng `stringResource`; **không** hardcode.
- Icon type: tái dùng `ic_*` sẵn có nếu hợp; thiếu thì thêm vector `res/drawable/ic_<domain>_*.xml` (fillColor `@android:color/white`, tint ở call-site).
- Màu (nếu cần) thêm token vào palette trung tâm; **không** tạo theme/màu mới.

### 7. Wiring

1. `settings.gradle.kts`: thêm `include(":feature:<domain>:api")` và `include(":feature:<domain>:impl")`.
2. `app/build.gradle.kts`: thêm `implementation(projects.feature.<domain>.api)` và `.impl`.
3. `app/.../MainActivity.kt`: thêm **một dòng** `<domain>Entry(navigator)` vào block `entryProvider { }`.
4. Wire entry point: module gọi tới khai `implementation(projects.feature.<domain>.api)`, entry builder của
   nó gọi `navigator.navigateTo<Domain>()`. **Không** truyền lambda điều hướng xuyên module.
5. Màn mới **không phải tab** thì không được đụng vào `topLevelRoutes`.

---

## Execution steps (làm inline, tuần tự theo dependency order)

- **B1 — Data slice**: layer 1→4. Checkpoint: `./gradlew :core:data:assembleDebug`.
- **B2 — Feature slice**: layer 5→6. Checkpoint: `./gradlew :feature:<domain>:assembleDebug`.
- **B3 — Wiring + hygiene**: layer 7, rồi giao agent **`android-quality`** chạy `spotlessApply` + `detekt` và fix cơ học (`List`→`PersistentList`, unused import, dòng >120, MagicNumber). Checkpoint: `./gradlew assembleDebug` xanh (kiểm `BUILD SUCCESSFUL` trong log, không chỉ exit code).
- **B4 — Review**: chạy `review-changes` (graph-powered) để soát impact trước khi báo xong.

## Chuyển sang API thật (sau này)

Real impl đã viết sẵn. Chỉ cần: đổi **1 dòng `@Binds`** trong `<Domain>RemoteModule` từ `Fake…` → `Real…`; xoá `resources/dummy/<domain>.json`. **Không đụng** ViewModel/UI/mapper/repository/ApiService.

## Checklist trước khi báo xong

- [ ] `feature/<domain>` không import `core:data/network/local`.
- [ ] Không mock/seed hardcode trong ViewModel/Repository — dữ liệu đi qua DTO + DataSource + mapper.
- [ ] `@Immutable` state/param dùng `PersistentList`, không `List`; `LazyColumn` có `key` + `contentType` (private enum).
- [ ] Mọi text qua `stringResource`; enum hiển thị dùng `@StringRes`/`@DrawableRes`.
- [ ] `spotlessApply` + `detekt` `BUILD SUCCESSFUL`; `@Suppress` (nếu có) kèm comment.
- [ ] Fake→Real chỉ tốn đúng 1 dòng `@Binds`.
