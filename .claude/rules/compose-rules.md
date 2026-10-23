---
paths:
  - "feature/**/*.kt"
  - "core/ui/**/*.kt"
  - "app/src/**/*.kt"
---

# Jetpack Compose Rules

Naming (callbacks `on + Noun + Verb`, named lambda parameters) and call-site style (named
arguments, one argument per line) are in `kotlin-style.md`. They apply to every Composable.

## Parameter order

Following the [Compose API guidelines](https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-component-api-guidelines.md#modifier-parameter),
`modifier` is the **first optional parameter**:

1. Required parameters (data, required callbacks).
2. `modifier: Modifier = Modifier`.
3. Other optional parameters (`enabled`, `colors`, `viewModel = hiltViewModel()`, ...).
4. Trailing `@Composable` lambda (`content`), if any.

```kotlin
@Composable
fun VideoCard(
  video: VideoUiModel,
  onVideoLikeToggle: (videoId: Video.Id) -> Unit,
  onVideoOpen: (videoId: Video.Id) -> Unit,
  modifier: Modifier = Modifier,
  isLiked: Boolean = false,
  content: @Composable () -> Unit = {},
)
```

Always default `modifier` to `Modifier` and apply it to the **root layout** only.

## Screens: `Route` + `Content`

Each screen in `feature/<x>/impl` has two Composables in `ui/<x>/<X>Screen.kt`:

| Layer | Name | Visibility | Responsibility |
|---|---|---|---|
| Stateful | `<X>Route` | `internal` (called by the entry provider) | `hiltViewModel()`, `collectAsStateWithLifecycle()`, collect `eventFlow`, wire callbacks to ViewModel functions |
| Stateless | `<X>Content` | `private` | Renders `uiState` + callbacks only. Knows nothing about ViewModel, Hilt or Flow |

```kotlin
@Composable
internal fun LanguageRoute(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: LanguageViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

  viewModel.eventFlow.collectInLaunchedEffectWithLifecycle { event ->
    when (event) { /* snackbar, navigate after async work, ... */ }
  }

  LanguageContent(
    uiState = uiState,
    onNavigateBack = onNavigateBack,
    onLanguageItemSelect = viewModel::selectLanguage,
    modifier = modifier,
  )
}

@Composable
private fun LanguageContent(
  uiState: LanguageUiState,
  onNavigateBack: () -> Unit,
  onLanguageItemSelect: (languageId: Long) -> Unit,
  modifier: Modifier = Modifier,
) { /* UI */ }
```

- Collect events with `collectInLaunchedEffectWithLifecycle` (`core:ui` lifecycle package), not a
  raw `LaunchedEffect { eventFlow.collect { } }`.
- Never wrap `collectAsStateWithLifecycle()` in `remember`.
- `@Preview` calls `<X>Content` with fake `uiState`, never `<X>Route`.
- The entry provider (see `navigation.md`) calls only `<X>Route`.
- User-initiated navigation callbacks go straight from `Route` to `Content`, never via the ViewModel.
- Exception: a screen that only wraps a platform view (e.g. `WebViewRoute` with `AndroidView`) and
  has no state to preview may have only a `Route`.

## Component size

- Split large Composables: one Composable, one responsibility.
- Extract into its own function or file only when it is **reused (≥ 2 places)** or is a
  **meaningful standalone UI unit**. One-off, 1–2 line bits (a label, a caption with a link, a
  clickable icon) stay inline in the parent.
- Name components by role ("what it shows / does"). No decorative adjectives (`Playful…`,
  `Fancy…`, `Nice…`) or vague names. One reusable component per file.
- Render an explicit empty state; never return early and leave the layout blank.

## Stability and lazy lists

- `@Immutable` on every UiModel / UiState class; lists are `ImmutableList` / `PersistentList`.
- `LazyColumn` / `LazyGrid` items always have a stable `key = { item.id }`.
- `contentType` is **constant per item type**, so Compose can reuse compositions. Prefer a
  file-private `enum class`. Never include ids or create a new object per item.
  ```kotlin
  contentType = { LanguageContentType.Item }          // Right
  contentType = { item -> "Item_${item.id}" }         // Wrong
  ```

## State

| API | Use when |
|---|---|
| `rememberSaveable` | UI state must survive configuration change |
| `remember(key)` | Caching an expensive computation or keeping an object stable |
| `derivedStateOf` | **Pure UI** state that changes faster than the result you need (scroll, drag). Never for business state |
| `collectAsStateWithLifecycle()` | Collecting a ViewModel Flow |

```kotlin
// Right: pure UI derived state
val listState = rememberLazyListState()
val isScrollToTopVisible by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }

// Wrong: recomputing business state in the UI. Expose it from the ViewModel instead.
val isButtonEnabled by remember { derivedStateOf { uiState.email.isNotEmpty() && !uiState.isLoading } }
// Right: Button(enabled = uiState.isSignInButtonEnabled, ...)
```

When a deep state path is read several times, hoist it into a local `val` at the narrowest scope
that covers all uses.

## Side effects

| API | Use when |
|---|---|
| `LaunchedEffect` | Coroutine / one-shot work when its key changes |
| `DisposableEffect` | Register / unregister listeners or observers. Always `onDispose` |
| `SideEffect` | Publish Compose state to a non-Compose object after a successful recomposition |
| `rememberUpdatedState` | Read the latest lambda inside a long-running effect without restarting it |
| `produceState` | Turn an external async source into Compose `State` |

```kotlin
// Right: key on the state holder, observe the inner value with snapshotFlow
LaunchedEffect(pagerState) {
  snapshotFlow { pagerState.currentPage }.collect { page -> onPageChange(page) }
}

// Wrong: keying on the inner value restarts the effect on every change
LaunchedEffect(pagerState.currentPage) { snapshotFlow { pagerState.currentPage }.collect { } }
```

- No side effects directly in the composition body.
- No complex business logic in Composables.
- Keep recomposition scopes as small as possible.

## Modals and shared widgets

- **Bottom sheet**: Material 3 `ModalBottomSheet`. Never hand-build a scrim + `Box` + animation.
  There is no shared wrapper yet. When a second bottom sheet appears (or shared styling is needed),
  create `AppModalBottomSheet` in `core:ui` `widget/common` and migrate existing usages.
- **Dialog**: reuse `DialogCommon` (`core:ui` `widget/common`). If it lacks a feature, add a slot or
  parameter with a default so existing call sites keep working. Never create a parallel dialog.
- **Toast / message**: use the shared `SnackBarManager` / snackbar host. The ViewModel emits a
  SingleEvent, the Route shows it.
- **Repeated widgets** (card, button, chip, text field, badge, ...) live in `core:ui` `widget/` and
  are reused, not redrawn per feature.
- Modal visibility is driven by state (`selectedItem != null` → show), closed via `onDismiss`
  updating that state.
