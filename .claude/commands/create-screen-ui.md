# Create Screen UI

Implement the full UI layer for an existing feature module — from UiState definition through composables to string resources.

## Usage

```
/create-screen-ui <ScreenName>
```

Example: `/create-screen-ui Language` → creates UI for `feature/language`

---

## Prerequisites

- Feature module đã được tạo bằng `/new-feature-module`
- Có ảnh thiết kế hoặc mô tả UI

---

## Steps

### 1. Đọc file hiện tại

Đọc các file skeleton được tạo bởi `/new-feature-module`:
- `presentation/<name>/<Name>Contract.kt`
- `presentation/<name>/<Name>ViewModel.kt`
- `ui/<name>/<Name>Screen.kt`

---

### 2. Cập nhật `<Name>Contract.kt` — LCE Sealed Interface

Xoá `data class` cũ, thay bằng `@Immutable sealed interface` theo LCE pattern:

```kotlin
package com.vmh.mvvmjetpackcompose.feature.<name>.presentation.<name>

import androidx.compose.runtime.Immutable
import com.vmh.mvvmjetpackcompose.core.model.error.AppError

@Immutable
sealed interface <Name>UiState {
  companion object {
    val initial: <Name>UiState get() = Loading
  }

  @Immutable
  data object Loading : <Name>UiState

  @Immutable
  data class Content(
    val items: List<Item>,
    // thêm các field theo thiết kế, ví dụ:
    // val isSaveButtonEnabled: Boolean,
  ) : <Name>UiState {
    @Immutable
    data class Item(
      val id: String,
      // ... fields theo thiết kế
      val isSelected: Boolean,  // nếu list có selection
    )

    companion object {
      val initial
        get() = Content(
          items = listOf(/* hardcoded mock data */),
        )
    }
  }

  @Immutable
  data class Error(val error: AppError) : <Name>UiState
}
```

**Rules:**
- `@Immutable` bắt buộc trên cả sealed interface VÀ từng subtype
- `companion object { val initial get() = Loading }` ở sealed interface level
- Item/UiModel đặt **lồng bên trong `Content`**, không phải top-level
- `isSelected` đặt trong Item — không dùng `selectedItemId` ở outer state
- `Error` luôn chứa `AppError` object, không dùng `String`
- Không dùng `@Parcelize` cho sealed interface

---

### 3. Cập nhật `<Name>ViewModel.kt`

```kotlin
package com.vmh.mvvmjetpackcompose.feature.<name>.presentation.<name>

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Suppress("EmptyFunctionBlock", "UnusedParameter", "UnusedPrivateMember")
@HiltViewModel
internal class <Name>ViewModel @Inject constructor() : ViewModel() {

  private val _uiStateFlow = MutableStateFlow(<Name>UiState.initial)

  val uiStateFlow: StateFlow<<Name>UiState> = _uiStateFlow.asStateFlow()

  private inline fun emitState(f: (<Name>UiState) -> <Name>UiState) = _uiStateFlow.update(f)

  init {
    load<Name>()
  }

  private fun load<Name>() {
    emitState { <Name>UiState.Content.initial }
  }

  // User action stubs — thêm logic sau khi có data source
  fun on<Action>() {
  }
}
```

**Rules:**
- `_uiStateFlow` khởi tạo bằng `<Name>UiState.initial` (= `Loading`)
- `init` block gọi `load<Name>()` để chuyển sang `Content`
- Các hàm action là stub rỗng ở bước này — chưa cần logic

---

### 4. Thêm strings vào `strings.xml`

Path: `core/resource/src/main/res/values/strings.xml`

Thêm section mới cho screen:

```xml
<!-- <Name> -->
<string name="<name>_title"><Display Title></string>
<!-- Thêm các string khác theo thiết kế -->
```

**Rule:** Mọi text hiển thị trong Composable phải đến từ `stringResource()`, không hardcode.

---

### 5. Tạo `component/` package

Path: `ui/<name>/component/`

Tạo một file `.kt` cho mỗi thành phần UI tái sử dụng (item trong list, card, v.v.).

#### Template: `<Name>Item.kt`

```kotlin
package com.vmh.mvvmjetpackcompose.feature.<name>.ui.<name>.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme

@Composable
internal fun <Name>Item(
  modifier: Modifier = Modifier,      // FIRST — convention cho reusable component
  // ... data params
  isSelected: Boolean,
  on<Name>ItemClick: () -> Unit,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = on<Name>ItemClick)
      .padding(horizontal = 16.dp, vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    // content
    if (isSelected) {
      Icon(
        imageVector = ImageVector.vectorResource(id = CoreResourceR.drawable.ic_check),
        contentDescription = null,
        tint = MVVMJetPackComposeColors.Neutral10,
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun <Name>ItemSelectedPreview() {
  MVVMJetpackComposeTheme {
    <Name>Item(
      isSelected = true,
      on<Name>ItemClick = {},
    )
  }
}

@Preview(showBackground = true)
@Composable
private fun <Name>ItemUnselectedPreview() {
  MVVMJetpackComposeTheme {
    <Name>Item(
      isSelected = false,
      on<Name>ItemClick = {},
    )
  }
}
```

