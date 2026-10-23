---
paths:
  - "feature/**/*.kt"
  - "app/src/**/*.kt"
---

# MVI Pattern (Presentation Layer)

Every screen in `feature/*/impl` follows **State + Intent + Effect**. Each screen has a
`<Screen>Contract.kt` (UiState, SingleEvent, UiState mappers) and a `<Screen>ViewModel.kt`.
How the Composable side consumes them is in `compose-rules.md`.

## UiState: pick one of two shapes

**A. Form / input screens** whose state must survive process death: `@Parcelize @Immutable data class`
with a `StateSaver`.

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

  internal class StateSaver {
    fun SignInUiState.toBundle() = bundleOf(VIEW_STATE_KEY to this)
    inline fun restore(bundle: Bundle?, initial: () -> SignInUiState) =
      bundle?.getParcelableCompat(VIEW_STATE_KEY) ?: initial()
  }
}
```

**B. Data screens** (load, show, retry): LCE `@Immutable sealed interface`, no `@Parcelize`.

```kotlin
@Immutable
sealed interface LanguageUiState {
  companion object {
    val initial: LanguageUiState get() = Loading
  }

  @Immutable data object Loading : LanguageUiState

  @Immutable
  data class Content(val items: PersistentList<ItemUiModel>) : LanguageUiState // empty → empty state

  @Immutable data class Error(val error: AppError) : LanguageUiState
}
```

Rules for both:
- `@Immutable` on the type and on every subtype / nested UiModel. All fields `val`.
- Lists are `ImmutableList` / `PersistentList`, never `List` (see `detekt-hygiene.md`).
- `initial` is a `val` getter, not `const val`.
- `Error` holds an `AppError`, never a `String`.
- Selection lives on the item (`isSelected`), not as `selectedItemId` on the outer state.
- No navigation triggers, toasts or snackbars in UiState.

## SingleEvent (side effects)

```kotlin
@Immutable
sealed interface SignInSingleEvent {
  data object SignInSuccess : SignInSingleEvent
  @JvmInline value class SignInFailure(val error: AppError) : SignInSingleEvent
}
```

- `sealed interface`, not `sealed class`. `@JvmInline value class` for a single payload.
- Defined in the Contract file.
- **User-initiated navigation does not go through SingleEvent.** The Composable takes
  `onNavigateToX: () -> Unit` and the entry provider wires it to `navigator` (see `navigation.md`).
- Navigation **after async work** (sign-in success, logout, deep link, 401), snackbars and dialogs
  do go through SingleEvent.
- Use `EventChannel` (injected by Hilt, `Channel.UNLIMITED`) for one-shot events. Never
  `SharedFlow` / `StateFlow`.
- `EventChannel` closes only when the ViewModel is cleared, and under Nav3 a ViewModel can outlive
  its screen's visibility. Events sent while the screen is hidden queue up and all fire when it
  returns. If an event only makes sense while visible, narrow the source
  (e.g. `.map { it is Authenticated }.distinctUntilChanged()`) instead of relying on a fresh ViewModel.

## ValidationStatus (form input)

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

## ViewModel

```kotlin
@HiltViewModel
internal class SignInViewModel @Inject constructor(
  private val authRepository: AuthRepository,
  private val eventChannel: EventChannel<SignInSingleEvent>,
  savedStateHandle: SavedStateHandle,
) : ViewModel(eventChannel), HasEventFlow<SignInSingleEvent> by eventChannel {

  private val stateSaver = SignInUiState.StateSaver()

  private val _uiStateFlow = MutableStateFlow(
    stateSaver.restore(savedStateHandle[VIEW_STATE_BUNDLE_KEY]) { SignInUiState.initial },
  )
  val uiStateFlow: StateFlow<SignInUiState> = _uiStateFlow.asStateFlow()

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
          failure = { error ->
            Timber.e(error, "Failed to sign in")
            eventChannel.send(SignInSingleEvent.SignInFailure(error))
          },
        )
      emitState { it.copy(isLoading = false) }
    }
  }
}
```

- `@HiltViewModel`, `internal`.
- The state update function is **always** named `emitState`.
- One public function per user action. No `sealed interface` Intent in this project.
- Call repository interfaces from `core:domain` only, never DataSources.
- Keep complex business logic in the repository.
- **The UI reads derived state, it never recomputes it.** Validation results, button enablement,
  error messages are computed here and exposed in UiState.
- Work that must re-run each time the screen is shown goes in a `LaunchedEffect` in the Route, not
  in `init` (Nav3 keeps ViewModels alive; see `navigation.md`).

## UiState mappers

Map domain models to UI items with a dedicated extension function in the Contract file, named
`to + DomainModel + UiItemType`. Never build UI items inline inside the ViewModel's `.map { }`.

```kotlin
// Contract file
internal fun SearchResult.toSearchResultContentUiItem(): SearchUiState.ResultContentUiItem =
  SearchUiState.ResultContentUiItem(id = id, title = title)

// ViewModel
contents = items.mapToPersistentList { it.toSearchResultContentUiItem() }
```

## Sub-state update functions

Updating one sub-state goes through a file-private inline extension on the UiState, placed after
the ViewModel class. Name it action + sub-state; return `this` when the sub-state has another type.

```kotlin
// Right
private inline fun SearchUiState.updateSuggestionContent(
  transform: (SearchUiState.SuggestionUiState.Visible.Content) -> SearchUiState.SuggestionUiState.Visible.Content,
): SearchUiState = when (val current = suggestionUiState) {
  is SearchUiState.SuggestionUiState.Visible.Content -> copy(suggestionUiState = transform(current))
  else -> this
}

// Wrong: casting and copying inline in emitState { }
```

## List operations in UiState

Use the helpers in `core.common.extension.ImmutableList`:

| Operation | Use | Not |
|---|---|---|
| Map | `mapToPersistentList { }` | `.map { }.toPersistentList()` |
| Filter | `filterToPersistentList { }` | `.filter { }.toPersistentList()` |
| Map not null | `mapNotNullToPersistentList { }` | `.mapNotNull { }.toPersistentList()` |
| Build | `buildPersistentList { add(...) }` | `persistentListOf(...).mutate { }` |
| Upsert by key | `upsertByKey(items) { it.id }` | manual indexOf + removeAt + add |
| Append without duplicates | `filterDuplicatesAndAddAll(items) { it.id }` | `.plus(items).distinctBy { it.id }` |
