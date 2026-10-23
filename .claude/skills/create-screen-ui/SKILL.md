---
name: create-screen-ui
description: Build the full UI layer (LCE UiState, ViewModel stubs, strings, components, Route/Content screen) for an existing feature module created by /new-feature-module, from a design image or description.
argument-hint: <ScreenName>
disable-model-invocation: true
---

# Create Screen UI

Implement the UI layer for the existing feature module matching `$ARGUMENTS`
(e.g. `Language` → `feature/language`). `<name>` is the lowercase package, `<Name>` PascalCase.

Prerequisites: the module was scaffolded with `/new-feature-module`, and there is a design image or
a description of the UI. If the screen needs real data, build the data layer
with `.claude/rules/data-layer.md`; don't put mock data in the ViewModel.

Follow `.claude/rules/mvi-pattern.md`, `compose-rules.md`, `theming-strings-resources.md` and
`detekt-hygiene.md`.

## 1. Read the skeleton

- `impl/.../presentation/<name>/<Name>Contract.kt`
- `impl/.../presentation/<name>/<Name>ViewModel.kt`
- `impl/.../ui/<name>/<Name>Screen.kt`

## 2. Contract: LCE sealed interface

Replace the scaffold `data class` with an `@Immutable` LCE sealed interface:

```kotlin
package com.vmh.mvvmjetpackcompose.feature.<name>.presentation.<name>

import androidx.compose.runtime.Immutable
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import kotlinx.collections.immutable.PersistentList

@Immutable
sealed interface <Name>UiState {
  companion object {
    val initial: <Name>UiState get() = Loading
  }

  @Immutable
  data object Loading : <Name>UiState

  @Immutable
  data class Content(
    val items: PersistentList<Item>,
    // more fields from the design, e.g. val isSaveButtonEnabled: Boolean,
  ) : <Name>UiState {
    @Immutable
    data class Item(
      val id: String,
      val title: String,
      // ... more fields from the design
      val isSelected: Boolean, // if the list has selection
    )
  }

  @Immutable
  data class Error(val error: AppError) : <Name>UiState
}
```

