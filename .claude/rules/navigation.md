# Navigation Rules — Jetpack Navigation 3

Dự án dùng **Navigation 3**. Back stack là `List` do code mình giữ, không nằm trong `NavController`.
Navigate = `add()`. Back = `removeLastOrNull()`. Không có route string, không có nested graph.

## Mô hình hai lớp

```
┌───────────────────────────────────────────────────────────┐
│  LỚP NGOÀI — rootStack: NavBackStack<NavKey>              │
│  [ Auth ] · [ Auth, SignIn ] · [ Auth, SignUp ] · [ Main ]│
│  vào/ra bằng add / remove  →  ViewModel được clear         │
├───────────────────────────────────────────────────────────┤
│  LỚP TRONG — NavigationState (chỉ tồn tại dưới MainNavKey)│
│  startRoute   = HomeNavKey        (tab thoát app)         │
│  topLevelRoute: MutableState      = Home | Profile        │
│  backStacks   = { Home: [...], Profile: [...] }           │
└───────────────────────────────────────────────────────────┘
```

**`MainNavKey` là marker**, không bao giờ được render → không khai `entry<MainNavKey>`.

Nguyên tắc bất di bất dịch: **`topLevelRoutes` chỉ chứa tab.** Màn hình ngoài hệ tab (luồng xác thực,
onboarding…) thuộc `rootStack`. Nhét màn không-phải-tab vào `topLevelRoutes` sẽ khiến entry của nó
không bao giờ rời back stack → ViewModel sống mãi.

## Module

| Module | Chứa | Phụ thuộc |
|---|---|---|
| `:core:navigation` | `NavigationState`, `AppNavigationState`, `Navigator`, `toEntries()` | chỉ `navigation3-runtime` — **không** biết key của feature nào |
| `feature/x/api` | `@Serializable data object XNavKey : NavKey` + `fun Navigator.navigateToX()` | `:core:navigation` |
| `feature/x/impl` | `xEntry()`, Screen, ViewModel, Contract | `x/api` + `api` của feature nó điều hướng tới |

Feature cần đi tới feature khác thì phụ thuộc `:api` của feature đó, **không bao giờ** `:impl`.

## NavKey — đặt ở `:api`

```kotlin
@Serializable
data object HomeNavKey : NavKey

fun Navigator.navigateToHome() = navigate(HomeNavKey)
```

- `data object` (không phải `object`) — `toString()` đọc được khi log back stack.
- Key có tham số dùng `data class`; tham số **là** argument, không encode vào string.
- **Không để text hiển thị trong key.** Key được serialize và lưu qua process death; chuỗi đã dịch
  sẽ đóng băng, đổi ngôn ngữ không cập nhật. Giữ `@StringRes` hoặc enum, resolve ở màn hình.
- `@Serializable` chỉ có tác dụng khi module apply plugin `kotlin.plugin.serialization` —
  plugin `android.feature.api` đã lo. Đặt `NavKey` ở module khác thì phải tự kiểm bằng
  `javap` xem có sinh `serializer()` không; thiếu thì crash lúc process death, không phải lúc compile.

## Entry provider — đặt ở `:impl`

```kotlin
// feature/profile/impl/.../navigation/ProfileEntryProvider.kt
fun EntryProviderScope<NavKey>.profileEntry(navigator: Navigator) {
  entry<ProfileNavKey> {
    ProfileRoute(
      onNavigateToLanguageScreen = navigator::navigateToLanguage,
      onNavigateToWebViewScreen = navigator::navigateToWebView,
      onNavigateToAuthenticationScreen = { navigator.resetRootTo(AuthenticationNavKey) },
    )
  }
}
```

- Một file `<Feature>EntryProvider.kt` cho mỗi màn, hàm `fun EntryProviderScope<NavKey>.xEntry(navigator: Navigator)`.
- `:app` gọi thẳng trong `entryProvider { }` — **không** dùng Hilt multibinding (theo nowinandroid).
- **Không truyền lambda điều hướng xuyên module.** Entry builder có sẵn `navigator` trong scope.
- Composable màn hình giữ nguyên chữ ký `onNavigateToX: () -> Unit` — chỉ chỗ nối dây đổi.
- `entry<T>` nhận mọi `NavKey`, compiler **không** bắt được nếu khai nhầm key. Kiểm mắt một lượt:
  mỗi key đúng một entry, không key nào thiếu.

