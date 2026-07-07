---
description: Jetpack Compose rules — API design, state management, side-effect APIs. Applied to all Composable files.
globs: feature/**/*.kt, core/ui/**/*.kt
---

# Jetpack Compose Rules

## API Design cho Composable

### Argument Names & modifier

```kotlin
// Đúng — ghi rõ tên argument; modifier là parameter cuối
@Composable
fun VideoCard(
    video: VideoUiModel,
    onVideoLikeToggle: (videoId: Video.Id) -> Unit,
    onVideoOpen: (videoId: Video.Id) -> Unit,
    modifier: Modifier = Modifier,
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

**Vị trí `modifier`**: luôn là **tham số cuối** (sau required params, trước trailing lambda nếu có) — cho mọi composable (reusable widget, screen, item…). Với Route: sau required params, trước `viewModel`. Luôn có default `modifier: Modifier = Modifier`.

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
- `contentType` phải ổn định — dùng `enum` hoặc `object`, không dùng `String` tuỳ tiện.

## State Management

### Khi nào dùng gì

| API | Dùng khi |
|---|---|
| `rememberSaveable` | State cần sống qua configuration change |
| `remember(key)` | Cache computation nặng hoặc object cần giữ ổn định |
| `derivedStateOf` | Computed state phụ thuộc state khác — tránh recomposition thừa |
| `collectAsStateWithLifecycle()` | Thu thập Flow từ ViewModel |

```kotlin
// Đúng
val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
val isButtonEnabled by remember { derivedStateOf { uiState.email.isNotEmpty() && !uiState.isLoading } }

// Sai — không bọc collectAsStateWithLifecycle bằng remember
val uiState by remember { viewModel.uiStateFlow.collectAsStateWithLifecycle() }

// Sai — không dùng remember(key1 = ...) style cũ nếu không cần
val result by remember(key1 = someKey) { ... }
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

- **Bottom sheet** → dùng Material 3 `ModalBottomSheet` (hoặc wrapper chung của project như `AppModalBottomSheet` ở `core:ui`). **Không** tự dựng scrim + `Box` + animation thủ công.
  ```kotlin
  content?.let { state ->
    AppModalBottomSheet(onDismissRequest = onDismiss) { /* nội dung sheet */ }
  }
  ```
- **Dialog** → **tái dùng dialog chung** của project (vd `DialogCommon`). Nếu thiếu tính năng (ô nhập, màu theme) → **cập nhật dialog chung** thêm slot/tham số (giữ default để không vỡ chỗ dùng cũ), rồi tái dùng. **Không** tự dựng dialog mới trùng chức năng.
- **Toast/thông báo** → dùng `SnackbarManager`/host chung sẵn có; ViewModel emit SingleEvent → screen hiển thị. Không tự vẽ toast overlay nếu đã có cơ chế chung.
- **Widget lặp lại** (card, button, chip, text field, badge…) → để ở `core:ui/widget`, tái dùng; không copy-vẽ lại trong từng feature.
- Trạng thái hiển thị modal do state điều khiển (`deposit != null` → hiện sheet), đóng qua `onDismiss` cập nhật state.
