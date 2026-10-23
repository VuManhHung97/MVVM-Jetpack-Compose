---
name: feature-vertical-slice
description: Create a complete feature vertical slice through every Clean Architecture layer (domain model, repository, network DTO/ApiService/Fake+Real data source, data mapper/repository, MVI presentation, Compose UI, navigation wiring), backed by dummy JSON until the backend exists.
argument-hint: <domain>
disable-model-invocation: true
---

# Feature Vertical Slice

Build a full slice for `$ARGUMENTS`: domain model → repository interface → network (DTO + ApiService
contract + DataSource + **Fake reading dummy JSON + Real written up front** + DI) → data (mapper +
`Default*Repository` + binding) → MVI presentation → Compose UI → `entryProvider` wiring → hygiene.

`/new-feature-module` only scaffolds an empty presentation layer. This skill delivers a slice with
data, so that when the backend is ready you change **one `@Binds` line**.

| Placeholder | Meaning | Example |
|---|---|---|
| `<domain>` | lowercase, package name | `history` |
| `<Domain>` | PascalCase, class / type (singular) | `History` |

## Source of truth: read the rules first

This skill does not repeat conventions. Read and follow:

- `.claude/rules/clean-architecture.md`: layer boundaries, Repository / DataSource, `AppError`, `coroutineBinding`.
- `.claude/rules/data-layer.md`: DTO → ApiService → Fake / Real DataSource → mapper → repository. **Use its templates for layers 3 and 4.**
- `.claude/rules/mvi-pattern.md`, `compose-rules.md`, `navigation.md`.
- `.claude/rules/kotlin-style.md`, `theming-strings-resources.md`, `detekt-hygiene.md`.
- `.claude/rules/local-storage.md` if the domain needs caching / DataStore / Room.

## Default decisions (change if the domain needs otherwise)

- Fake data source reads **dummy JSON via Moshi + classloader**. Write the Real impl too.
- UiState is an `@Immutable` **LCE sealed interface**, no `@Parcelize` / `StateSaver`, unless the
  feature has form input or filters that must survive process death.
- Work **inline and sequentially** in dependency order. Don't fan out parallel subagents: the
  layers depend on each other and cold agents drift on naming and return types.

## Layers

### 1. Domain model (`core:model`)

`core/model/src/main/kotlin/.../core/model/<domain>/<Domain>Item.kt`. Pure Kotlin, no Android /
Retrofit / Room annotations, `java.time.Instant` for timestamps, no resource ids.

```kotlin
data class <Domain>Item(
  val id: String,
  val title: String,
  val occurredAt: Instant,
  val type: <Domain>Type,
) {
  enum class <Domain>Type { /* real values */ }
}
```

### 2. Repository interface (`core:domain`)

`core/domain/src/main/kotlin/.../core/domain/repository/<Domain>Repository.kt`

```kotlin
interface <Domain>Repository {
  suspend fun get<Domain>Items(): Result<List<<Domain>Item>, AppError>
}
```

### 3. Network (`core:network`)

Follow `data-layer.md` §1–5:
- `remote/response/<domain>/<Domain>Response.kt` (`@Keep`, `@param:Json`, real API shape)
- `remote/service/<Domain>ApiService.kt` (real contract)
- `remote/datasource/<Domain>RemoteDataSource.kt` → `Result<List<<Domain>Response>, AppError.ApiException>`
- `remote/datasourceimpl/Fake<Domain>RemoteDataSourceImpl.kt` + `Real<Domain>RemoteDataSourceImpl.kt`
- `core/network/src/main/resources/dummy/<domain>.json`
- `remote/di/<Domain>RemoteModule.kt` binding the **Fake**, with a comment on the line to switch

### 4. Data (`core:data`)

- `mapper/<domain>/<Domain>Mapper.kt`: `internal fun <Domain>Response.to<Domain>Item()`, string → enum here.
- `repository/Default<Domain>Repository.kt`: `internal`, `withContext(appCoroutineDispatchers.io)`,
  map the Result value (`ApiException` is already an `AppError`):
  ```kotlin
  override suspend fun get<Domain>Items() = withContext(appCoroutineDispatchers.io) {
    <domain>RemoteDataSource.get<Domain>Items().map { items -> items.map { it.to<Domain>Item() } }
  }
  ```
- Bind in `di/DataModule.kt`:
  ```kotlin
  @Binds
  fun <domain>Repository(impl: Default<Domain>Repository): <Domain>Repository
  ```

### 5. Presentation (`feature/<domain>`)

`feature/<domain>/api/build.gradle.kts`:
```kotlin
plugins { id(libs.plugins.android.feature.api.get().pluginId) }
android { namespace = "com.vmh.mvvmjetpackcompose.feature.<domain>.api" }
```

