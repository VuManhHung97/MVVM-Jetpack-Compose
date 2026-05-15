---
description: Kotlin và Android coding style, naming conventions cho toàn bộ project.
globs: **/*.kt
---

# Kotlin & Android Coding Style

Tuân thủ Kotlin coding conventions, Android Kotlin style guide và coroutines best practices.

## Naming Conventions

### Boolean

| Nên dùng | Tránh |
|---|---|
| `isActive`, `hasPermission`, `canEdit`, `shouldShowInput`, `wasSelected` | `isNotActive`, `hasNoPermission`, `getIsActive` |

- Prefix chuẩn: `is`, `has`, `can`, `should`, `was`.
- **Luôn đặt tên positive** — negation ở call site: `!isActive`, không phải `isInactive`.
- `isActive` → trạng thái; `shouldShowInput` → quyết định hiển thị (khác nhau về nghĩa).
- `isAny...` / `isSome...` → một vài phần tử; `isEvery...` → tất cả.

### Count / Số đếm

```kotlin
// Đúng
val columnCount: Int
val rowCount: Int
val itemCount: Int
val pageCount: Int

// Sai
val columnsCount: Int   // sai ngữ pháp tự nhiên
val numberOfColumns: Int  // verbose không cần thiết
```

### Feature / Module / Package / Class

| Scope | Convention | Ví dụ |
|---|---|---|
| Feature/module/package | Plural | `ShortVideos`, `phoneNumbers` |
| Class / type / value | Singular | `ShortVideo`, `PhoneNumber`, `PhoneNumberRepository` |
| Presentation package | `presentation` | `feature/search/presentation/` |

- Resource icon: prefix `ic_` + hình dạng/visual: `ic_like_filled`, `ic_arrow_back`. Không đặt theo context mơ hồ: ~~`ic_login_icon`~~.

### Lambda Types

```kotlin
// Đúng — luôn ghi tên parameter
(videoId: Video.Id) -> Unit
(itemId: WatchHistory.Id, position: Int) -> Unit

// Sai
(Video.Id) -> Unit
```

### Callback / Event Naming

Convention: `on + NOUN + VERB`

```kotlin
// Đúng
onVideoLikeToggle(videoId: Video.Id)
onWatchHistoryItemOpen(itemId: WatchHistory.Id)
onProfileAvatarClick()

// Sai
onToggleLike()          // verb trước noun
onClick()               // thiếu ngữ nghĩa
onClicked()             // past tense không nhất quán
onClickDeleteAccountButton()  // quá dài, lặp "click"
```

- Callback nên có parameter nếu parent cần biết item nào bị tác động: `onItemOpen(itemId)` không phải `onItemOpen()`.

### Commit / PR Title

Dùng **Conventional Commits**:
```
feat(search): add filter by category
fix(auth): handle token refresh race condition
refactor(home): extract video card to sub-component
```
Không dùng: `update`, `fix bug`, `change code`.

## Function Calls

```kotlin
// Đúng — luôn ghi tên argument, đặc biệt với composable
Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background)

// Sai
Surface(modifier.fillMaxSize())
```

Quy tắc xuống dòng (áp dụng cho mọi function call, không chỉ Composable):

- **1 arg**: có thể viết trên 1 dòng.
- **> 1 arg**: mỗi arg xuống 1 dòng riêng, dấu `)` xuống dòng mới.

```kotlin
// Đúng — > 1 arg, mỗi arg 1 dòng
searchRepository.searchByKeyword(
    keyword = keyword,
    limit = SEARCH_LIMIT,
    offset = 0,
)

// Sai — > 1 arg nhưng viết gộp
searchRepository.searchByKeyword(keyword = keyword, limit = SEARCH_LIMIT, offset = 0)
```

## Error Logging

Luôn log với `Timber.e()` trong failure branch của `fold` / `onFailure`, kể cả khi lỗi đã được surface lên UI:

```kotlin
// Đúng
.fold(
    success = { ... },
    failure = { error ->
        Timber.e(error, "Failed to search for keyword: $keyword")
        emitState { it.copy(searchResultUiState = Error(error)) }
    },
)

// Sai — không có log
failure = { error ->
    emitState { it.copy(searchResultUiState = Error(error)) }
}
```

## Dependencies & Imports

- Chỉ import/inject dependency thật sự được dùng.
- Không để import thừa, annotation thừa, unused parameter.

## Constants

- Tách constant dùng lặp lại sang `companion object` hoặc top-level `val` trong file riêng.
- Không hard-code lặp lại nhiều nơi.

## TODO

```kotlin
// Đúng
TODO("AuthViewModel: handle biometric fallback after API < 28")
// TODO(vmh/2026-Q3): remove after migration to DataStore v2

// Sai
TODO("fix this later")  // ai? khi nào?
```

## Timestamps

- Ưu tiên `java.time.Instant` thay vì `Long` cho `startTime`/`endTime` trong repository và API.
- Format thời gian dùng `ZoneId.systemDefault()` theo device timezone.
- Chỉ expose `ZoneId` từ ViewModel nếu UI cần react với timezone change.

## Code Quality

- Chạy `./gradlew spotlessApply` trước mỗi commit.
- Chạy `./gradlew detekt` và xử lý hết warnings trước PR.
- `@Suppress` chỉ dùng khi rule thật sự không áp dụng được — phải có comment giải thích lý do.
- `internal` là default visibility cho mọi implementation class. Chỉ `public` khi là interface hoặc public API.
