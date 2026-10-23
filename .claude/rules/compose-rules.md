---
description: Jetpack Compose rules — API design, state management, side-effect APIs. Applied to all Composable files.
globs: feature/**/*.kt, core/ui/**/*.kt
---

# Jetpack Compose Rules

## API Design cho Composable

### Argument Names & modifier

```kotlin
// Đúng — ghi rõ tên argument; modifier là tham số optional ĐẦU TIÊN
@Composable
fun VideoCard(
    video: VideoUiModel,                               // 1. required
    onVideoLikeToggle: (videoId: Video.Id) -> Unit,
    onVideoOpen: (videoId: Video.Id) -> Unit,
    modifier: Modifier = Modifier,                     // 2. modifier
    isLiked: Boolean = false,                          // 3. optional khác
    content: @Composable () -> Unit = {},              // 4. trailing @Composable lambda (nếu có)
)

// Đúng — call site ghi rõ tên
VideoCard(
    video = item,
    onVideoLikeToggle = viewModel::onLikeToggle,
    onVideoOpen = onVideoOpen,
    modifier = Modifier.fillMaxWidth(),
)

// Sai — positional
VideoCard(item, viewModel::onLikeToggle, onVideoOpen, Modifier.fillMaxWidth())
```

**Vị trí `modifier`** — theo [Compose API guidelines](https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-component-api-guidelines.md#modifier-parameter) của Google: `modifier` là **tham số optional đầu tiên**, tức thứ tự luôn là:

1. Tham số required (data, callback bắt buộc).
2. `modifier: Modifier = Modifier`.
3. Các tham số optional khác (`enabled`, `colors`, `viewModel = hiltViewModel()`…).
4. Trailing `@Composable` lambda (`content`) nếu có.

Áp dụng cho mọi composable (reusable widget, screen, item…). Luôn có default `modifier: Modifier = Modifier`, và chỉ apply nó cho **root layout** của composable.

### Màn hình — tách 2 tầng `Route` / `Content`

Mỗi màn trong `feature/<x>/impl` gồm 2 composable, đặt chung file `<X>Screen.kt`:

| Tầng | Tên | Visibility | Trách nhiệm |
|---|---|---|---|
| Stateful | `<X>Route` | `internal` (entry provider gọi) | Nhận `viewModel = hiltViewModel()`, `collectAsStateWithLifecycle()`, thu thập `eventFlow`, nối callback → hàm ViewModel |
| Stateless | `<X>Content` | `private` | Chỉ nhận `uiState` + callback, render UI. **Không** biết ViewModel, Hilt, Flow |

```kotlin
@Composable
internal fun LanguageRoute(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: LanguageViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

  viewModel.eventFlow.collectInLaunchedEffectWithLifecycle { event -> /* snackbar, navigate sau async… */ }

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

- `@Preview` chỉ gọi `<X>Content` với `uiState` giả — **không** preview `<X>Route`.
- Entry provider (xem [`navigation.md`](navigation.md)) chỉ gọi `<X>Route`.
- Callback điều hướng do người dùng bấm (`onNavigateBack`, `onNavigateToX`) đi thẳng từ `Route` xuống `Content`, không vòng qua ViewModel.
- Ngoại lệ: màn chỉ bọc một view nền tảng (vd `WebViewRoute` dùng `AndroidView`) không có state để preview → được phép chỉ có `Route`.

### Xuống dòng theo số lượng parameter

- **1 param**: có thể viết trên 1 dòng.
- **> 1 param**: mỗi param xuống 1 dòng riêng, dấu `)` hoặc `}` cuối cùng xuống dòng mới.

```kotlin
// Đúng — 1 param, 1 dòng
Text(text = title)

// Đúng — > 1 param, mỗi param 1 dòng
Text(
    text = title,
    style = MaterialTheme.typography.bodyMedium,
)

