---
name: new-feature-module
description: Scaffold a new empty feature module pair (feature/<name>/api + feature/<name>/impl) with Contract, ViewModel, Screen, NavKey and entry provider, wired into the app.
argument-hint: <module-name>
disable-model-invocation: true
---

# New Feature Module

Scaffold an empty feature module for `$ARGUMENTS` following the project conventions. Derive
`<name>` (lowercase package, e.g. `language`) and `<Name>` (PascalCase, e.g. `Language`) from it.

This creates a presentation skeleton only. For a feature with real data, use
`/feature-vertical-slice` instead. To build out the UI afterwards, use `/create-screen-ui`.

Follow `.claude/rules/clean-architecture.md`, `mvi-pattern.md`, `compose-rules.md` and `navigation.md`.

## 1. Gradle modules

`feature/<name>/api/build.gradle.kts`: navigation contract only (`NavKey` + `navigateToX()`).
No Hilt, no Compose, no `core:ui`.

```kotlin
plugins {
  id(libs.plugins.android.feature.api.get().pluginId)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose.feature.<name>.api"
}
```

`feature/<name>/impl/build.gradle.kts`: screens, ViewModel, entry provider.

```kotlin
plugins {
  id(libs.plugins.android.feature.impl.get().pluginId)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose.feature.<name>"
}

dependencies {
  api(projects.feature.<name>.api)

  implementation(projects.core.ui)
  implementation(projects.core.resource)
  implementation(projects.core.common)
  implementation(projects.core.domain)
  implementation(projects.core.model)
}
```

## 2. `<Name>Contract.kt` (UiState only; no StateSaver or SingleEvent yet)

`feature/<name>/impl/src/main/kotlin/com/vmh/mvvmjetpackcompose/feature/<name>/presentation/<name>/<Name>Contract.kt`

```kotlin
package com.vmh.mvvmjetpackcompose.feature.<name>.presentation.<name>

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class <Name>UiState(val isLoading: Boolean) : Parcelable {
  companion object {
    val initial
      get() = <Name>UiState(isLoading = false)
  }
}
```

## 3. `<Name>ViewModel.kt`

Same package as the Contract.

```kotlin
package com.vmh.mvvmjetpackcompose.feature.<name>.presentation.<name>

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Suppress("UnusedPrivateMember") // emitState is unused until the first action is added
@HiltViewModel
internal class <Name>ViewModel @Inject constructor() : ViewModel() {

  private val _uiStateFlow = MutableStateFlow(<Name>UiState.initial)

  val uiStateFlow: StateFlow<<Name>UiState> = _uiStateFlow.asStateFlow()

  private inline fun emitState(f: (<Name>UiState) -> <Name>UiState) = _uiStateFlow.update(f)
}
```

## 4. NavKey (in `api`) and entry provider (in `impl`)

`feature/<name>/api/.../presentation/<name>/navigation/<Name>NavKey.kt`

```kotlin
package com.vmh.mvvmjetpackcompose.feature.<name>.presentation.<name>.navigation

import androidx.navigation3.runtime.NavKey
import com.vmh.mvvmjetpackcompose.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object <Name>NavKey : NavKey

fun Navigator.navigateTo<Name>() = navigate(<Name>NavKey)
```

`feature/<name>/impl/.../presentation/<name>/navigation/<Name>EntryProvider.kt`

```kotlin
package com.vmh.mvvmjetpackcompose.feature.<name>.presentation.<name>.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.vmh.mvvmjetpackcompose.core.navigation.Navigator
import com.vmh.mvvmjetpackcompose.feature.<name>.ui.<name>.<Name>Route

fun EntryProviderScope<NavKey>.<name>Entry(navigator: Navigator) {
  entry<<Name>NavKey> {
    <Name>Route(onNavigateBack = { navigator.goBack() })
  }
}
```

## 5. `<Name>Screen.kt`

`feature/<name>/impl/src/main/kotlin/com/vmh/mvvmjetpackcompose/feature/<name>/ui/<name>/<Name>Screen.kt`

`<Name>Route` (stateful, `internal`) + `<Name>Content` (stateless, `private`) + preview.

```kotlin
package com.vmh.mvvmjetpackcompose.feature.<name>.ui.<name>

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme
import com.vmh.mvvmjetpackcompose.feature.<name>.presentation.<name>.<Name>UiState
import com.vmh.mvvmjetpackcompose.feature.<name>.presentation.<name>.<Name>ViewModel
import com.vmh.mvvmjetpackcompose.ui.widget.common.BackIconButton

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
    modifier = modifier,
  )
}

@Suppress("UnusedParameter") // uiState is unused until the screen gets real content
@Composable
private fun <Name>Content(
  uiState: <Name>UiState,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Scaffold(
    modifier = modifier,
    topBar = {
      BackIconButton(
        modifier = Modifier.padding(start = 6.dp, top = 40.dp),
        onBackClick = onNavigateBack,
      )
    },
    content = { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues = innerPadding)
          .consumeWindowInsets(paddingValues = innerPadding),
        contentAlignment = Alignment.Center,
      ) {
        Text(text = "<Name>") // placeholder; replace with stringResource when building the UI
      }
    },
  )
}

@Preview(showBackground = true)
@Composable
private fun <Name>ContentPreview() {
  MVVMJetpackComposeTheme {
    <Name>Content(
      uiState = <Name>UiState.initial,
      onNavigateBack = {},
    )
  }
}
```

## 6. Register and wire

1. `settings.gradle.kts`, in the feature section:
   ```kotlin
   include(":feature:<name>:api")
   include(":feature:<name>:impl")
   ```
2. `app/build.gradle.kts`, next to the other features:
   ```kotlin
   implementation(projects.feature.<name>.api)
   implementation(projects.feature.<name>.impl)
   ```
3. `app/.../MainActivity.kt`: add **one line** `<name>Entry(navigator)` inside `entryProvider { }`.
4. Callers: the module that opens this screen adds `implementation(projects.feature.<name>.api)`
   and its entry provider calls `navigator::navigateTo<Name>`. Never pass navigation lambdas
   down from `:app`.
5. Tab screen only: add `<Name>NavKey` to `topLevelRoutes` and to `MainTopScreenTopLevelDestination`.
   **A non-tab screen never touches `topLevelRoutes`.**

## Resulting structure

```
feature/<name>/
├── api/
│   ├── build.gradle.kts
│   └── src/main/kotlin/.../feature/<name>/presentation/<name>/navigation/<Name>NavKey.kt
└── impl/
    ├── build.gradle.kts
    └── src/main/kotlin/.../feature/<name>/
        ├── presentation/<name>/
        │   ├── <Name>Contract.kt
        │   ├── <Name>ViewModel.kt
        │   └── navigation/<Name>EntryProvider.kt
        └── ui/<name>/
            └── <Name>Screen.kt
```

## Notes

- No `StateSaver` or `SingleEvent` at scaffold time; add them when needed.
- `onNavigateBack` is a direct lambda, not a `SingleEvent`.
- Finish with `./gradlew :feature:<name>:impl:assembleDebug`, then hand off to the
  `android-quality` agent or run spotless + detekt yourself.
