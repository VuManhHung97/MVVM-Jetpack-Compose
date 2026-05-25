# New Feature Module

Create a new Android feature module following the project's conventions.

## Usage
```
/new-feature-module <module-name>
```

## Steps

You will create a complete feature module scaffold. Replace `<name>` with the module name provided (e.g. `language` → `Language`, package `language`).

### 1. Create `feature/<name>/build.gradle.kts`

```kotlin
plugins {
  id(libs.plugins.android.feature.get().pluginId)
}

android {
  namespace = "com.vmh.mvvmjetpackcompose.feature.<name>"
}

dependencies {
  implementation(projects.core.ui)
  implementation(projects.core.resource)
  implementation(projects.core.common)
  implementation(projects.core.domain)
  implementation(projects.core.model)
}
```

### 2. Create `<Name>Contract.kt` (UiState only — no StateSaver, no SingleEvent at this stage)

Path: `feature/<name>/src/main/kotlin/com/vmh/mvvmjetpackcompose/feature/<name>/presentation/<name>/<Name>Contract.kt`

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

### 3. Create `<Name>ViewModel.kt`

Path: `feature/<name>/src/main/kotlin/com/vmh/mvvmjetpackcompose/feature/<name>/presentation/<name>/<Name>ViewModel.kt`

```kotlin
package com.vmh.mvvmjetpackcompose.feature.<name>.presentation.<name>

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Suppress("UnusedPrivateMember")
@HiltViewModel
internal class <Name>ViewModel @Inject constructor() : ViewModel() {

  private val _uiStateFlow = MutableStateFlow(<Name>UiState.initial)

  val uiStateFlow: StateFlow<<Name>UiState> = _uiStateFlow.asStateFlow()

  private inline fun emitState(f: (<Name>UiState) -> <Name>UiState) = _uiStateFlow.update(f)
}
```

### 4. Create `navigation/navigation.kt`

Path: `feature/<name>/src/main/kotlin/com/vmh/mvvmjetpackcompose/feature/<name>/presentation/<name>/navigation/navigation.kt`

```kotlin
package com.vmh.mvvmjetpackcompose.feature.<name>.presentation.<name>.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.vmh.mvvmjetpackcompose.feature.<name>.ui.<name>.<Name>Route

const val <Name>RoutePattern = "<name>_route"

fun NavController.navigateTo<Name>Screen(navOptions: NavOptions? = null) = navigate(
  route = <Name>RoutePattern,
  navOptions = navOptions,
)

fun NavGraphBuilder.<name>Screen(onNavigateBack: () -> Unit) {
  composable(route = <Name>RoutePattern) {
    <Name>Route(onNavigateBack = onNavigateBack)
  }
}
```

### 5. Create `<Name>Screen.kt`

Path: `feature/<name>/src/main/kotlin/com/vmh/mvvmjetpackcompose/feature/<name>/ui/<name>/<Name>Screen.kt`

- `<Name>Route`: collect uiState, truyền `onNavigateBack` lambda trực tiếp xuống `<Name>Screen`
- `<Name>Screen`: stateless UI với `Scaffold` + `BackIconButton`
- Preview function ở cuối file

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
  <Name>Screen(
    uiState = uiState,
    onNavigateBack = onNavigateBack,
    modifier = modifier,
  )
}

@Suppress("UnusedParameter")
@Composable
internal fun <Name>Screen(
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
        Text(text = "<Name> Screen")
      }
    },
  )
}

@Preview(showBackground = true)
@Composable
private fun <Name>ScreenPreview() {
  MVVMJetpackComposeTheme {
    <Name>Screen(
      uiState = <Name>UiState.initial,
      onNavigateBack = {},
    )
  }
}
```

### 6. Register in `settings.gradle.kts`

Thêm vào phần Feature modules:
```kotlin
include(":feature:<name>")
```

### 7. Add dependency in `app/build.gradle.kts`

Thêm cùng nhóm với các feature module khác:
```kotlin
implementation(projects.feature.<name>)
```

### 8. Wire into `app/MainActivity.kt`

Thêm import:
```kotlin
import com.vmh.mvvmjetpackcompose.feature.<name>.presentation.<name>.navigation.<name>Screen
import com.vmh.mvvmjetpackcompose.feature.<name>.presentation.<name>.navigation.navigateTo<Name>Screen
```

Thêm vào NavHost bên trong `MVVMJetpackComposeApp`:
```kotlin
<name>Screen(
  onNavigateBack = navController::popBackStack,
)
```

---

## Directory Structure

```
feature/<name>/
├── build.gradle.kts
└── src/main/kotlin/com/vmh/mvvmjetpackcompose/feature/<name>/
    ├── presentation/<name>/
    │   ├── <Name>Contract.kt
    │   ├── <Name>ViewModel.kt
    │   └── navigation/
    │       └── navigation.kt
    └── ui/<name>/
        └── <Name>Screen.kt
```

---

## Rules

- Không tạo `StateSaver` và `SingleEvent` ở bước khởi tạo — chỉ thêm khi cần
- `<Name>Screen` nhận `onNavigateBack` là lambda trực tiếp, không qua `SingleEvent`
- `ViewModel` visibility: `internal`, annotate `@Suppress("UnusedPrivateMember")` cho `emitState`
- `<Name>Screen` annotate `@Suppress("UnusedParameter")` cho `uiState` ở bước khởi tạo
- Tuân thủ `clean-architecture.md`, `mvi-pattern.md`, `compose-rules.md`