**Rules:**
- `modifier` đặt **ĐẦU TIÊN** cho reusable component composable
- Màu sắc dùng `MVVMJetPackComposeColors.*` — không dùng `MaterialTheme.colorScheme.*`
  - Primary text: `Neutral10`
  - Secondary text: `Neutral20`
  - Check icon tint: `Neutral10`
- Callback đặt tên `on + Noun + Verb`: `on<Name>ItemClick`, không phải `onClick`
- Mỗi component có ít nhất 1 `@Preview`

---

### 6. Cập nhật `<Name>Screen.kt`

```kotlin
package com.vmh.mvvmjetpackcompose.feature.<name>.ui.<name>

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.common.DefaultGetAppErrorMessageForInline
import com.vmh.mvvmjetpackcompose.core.ui.common.LoadingIndicator
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme
import com.vmh.mvvmjetpackcompose.feature.<name>.presentation.<name>.<Name>UiState
import com.vmh.mvvmjetpackcompose.feature.<name>.presentation.<name>.<Name>ViewModel
import com.vmh.mvvmjetpackcompose.feature.<name>.ui.<name>.component.<Name>Item
import com.vmh.mvvmjetpackcompose.ui.widget.common.BackIconButton
import com.vmh.mvvmjetpackcompose.ui.widget.common.CommonAppErrorContent

// Route — required params trước, modifier + viewModel sau
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
    on<Action> = viewModel::<onAction>,
    modifier = modifier,
  )
}

// Screen — modifier CUỐI (screen composable convention)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun <Name>Screen(
  uiState: <Name>UiState,
  onNavigateBack: () -> Unit,
  on<Action>: (<param>) -> Unit,
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
        navigationIcon = {
          BackIconButton(onBackClick = onNavigateBack)
        },
        // actions nếu cần (Save button, v.v.)
      )
    },
    content = { innerPadding ->
      when (uiState) {
        <Name>UiState.Loading -> {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(paddingValues = innerPadding)
              .consumeWindowInsets(paddingValues = innerPadding),
            contentAlignment = Alignment.Center,
          ) {
            LoadingIndicator()
          }
        }
        is <Name>UiState.Content -> {
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(paddingValues = innerPadding)
              .consumeWindowInsets(paddingValues = innerPadding),
          ) {
            items(
              items = uiState.items,
              key = { item -> item.id },
              contentType = { <Name>ContentType.Item },   // dùng enum, không dùng String
            ) { item ->
              <Name>Item(
                // pass data fields
                isSelected = item.isSelected,
                on<Name>ItemClick = { on<Action>(item.id) },
              )
            }
          }
        }
        is <Name>UiState.Error -> {
          CommonAppErrorContent(
            modifier = Modifier
              .fillMaxSize()
              .padding(paddingValues = innerPadding)
              .consumeWindowInsets(paddingValues = innerPadding),
            appError = uiState.error,
            getAppErrorMessage = DefaultGetAppErrorMessageForInline,
          )
        }
      }
    },
  )
}

private enum class <Name>ContentType { Item }

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun <Name>ScreenPreview() {
  MVVMJetpackComposeTheme {
    <Name>Screen(
      uiState = <Name>UiState.Content.initial,
      onNavigateBack = {},
      on<Action> = {},
    )
  }
}
```

---

## Convention Summary

| Element | Rule |
|---|---|
| `UiState` | `@Immutable sealed interface` với LCE (Loading / Content / Error) |
| `UiState.initial` | Trả về `Loading` — định nghĩa ở sealed interface level |
| `UIItem` | Lồng bên trong `Content`, có `isSelected` thay vì `selectedItemId` |
| `Error` | Chứa `AppError` object |
| `@Immutable` | Bắt buộc trên sealed interface, `data object`, và mỗi `data class` subtype |
| `modifier` vị trí | **FIRST** cho reusable component · **LAST** cho Screen · sau required params cho Route |
| Màu sắc | `MVVMJetPackComposeColors.*` — không dùng `MaterialTheme.colorScheme.*` |
| Strings | `stringResource()` từ `strings.xml` — không hardcode text |
| `contentType` | `private enum class` ở file level — không dùng `String` hay `Int` |
| `TopAppBar` | Dùng trực tiếp trong `Scaffold.topBar` — không tạo composable riêng |
| Error UI | `CommonAppErrorContent` với `DefaultGetAppErrorMessageForInline` |
| Loading UI | `Box(Alignment.Center) { LoadingIndicator() }` |

---

## File Structure

```
feature/<name>/ui/<name>/
├── <Name>Screen.kt
└── component/
    └── <Name>Item.kt        # (1 file per reusable component)
```
