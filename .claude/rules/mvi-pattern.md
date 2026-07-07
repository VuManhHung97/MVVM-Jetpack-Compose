---
description: MVI pattern rules for feature screens — UiState, SingleEvent, ViewModel structure. Applied to all presentation layer files.
globs: feature/**/*.kt
---

# MVI Pattern — Presentation Layer Rules

Every screen in `feature/*` follows the MVI contract: **State + Intent + Effect**.

## Contract Structure

Each screen has a contract file `<Screen>Contract.kt` containing:

### UiState

```kotlin
@Parcelize
@Immutable
data class SignInUiState(
    val email: String,
    val password: String,
    val isLoading: Boolean,
    val emailValidationStatus: ValidationStatus,
) : Parcelable {
    companion object {
        val initial get() = SignInUiState(email = "", password = "", isLoading = false, ...)
    }

    // Process-death survival via SavedStateHandle
    internal class StateSaver {
        fun SignInUiState.toBundle() = bundleOf(VIEW_STATE_KEY to this)
        inline fun restore(bundle: Bundle?, initial: () -> SignInUiState) =
            bundle?.getParcelableCompat(VIEW_STATE_KEY) ?: initial()
    }
}
```

- `@Parcelize @Immutable` — mandatory, without exception.
- All fields immutable. Never use `var`.
- `StateSaver` inner class for `SavedStateHandle` persistence.
- `initial` là `val`, không phải `const val` — cho phép lazy init nếu cần.

### SingleEvent (Side effects)

```kotlin
@Immutable
sealed interface SignInSingleEvent {
    data object SignInSuccess : SignInSingleEvent
    @JvmInline value class SignInFailure(val error: AppError) : SignInSingleEvent
}
```

- Dùng `sealed interface`, không dùng `sealed class`.
- Dùng `@JvmInline value class` cho event có payload đơn.
- **Không bao giờ** đặt navigation trigger hoặc toast trong `UiState`. Navigation và snackbar là side effect, phải đi qua `SingleEvent`.
- Không dùng `SharedFlow` hay `StateFlow` cho one-shot event — dùng `EventChannel` (Channel.UNLIMITED).

### ValidationStatus (nếu có form input)

```kotlin
sealed interface ValidationStatus : Parcelable {
    @Parcelize data object Valid : ValidationStatus
    @Parcelize sealed interface Error : ValidationStatus {
        sealed interface Email : Error {
            @Parcelize data object Empty : Email
            @Parcelize data object InvalidFormat : Email
        }
    }
}
```

## ViewModel Structure

```kotlin
@HiltViewModel
internal class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val eventChannel: EventChannel<SignInSingleEvent>,
    savedStateHandle: SavedStateHandle,
) : ViewModel(eventChannel), HasEventFlow<SignInSingleEvent> by eventChannel {

    private val stateSaver = SignInUiState.StateSaver()

    private val _uiStateFlow = MutableStateFlow(
        stateSaver.restore(savedStateHandle[VIEW_STATE_BUNDLE_KEY]) { SignInUiState.initial }
    )
    val uiStateFlow: StateFlow<SignInUiState> = _uiStateFlow.asStateFlow()

    // Luôn dùng tên emitState cho hàm update state
    private inline fun emitState(f: (SignInUiState) -> SignInUiState) = _uiStateFlow.update(f)

    init {
        savedStateHandle.setSavedStateProvider(VIEW_STATE_BUNDLE_KEY) {
            stateSaver.run { uiStateFlow.value.toBundle() }
        }
    }

    fun signIn() {
        emitState { it.copy(isLoading = true) }
        viewModelScope.launch {
            authRepository.signIn(uiStateFlow.value.email, uiStateFlow.value.password)
                .fold(
                    success = { eventChannel.send(SignInSingleEvent.SignInSuccess) },
                    failure = { eventChannel.send(SignInSingleEvent.SignInFailure(it)) },
                )
            emitState { it.copy(isLoading = false) }
        }
    }
}
```

## Rules