## `Navigator` API

| Hàm | Dùng khi |
|---|---|
| `navigate(navKey)` | đi tới màn bất kỳ — tự phân nhánh theo vùng đang đứng |
| `resetRootTo(navKey)` | đổi vùng: đăng nhập xong → `MainNavKey`, đăng xuất / 401 → `AuthenticationNavKey`. Reset luôn mọi `backStacks` |
| `goBack(): Boolean` | `false` = hết stack, caller quyết định `finish()` |
| `clearCurrentStack()` | bấm lại tab đang đứng |

`navigate()` không cần marker: đang ở vùng auth thì key vào `rootStack`, đang ở vùng app thì key vào
sub-stack của tab hiện tại.

## Vòng đời ViewModel — quy tắc duy nhất

> *"when an entry is removed from the back stack the `ViewModelStoreNavEntryDecorator`
> clears its associated `ViewModelStore`"* — [tài liệu Nav3](https://developer.android.com/guide/navigation/navigation-3/naventrydecorators)

Hệ quả phải nhớ:

- **`clear()` rồi `add()` cùng một key trong cùng một frame KHÔNG tính là "rời stack".** Compose chỉ
  thấy snapshot cuối. Muốn ViewModel bị clear thì entry phải thật sự biến mất khỏi danh sách.
- **Entry của tab sống mãi** — đúng thiết kế, tab cần giữ state. Đừng trông chờ ViewModel của tab
  được tạo lại khi chuyển tab.
- **Nav2 huỷ destination khi `popUpTo(inclusive = true)`; Nav3 thì không.** Mọi chỗ code cũ ngầm dựa
  vào *"quay lại màn X = X chạy lại từ đầu"* đều sai. Cần chạy lại thì dùng `LaunchedEffect` trong
  Route, đừng đặt trong `init` của ViewModel.

## Điều hướng khởi phát từ đâu

| Loại | Ví dụ | Cách làm |
|---|---|---|
| Người dùng bấm | back, mở WebView, chọn ngôn ngữ | gọi thẳng `navigator` trong entry builder |
| ViewModel, sau việc async | sign-in thành công, logout, deep link, 401 | `SingleEvent` qua `EventChannel` như cũ |

`EventChannel` là `Channel.UNLIMITED` và chỉ đóng khi ViewModel bị clear. ViewModel sống lâu + màn
không hiển thị = event xếp hàng, sẽ nổ hết khi màn quay lại. Nếu một `SingleEvent` chỉ có nghĩa lúc
màn đang hiển thị, thu hẹp nguồn phát (ví dụ `.map { it is Authenticated }.distinctUntilChanged()`)
thay vì trông vào việc ViewModel sẽ được tạo mới.

## Bottom bar

```kotlin
val currentTopLevelKey = navigationState.tabs.topLevelRoute
val currentKey = if (navigationState.isInAppArea) navigationState.tabs.currentStack.last() else null
val isMainNavigationBarVisible = topLevelDestinations.any { it.navKey == currentKey }
```

Bar chỉ hiện ở vùng app **và** khi đang đứng ở màn gốc của một tab. Chuyển tab và bấm lại tab là hai
ý định khác nhau → hai callback riêng (`onDestinationSelect` / `onDestinationReselect`), không gộp.

## Bất biến phải giữ

- Sub-stack của mỗi tab **không bao giờ rỗng** — luôn còn ít nhất key gốc của tab.
- `startRoute` phải nằm trong `topLevelRoutes`.
- `resetRootTo` chỉ nhận key thuộc `rootStack` (Auth hoặc Main), không nhận key của tab.

## Network Interceptors — AuthInterceptor

Interceptor dùng `Mutex` để tránh race condition khi refresh token:

```kotlin
private val mutex = Mutex()

val newAccessToken = runBlocking(io) {
  mutex.withLock { executeRefreshTokenIfNeeded(localUser) }
}
```

- `RefreshTokenInterceptor` chạy trên OkHttp client **riêng biệt** với `AuthInterceptor`.
- Không tạo circular dependency giữa hai OkHttp client.