// Sai — > 1 param nhưng viết gộp
Text(text = title, style = MaterialTheme.typography.bodyMedium)
```

Quy tắc này áp dụng cho cả **khai báo** composable lẫn **call site**, và cả function call thông thường trong ViewModel/Repository.

### Callback Naming

`on + NOUN + VERB`:

| Sai | Đúng |
|---|---|
| `onToggleLike` | `onVideoLikeToggle` |
| `onClick` | `onVideoOpen` / `onProfileAvatarClick` |
| `onClicked` | `onSearchSubmit` |
| `onClickDeleteAccountButton` | `onDeleteAccountClick` |

### Lambda Type — luôn ghi tên parameter

```kotlin
// Đúng
val onVideoLikeToggle: (videoId: Video.Id) -> Unit

// Sai
val onVideoLikeToggle: (Video.Id) -> Unit
```

### Callback Payload

```kotlin
// Đúng — parent biết được item nào
onWatchHistoryItemOpen: (itemId: WatchHistory.Id) -> Unit

// Sai — parent không biết item nào
onWatchHistoryItemOpen: () -> Unit
```

### Large Composables

- Extract composable lớn thành sub-components riêng.
- Một composable = một responsibility.
- Emit empty state UI rõ ràng, không early return mà bỏ trống layout.

### Tách vừa đủ — đừng chẻ quá nhỏ (ngược lại của Large Composables)

- Chỉ tách một Composable ra hàm/file riêng khi nó **được tái sử dụng (≥ 2 nơi)** hoặc là **một đơn vị UI độc lập có ý nghĩa**.
- Mẩu UI **dùng một lần, chỉ 1–2 dòng** (một nhãn, một dòng chú thích kèm link, một icon bấm…) → **viết thẳng trong parent**, không tạo composable/ file riêng.
- **Đặt tên theo vai trò/chức năng cụ thể** của component (mô tả nó *hiển thị/làm gì*), **tránh** tính từ trang trí (`Playful…`, `Fancy…`, `Nice…`) và tên chung chung vô nghĩa. Mỗi component tái dùng = **một file** riêng.

### Stable Types

- Dùng `@Immutable` cho tất cả UiModel/UiState class.
- `LazyList` / `LazyGrid`: luôn có `key { item.id }` ổn định.
- `contentType` phải là **hằng theo loại item** — cùng loại layout thì cùng giá trị, để Compose tái dùng composition giữa các item cùng loại. Dùng `enum`, `object` hoặc `String` constant đều được; **không** chứa id/dữ liệu động, **không** tạo object mới mỗi lần.
  ```kotlin
  // Đúng
  contentType = { "SearchResultItem" }
  contentType = { item -> item.type }                 // enum
  // Sai — mỗi item một giá trị, mất tác dụng tái dùng
  contentType = { item -> "SearchResultItem_${item.id}" }
  ```

## State Management

### Khi nào dùng gì

| API | Dùng khi |
|---|---|
| `rememberSaveable` | State cần sống qua configuration change |
| `remember(key)` | Cache computation nặng hoặc object cần giữ ổn định |
| `derivedStateOf` | State **thuần UI** đổi nhanh hơn kết quả cần (scroll, drag…) — tránh recomposition thừa. **Không** dùng để tính state nghiệp vụ |
| `collectAsStateWithLifecycle()` | Thu thập Flow từ ViewModel |

```kotlin
// Đúng
val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

// Đúng — derivedStateOf cho state thuần UI: index đổi liên tục, kết quả chỉ đổi khi qua ngưỡng
val listState = rememberLazyListState()
val isScrollToTopVisible by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }

// Sai — tính lại state nghiệp vụ trong UI; ViewModel phải phơi sẵn trong UiState (xem mvi-pattern.md)
val isButtonEnabled by remember { derivedStateOf { uiState.email.isNotEmpty() && !uiState.isLoading } }
// → Đúng: Button(enabled = uiState.isSignInButtonEnabled, ...)