- Tên hàm update state **luôn là `emitState`**, không đổi tên.
- ViewModel chỉ gọi Repository interface từ `core:domain`, không bao giờ gọi DataSource trực tiếp.
- `@HiltViewModel` là bắt buộc.
- Visibility `internal` cho ViewModel class.
- Mỗi user action = một public function trên ViewModel (không dùng `sealed interface` cho intent trong project này).
- `EventChannel` được inject qua Hilt, không tạo thủ công.
- Không để business logic phức tạp trong ViewModel — delegate xuống Repository.
- **UI đọc state, không tính lại.** Trạng thái validate / dẫn xuất (vd đã hợp lệ hay chưa, nút có enable không, thông báo lỗi) đã được ViewModel tính và phơi trong `UiState` → Composable **đọc thẳng từ đó**, tuyệt đối **không recompute** logic đó trong màn. Recompute = lặp logic nghiệp vụ ở UI + dễ lệch với ViewModel.

## Composable Side — Thu thập State và Event

```kotlin
@Composable
fun SignInScreen(viewModel: SignInViewModel = hiltViewModel()) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    // Thu thập single events
    LaunchedEffect(viewModel) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is SignInSingleEvent.SignInSuccess -> { /* navigate */ }
                is SignInSingleEvent.SignInFailure -> { /* show snackbar */ }
            }
        }
    }
}
```

- Không dùng `remember { }` bọc quanh `collectAsStateWithLifecycle()`.
- `LaunchedEffect(viewModel)` với key là ViewModel instance (stable).

## UiState Mapper Convention

Khi map domain model sang UiState item, luôn tạo **extension function riêng** — không inline trong `.map { }` trong ViewModel.

**Naming:** `to + {DomainModelName} + {UiItemType}`

```kotlin
// Đúng — mapper riêng trong SearchContact.kt, cùng file với SearchUiState
fun SearchResult.toSearchResultContentUiItem(): SearchUiState.ResultContentUiItem =
    SearchUiState.ResultContentUiItem(id = id, title = title)

// Đúng — dùng trong ViewModel
contents = items.mapToPersistentList { it.toSearchResultContentUiItem() }

// Sai — inline trong ViewModel
contents = items.mapToPersistentList { SearchUiState.ResultContentUiItem(id = it.id, title = it.title) }
```

Đặt mapper function trong file Contract (`*Contact.kt` / `*Contract.kt`) cùng với UiState definition — cùng pattern với `toHistorySuggestionUiItem()`, `toAutocompleteSuggestionUiItem()`.

Dùng `mapToPersistentList { }` từ `core.common.extension.ImmutableList` thay vì `.map { }.toPersistentList()` để tránh intermediate list allocation.

## Inline State Update Functions

Mọi hàm update một sub-state cụ thể trong UiState phải được tách thành private inline extension function riêng — không inline trực tiếp logic vào trong `emitState { }`.

Pattern bắt buộc:
- Extension trên UiState (file-level private, đặt ở cuối file ViewModel sau closing brace của class)
- Nhận lambda `transform` với kiểu input/output tường minh
- Return type luôn là `UiState` (outer state)
- Tên function: action + substate (ví dụ: `updateSearchResultContent`, `updateSuggestionContent`)
- Nếu substate không đúng loại → return `this` không đổi gì

```kotlin
// Đúng
private inline fun SearchUiState.updateSuggestionContent(
    transform: (SearchUiState.SuggestionUiState.Visible.Content) -> SearchUiState.SuggestionUiState.Visible.Content,
): SearchUiState = when (val current = suggestionUiState) {
    is SearchUiState.SuggestionUiState.Visible.Content -> copy(suggestionUiState = transform(current))
    else -> this
}

// Sai — inline logic trực tiếp trong emitState
emitState { state ->
    val current = state.suggestionUiState as? SearchUiState.SuggestionUiState.Visible.Content ?: return@emitState state
    state.copy(suggestionUiState = current.copy(suggestions = ...))
}
```

## List Operations in UiState

Khi thao tác với list bên trong UiState, luôn dùng extension functions từ `core.common.extension.ImmutableList` — không dùng standard Kotlin operators rồi chuyển đổi thủ công.

| Operation | Dùng | Không dùng |
|---|---|---|
| Map sang PersistentList | `mapToPersistentList { }` | `.map { }.toPersistentList()` |
| Filter sang PersistentList | `filterToPersistentList { }` | `.filter { }.toPersistentList()` |
| Map nullable | `mapNotNullToPersistentList { }` | `.mapNotNull { }.toPersistentList()` |
| Build từ đầu | `buildPersistentList { add(...) }` | `persistentListOf(...).mutate { }` |
| Upsert theo key | `upsertByKey(items) { it.id }` | thủ công indexOf + removeAt + add |
| Thêm tránh trùng | `filterDuplicatesAndAddAll(items) { it.id }` | `.plus(items).distinctBy { it.id }` |