- `@Immutable` on the sealed interface **and** every subtype; no `@Parcelize`.
- UI items are nested in `Content`; selection is `isSelected` on the item, not `selectedItemId`.
- Lists are `PersistentList` (needs `implementation(libs.kotlinx.collections.immutable)` in the
  module's `build.gradle.kts`).

## 3. ViewModel

```kotlin
@Suppress("EmptyFunctionBlock", "UnusedParameter") // action stubs until the data layer exists
@HiltViewModel
internal class <Name>ViewModel @Inject constructor() : ViewModel() {

  private val _uiStateFlow = MutableStateFlow(<Name>UiState.initial)

  val uiStateFlow: StateFlow<<Name>UiState> = _uiStateFlow.asStateFlow()

  private inline fun emitState(f: (<Name>UiState) -> <Name>UiState) = _uiStateFlow.update(f)

  init {
    load<Name>()
  }

  private fun load<Name>() {
    // Replace with a repository call once the data layer exists.
    emitState { <Name>UiState.Content(items = persistentListOf()) }
  }

  fun on<Action>(itemId: String) {
  }
}
```

## 4. Strings

`core/resource/src/main/res/values/strings.xml`, in a new section:

```xml
<!-- <Name> -->
<string name="<name>_title">Display title</string>
<string name="<name>_empty">Nothing here yet</string>
```

Every visible text comes from `stringResource()`.

## 5. Components

One file per reusable component in `ui/<name>/component/`. Only extract what is reused or a
standalone unit (see `compose-rules.md`).

```kotlin
@Composable
internal fun <Name>Item(
  title: String,
  isSelected: Boolean,
  on<Name>ItemClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = on<Name>ItemClick)
      .padding(horizontal = 16.dp, vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = title,
      modifier = Modifier.weight(1f),
      color = MaterialTheme.colorScheme.onSurface,
    )
    if (isSelected) {
      Icon(
        imageVector = ImageVector.vectorResource(id = CoreResourceR.drawable.ic_check),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun <Name>ItemPreview() {
  MVVMJetpackComposeTheme {
    <Name>Item(
      title = "Preview",
      isSelected = true,
      on<Name>ItemClick = {},
    )
  }
}
```

- `modifier` is the first optional parameter (after required ones).
- Colors with a Material role come from `MaterialTheme.colorScheme.*`; others from
  `MVVMJetPackComposeColors` (see `theming-strings-resources.md`).
- Every component has at least one `@Preview`.

## 6. Screen

```kotlin
@Composable
internal fun <Name>Route(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: <Name>ViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

  <Name>Content(
    uiState = uiState,
    onNavigateBack = onNavigateBack,
    on<Action> = viewModel::on<Action>,
    modifier = modifier,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <Name>Content(
  uiState: <Name>UiState,
  onNavigateBack: () -> Unit,
  on<Action>: (itemId: String) -> Unit,
  modifier: Modifier = Modifier,
) {
  Scaffold(
    modifier = modifier,
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = stringResource(id = CoreResourceR.string.<name>_title),
            style = MVVMJetpackComposeTheme.typography.textStyleXLargeBold,
          )
        },
        navigationIcon = { BackIconButton(onBackClick = onNavigateBack) },
      )
    },
    content = { innerPadding ->
      val contentModifier = Modifier
        .fillMaxSize()
        .padding(paddingValues = innerPadding)
        .consumeWindowInsets(paddingValues = innerPadding)

      when (uiState) {
        <Name>UiState.Loading -> Box(
          modifier = contentModifier,
          contentAlignment = Alignment.Center,
        ) {
          LoadingIndicator()
        }

        is <Name>UiState.Content -> if (uiState.items.isEmpty()) {
          Box(
            modifier = contentModifier,
            contentAlignment = Alignment.Center,
          ) {
            Text(text = stringResource(id = CoreResourceR.string.<name>_empty))
          }
        } else {
          LazyColumn(modifier = contentModifier) {
            items(
              items = uiState.items,
              key = { item -> item.id },
              contentType = { <Name>ContentType.Item },
            ) { item ->
              <Name>Item(
                title = item.title,
                isSelected = item.isSelected,
                on<Name>ItemClick = { on<Action>(item.id) },
              )
            }
          }
        }

        is <Name>UiState.Error -> CommonAppErrorContent(
          modifier = contentModifier,
          appError = uiState.error,
          getAppErrorMessage = DefaultGetAppErrorMessageForInline,
        )
      }
    },
  )
}

private enum class <Name>ContentType { Item }

@Preview(showBackground = true)
@Composable
private fun <Name>ContentPreview() {
  MVVMJetpackComposeTheme {
    <Name>Content(
      uiState = <Name>UiState.Content(
        items = persistentListOf(
          <Name>UiState.Content.Item(id = "1", title = "First", isSelected = true),
          <Name>UiState.Content.Item(id = "2", title = "Second", isSelected = false),
        ),
      ),
      onNavigateBack = {},
      on<Action> = {},
    )
  }
}
```

Imports used above: `LoadingIndicator`, `DefaultGetAppErrorMessageForInline`
(`com.vmh.mvvmjetpackcompose.core.ui.common`), `BackIconButton`, `CommonAppErrorContent`
(`com.vmh.mvvmjetpackcompose.ui.widget.common`), `CoreResourceR`
(`com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR`).

## Convention summary

| Element | Rule |
|---|---|
| UiState | `@Immutable` LCE sealed interface; `initial` = `Loading` |
| UI item | Nested in `Content`, `isSelected` instead of `selectedItemId` |
| Lists | `PersistentList`, built with `core.common.extension.ImmutableList` helpers |
| Error | Holds `AppError`; rendered with `CommonAppErrorContent` + `DefaultGetAppErrorMessageForInline` |
| Loading | `Box(contentAlignment = Alignment.Center) { LoadingIndicator() }` |
| Empty | Explicit empty-state text from resources |
| Screen | `<Name>Route` (`internal`) + `<Name>Content` (`private`); previews call `Content` |
| `contentType` | File-private `enum class` |
| `TopAppBar` | Inline in `Scaffold.topBar`, no separate composable |
| Colors | Material roles via `MaterialTheme.colorScheme`, others via `MVVMJetPackComposeColors` |

## Finish

Run `./gradlew :feature:<name>:impl:assembleDebug`, then the `android-quality` agent (or spotless +
detekt yourself) until green.
