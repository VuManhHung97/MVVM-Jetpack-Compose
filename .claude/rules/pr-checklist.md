---
description: Checklist review PR cho Android/Kotlin/Compose. Dùng khi review code hoặc chuẩn bị tạo PR.
---

# PR Review Checklist — Android / Kotlin / Compose

Khi review code hoặc chuẩn bị submit PR, kiểm tra toàn bộ danh sách này.

## Naming & Style

- [ ] Tên biến/function/class đúng convention — boolean positive, `Count` naming đúng (`columnCount` không phải `columnsCount`).
- [ ] Feature/module/package dùng plural; class/type dùng singular.
- [ ] Commit message và PR title theo **Conventional Commits** (`feat:`, `fix:`, `refactor:`...).
- [ ] TODO có scope rõ ràng, không mơ hồ.

## Composable & Compose API

- [ ] Tất cả Composable call ghi rõ argument names — không positional args.
- [ ] Callback tên `on + Noun + Verb`; lambda type có parameter name.
- [ ] `modifier` là parameter cuối.
- [ ] Large composable được extract thành sub-components.
- [ ] Empty state UI được emit rõ ràng.

## State Management

- [ ] `UiState` là `@Parcelize @Immutable data class`.
- [ ] One-shot event đi qua `SingleEvent` / `EventChannel`, không model trong `UiState`.
- [ ] Không dùng `StateFlow` hay `SharedFlow` cho side effect — dùng `EventChannel`.
- [ ] `remember` / `derivedStateOf` / `rememberSaveable` dùng đúng mục đích.
- [ ] Không bọc `collectAsStateWithLifecycle()` bằng `remember`.

## Side Effects

- [ ] `LaunchedEffect` key đúng — `snapshotFlow` không restart effect vô nghĩa.
- [ ] `DisposableEffect` có `onDispose` cleanup.
- [ ] Không có side effect tạo ra trực tiếp trong composition body.

## Architecture & DI

- [ ] `feature/*` không import `core:data`, `core:network`, `core:local`.
- [ ] Domain model trong `core:model` là pure Kotlin — không có Android/Retrofit/Room annotation.
- [ ] DTO sống trong `core:network`, Local entity trong `core:local`, mapper trong `core:data`.
- [ ] DI chỉ inject external dependency — không inject `MutableMap`, `ArrayList`, `AtomicInteger`.
- [ ] Implementation class là `internal`, chỉ interface là `public`.

## Error Handling

- [ ] Hàm có thể fail trả về `Result<Value, AppError>` (kotlin-result), không throw raw exception qua layer boundary.
- [ ] `AppError` subtype đúng layer: `ApiException` từ network, `LocalStorageException` từ local.
- [ ] Dùng `coroutineBinding { }.bind()` để chain Results, không nest `.fold()`.

## Flow / Coroutines / Navigation

- [ ] Flow collection dùng đúng lifecycle scope — không leak collector sau navigation.
- [ ] `popUpTo` / `launchSingleTop` / `restoreState` không giữ ViewModel cũ ngoài ý muốn.
- [ ] Không có duplicate event collection sau navigate.

## Local Storage

- [ ] `update()` DataStore không ghi đè `accessToken`/`refreshToken` khi map profile response.
- [ ] `IOException` khi đọc DataStore được xử lý (emit default) thay vì crash.

## Code Quality

- [ ] `./gradlew spotlessApply` đã chạy — không còn formatting violation.
- [ ] `./gradlew detekt` sạch — không có warning bị bỏ qua không giải thích.
- [ ] `@Suppress` có comment giải thích lý do.
- [ ] Không có import thừa, dependency thừa trong `build.gradle.kts`.

## Ví dụ đúng / sai nhanh

| Chủ đề | Sai | Đúng |
|---|---|---|
| Lambda type | `(Video.Id) -> Unit` | `(videoId: Video.Id) -> Unit` |
| Composable call | `Surface(modifier.fillMaxSize())` | `Surface(modifier = modifier.fillMaxSize())` |
| Callback naming | `onToggleLike` | `onVideoLikeToggle` |
| Feature naming | `ShortVideo` (package) | `ShortVideos` (package) |
| Boolean naming | `isNotActive` | `isActive`, dùng `!isActive` ở call site |
| LaunchedEffect key | `LaunchedEffect(pagerState.currentPage)` + snapshotFlow | `LaunchedEffect(pagerState)` + snapshotFlow |
| Count naming | `columnsCount` | `columnCount` |