// Sai — không bọc collectAsStateWithLifecycle bằng remember
val uiState by remember { viewModel.uiStateFlow.collectAsStateWithLifecycle() }
```

### Local Val — Tránh lặp truy cập state

```kotlin
// Đúng — tạo local val cho value dùng nhiều nơi
val visibleFeatures = uiState.configState.visibleScreenFeatureTypes
// Sau đó dùng visibleFeatures trong nhiều child composable

// Sai — lặp lại truy cập chuỗi dài
if (uiState.configState.visibleScreenFeatureTypes.contains(Feature.Like)) { ... }
// ... dùng lại lần nữa ...
if (uiState.configState.visibleScreenFeatureTypes.contains(Feature.Share)) { ... }
```

- Đặt local val ở scope đủ rộng: nếu dùng trong nhiều child thì đặt ở outer scope (`Surface`/`Box`); nếu chỉ dùng trong một page thì đặt trong page scope.

## Side-Effect APIs

### LaunchedEffect + snapshotFlow

```kotlin
// Đúng — key là outer state holder, inner state observe bằng snapshotFlow
LaunchedEffect(pagerState) {
    snapshotFlow { pagerState.currentPage }.collect { page ->
        viewModel.onPageChanged(page)
    }
}

// Sai — key là inner state → LaunchedEffect restart mỗi lần thay đổi
LaunchedEffect(pagerState.currentPage) {
    snapshotFlow { pagerState.currentPage }.collect { ... }
}
```

### DisposableEffect

```kotlin
DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { ... }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
}
```

- Dùng cho: register/unregister listener, lifecycle observer, callback cần cleanup.
- **Không quên `onDispose`**.

### rememberUpdatedState

```kotlin
// Dùng khi muốn giữ lambda mới nhất trong long-running effect mà không restart effect
val currentOnEvent by rememberUpdatedState(onEvent)
LaunchedEffect(Unit) {
    externalFlow.collect { currentOnEvent(it) }
}
```

### SideEffect vs LaunchedEffect

| API | Dùng khi |
|---|---|
| `SideEffect` | Publish Compose state ra object bên ngoài sau recomposition thành công |
| `LaunchedEffect` | Coroutine / one-shot logic khi key thay đổi |
| `produceState` | Convert external async source thành Compose `State` |

- **Không** nhồi business logic phức tạp vào composable.
- **Không** tạo side effect trực tiếp trong composition body.
- Giữ recomposition scope nhỏ nhất có thể.

## Modal (bottom sheet / dialog) — dùng & tái dùng component chuẩn

- **Bottom sheet** → dùng Material 3 `ModalBottomSheet`. **Không** tự dựng scrim + `Box` + animation thủ công.
  - Project **chưa có** wrapper chung. Khi bottom sheet thứ 2 xuất hiện (hoặc cần style chung: shape, màu, drag handle) → tạo `AppModalBottomSheet` trong `core:ui/widget/common` rồi chuyển các chỗ dùng sang wrapper đó.
  ```kotlin
  content?.let { state ->
    ModalBottomSheet(onDismissRequest = onDismiss) { /* nội dung sheet */ }
  }
  ```
- **Dialog** → **tái dùng dialog chung** của project (vd `DialogCommon`). Nếu thiếu tính năng (ô nhập, màu theme) → **cập nhật dialog chung** thêm slot/tham số (giữ default để không vỡ chỗ dùng cũ), rồi tái dùng. **Không** tự dựng dialog mới trùng chức năng.
- **Toast/thông báo** → dùng `SnackbarManager`/host chung sẵn có; ViewModel emit SingleEvent → screen hiển thị. Không tự vẽ toast overlay nếu đã có cơ chế chung.
- **Widget lặp lại** (card, button, chip, text field, badge…) → để ở `core:ui/widget`, tái dùng; không copy-vẽ lại trong từng feature.
- Trạng thái hiển thị modal do state điều khiển (`deposit != null` → hiện sheet), đóng qua `onDismiss` cập nhật state.
