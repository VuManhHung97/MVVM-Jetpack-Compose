---
description: Navigation Compose và lifecycle pitfalls — tránh duplicate collectors, ViewModel leak, và nav args serialization.
globs: feature/**/*.kt, app/**/*.kt
---

# Navigation & Lifecycle Rules

## Nav Graph Structure

- Mỗi feature module có nav graph riêng, wired vào root `NavHost` trong `MainActivity`.
- Dùng `NavTypeContainer` để truyền complex object giữa destinations.

```kotlin
// Đăng ký MoshiNavType cho argument phức tạp
@Immutable
internal class NavTypeContainer @Inject constructor(
    internal val webViewArgsNavType: MoshiNavType<WebViewArgs>
)

// MainActivity inject NavTypeContainer và pass vào NavHost
```

## Complex Nav Args — NavTypeContainer

```kotlin
// Định nghĩa args class
@Parcelize
data class WebViewArgs(val url: String, val title: String) : Parcelable

// Đăng ký trong NavTypeContainer
val webViewArgsNavType: MoshiNavType<WebViewArgs>

// Dùng trong NavHost
composable(
    route = Screen.WebView.route,
    arguments = listOf(navArgument("args") { type = navTypeContainer.webViewArgsNavType })
) { backStackEntry ->
    val args = backStackEntry.arguments?.get("args") as WebViewArgs
    WebViewScreen(args = args)
}
```

## Lifecycle Pitfalls

### ViewModel Scope vs NavBackStackEntry Scope

```kotlin
// Cẩn thận — VM scope phụ thuộc BackStackEntry scope
// Khi popUpTo(route, inclusive = true): VM của route đó bị destroy
// Khi popUpTo(route, inclusive = false): VM của route đó vẫn sống

// Luôn kiểm tra: sau navigation, VM cũ có bị destroy không?
// Nếu không → có thể bị duplicate Flow collector
```

### launchSingleTop / restoreState

```kotlin
navController.navigate(Screen.Home.route) {
    popUpTo(navController.graph.startDestinationId) { saveState = true }
    launchSingleTop = true
    restoreState = true  // Cẩn thận: restoreState không phù hợp có thể giữ screen cũ
}
```

- `restoreState = true` chỉ dùng khi muốn khôi phục state của destination đã lưu.
- Nếu không cần restore state, bỏ `restoreState` để tránh giữ màn hình/ViewModel cũ ngoài ý muốn.

### Duplicate Flow Collection sau Navigation

Nguyên nhân thường gặp:
1. ViewModel không bị destroy sau `popUpTo` → Flow collector cũ vẫn còn
2. `collectAsStateWithLifecycle()` ở nhiều BackStackEntry scope cùng subscribe 1 Flow
3. Graph scope ViewModel bị share giữa các destination không mong muốn

Cách phát hiện và fix:
```kotlin
// Luôn dùng lifecycleOwner scope phù hợp khi collect
viewLifecycleOwner.lifecycleScope.launch {
    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.eventFlow.collect { ... }
    }
}

// Trong Compose: collectAsStateWithLifecycle() tự xử lý lifecycle
val state by viewModel.uiStateFlow.collectAsStateWithLifecycle()
```

## Single Event Collection

```kotlin
// Đúng — LaunchedEffect với key là ViewModel, không phải Unit nếu có thể leak
LaunchedEffect(viewModel) {
    viewModel.eventFlow.collect { event ->
        when (event) {
            is MyEvent.Navigate -> navController.navigate(event.route)
            is MyEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
        }
    }
}
```

- Key của `LaunchedEffect` cho event collection phải stable (ViewModel instance là stable trong Compose).
- Không dùng `LaunchedEffect(Unit)` cho event collection nếu có thể bị cancel/restart không mong muốn.

## Network Interceptors — AuthInterceptor

Interceptor sử dụng `Mutex` để tránh race condition khi refresh token:

```kotlin
// Chỉ một coroutine được refresh token tại một thời điểm
private val mutex = Mutex()

// Trên 401: các request song song sẽ wait mutex, sau đó dùng token mới
val newAccessToken = runBlocking(io) {
    mutex.withLock { executeRefreshTokenIfNeeded(localUser) }
}
```

- `RefreshTokenInterceptor` chạy trên OkHttp client **riêng biệt** với `AuthInterceptor`.
- Không tạo circular dependency giữa hai OkHttp client.