`feature/<domain>/impl/build.gradle.kts` (never `core:data` / `network` / `local`; `api(...)` only
for its own `:api`):
```kotlin
plugins { id(libs.plugins.android.feature.impl.get().pluginId) }
android { namespace = "com.vmh.mvvmjetpackcompose.feature.<domain>" }
dependencies {
  api(projects.feature.<domain>.api)

  implementation(projects.core.ui)
  implementation(projects.core.resource)
  implementation(projects.core.common)
  implementation(projects.core.domain)
  implementation(projects.core.model)
  implementation(libs.kotlinx.collections.immutable)
}
```

`<Domain>Contract.kt`: LCE UiState + UiModel + SingleEvent + mapper at the end of the file.

```kotlin
@Immutable
sealed interface <Domain>UiState {
  @Immutable data object Loading : <Domain>UiState
  @Immutable data class Content(val items: PersistentList<<Domain>ItemUiModel>) : <Domain>UiState // empty → empty state
  @Immutable data class Error(val error: AppError) : <Domain>UiState
}

@Immutable
data class <Domain>ItemUiModel(
  val id: String,
  val title: String,
  val occurredAt: Instant, // formatted in the Composable with ZoneId.systemDefault()
  @param:DrawableRes val iconResId: Int,
  @param:StringRes val typeLabelResId: Int,
)

@Immutable
sealed interface <Domain>SingleEvent {
  @JvmInline value class LoadFailure(val error: AppError) : <Domain>SingleEvent
}

internal fun <Domain>Item.to<Domain>ItemUiModel(): <Domain>ItemUiModel = /* map type → iconResId / typeLabelResId */
```

`<Domain>ViewModel.kt`:

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
        success = { items ->
          emitState { <Domain>UiState.Content(items.mapToPersistentList { it.to<Domain>ItemUiModel() }) }
        },
        failure = { error ->
          Timber.e(error, "Failed to load <domain>")
          emitState { <Domain>UiState.Error(error) }
        },
      )
    }
  }
}
```

`<Domain>Screen.kt`: `<Domain>Route` (stateful, `internal`) + `<Domain>Content` (stateless, `private`).
- Route: `hiltViewModel()`, `collectAsStateWithLifecycle()`,
  `viewModel.eventFlow.collectInLaunchedEffectWithLifecycle { }` for snackbars.
- Content: `when (uiState)` → Loading spinner / Content (`LazyColumn` with `key = { it.id }`,
  `contentType` from a private enum, explicit empty state) / Error with retry.
- Item row in `ui/component/<Domain>ItemRow.kt`. Format `occurredAt` with `DateTimeFormatter` +
  `ZoneId.systemDefault()` in the Composable.

`<Domain>NavKey.kt` (in `api`) and `<Domain>EntryProvider.kt` (in `impl`): see `/new-feature-module`
step 4 and `navigation.md`.

### 6. Resources (`core:resource`)

- Strings in `res/values/strings.xml` (`<domain>_title`, `<domain>_empty`, one label per enum type).
- Reuse existing `ic_*` icons; otherwise add `res/drawable/ic_<domain>_*.xml`
  (`fillColor="@android:color/white"`, tint at the call site).
- Colors per `theming-strings-resources.md`; no new theme or color object.

### 7. Wiring

1. `settings.gradle.kts`: `include(":feature:<domain>:api")` and `include(":feature:<domain>:impl")`.
2. `app/build.gradle.kts`: `implementation(projects.feature.<domain>.api)` and `.impl`.
3. `MainActivity`: one line `<domain>Entry(navigator)` inside `entryProvider { }`.
4. The calling module adds `implementation(projects.feature.<domain>.api)`; its entry provider calls
   `navigator.navigateTo<Domain>()`. No navigation lambdas across modules.
5. A non-tab screen never touches `topLevelRoutes`.

## Execution

- **Step 1, data slice**: layers 1–4. Checkpoint `./gradlew :core:data:assembleDebug`.
- **Step 2, feature slice**: layers 5–6. Checkpoint `./gradlew :feature:<domain>:impl:assembleDebug`.
- **Step 3, wiring + hygiene**: layer 7, then hand off to the `android-quality` agent (spotless +
  detekt + mechanical fixes). Checkpoint `./gradlew assembleDebug` with `BUILD SUCCESSFUL` in the log.
- **Step 4, review**: run `/pr-checklist` on the diff before reporting done.

## Switching to the real API

The Real impl already exists: change the one `@Binds` line in `<Domain>RemoteModule` from `Fake…` to
`Real…` and delete `resources/dummy/<domain>.json`. ViewModel, UI, mapper, repository and
ApiService stay untouched.

## Done checklist

- [ ] `feature/<domain>` imports nothing from `core:data` / `network` / `local`.
- [ ] No mock / seed data in ViewModel or repository.
- [ ] `PersistentList` in `@Immutable` state; `LazyColumn` has `key` + `contentType`.
- [ ] All text via `stringResource`; display enums use `@StringRes` / `@DrawableRes`.
- [ ] spotless + detekt `BUILD SUCCESSFUL`; every `@Suppress` has a comment.
- [ ] Fake → Real is exactly one `@Binds` line.
