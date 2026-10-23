---
paths:
  - "**/*.kt"
  - "**/*.kts"
---

# Kotlin & Android Style

Follow the Kotlin coding conventions, the Android Kotlin style guide and coroutines best practices.
This file is the single source for naming and call-site style; other rules link here.

## Naming

### Booleans

| Use | Avoid |
|---|---|
| `isActive`, `hasPermission`, `canEdit`, `shouldShowInput`, `wasSelected` | `isNotActive`, `hasNoPermission`, `getIsActive` |

- Prefixes: `is`, `has`, `can`, `should`, `was`.
- Always name positively and negate at the call site: `!isActive`, not `isInactive`.
- `isActive` describes state; `shouldShowInput` is a display decision. Don't mix them up.
- `isAny...` / `isSome...` for some elements, `isEvery...` for all.

### Counts

`columnCount`, `rowCount`, `itemCount`, `pageCount`. Not `columnsCount` or `numberOfColumns`.

### Features, packages, classes

| Scope | Convention | Example |
|---|---|---|
| Feature / module / package | Plural | `shortVideos`, `phoneNumbers` |
| Class / type / value | Singular | `ShortVideo`, `PhoneNumber`, `PhoneNumberRepository` |
| Presentation package | `presentation` | `feature/search/impl/.../presentation/` |

Icon resources: `ic_` + what the icon looks like (`ic_like_filled`, `ic_arrow_back`), never the
context it's used in (`ic_login_icon`).

### Callbacks: `on + Noun + Verb`

```kotlin
// Right
onVideoLikeToggle(videoId: Video.Id)
onWatchHistoryItemOpen(itemId: WatchHistory.Id)
onProfileAvatarClick()

// Wrong
onToggleLike()                 // verb before noun
onClick()                      // no meaning
onClicked()                    // inconsistent past tense
onClickDeleteAccountButton()   // too long, repeats "click"
```

Pass a payload when the parent needs to know which item was affected: `onItemOpen(itemId)`, not
`onItemOpen()`.

### Lambda types: always name the parameters

```kotlin
// Right
(videoId: Video.Id) -> Unit
(itemId: WatchHistory.Id, position: Int) -> Unit

// Wrong
(Video.Id) -> Unit
```

## Call-site style

Always use named arguments for Composables and for any call with more than one argument.

- **One argument**: may stay on one line.
- **More than one**: one argument per line, trailing comma, closing `)` on its own line.

```kotlin
// Right
Text(text = title)

searchRepository.searchByKeyword(
  keyword = keyword,
  limit = SEARCH_LIMIT,
  offset = 0,
)

// Wrong
searchRepository.searchByKeyword(keyword = keyword, limit = SEARCH_LIMIT, offset = 0)
Surface(modifier.fillMaxSize())
```

This applies to declarations and call sites alike, in Composables, ViewModels and repositories.

## Error logging

Log with `Timber.e()` in every failure branch, even when the error is also shown in the UI:

```kotlin
.fold(
  success = { ... },
  failure = { error ->
    Timber.e(error, "Failed to search for keyword: $keyword")
    emitState { it.copy(searchResultUiState = Error(error)) }
  },
)
```

## Visibility, imports, constants

- `internal` is the default for every implementation class. `public` only for interfaces and real
  public API.
- Only import and inject what is used. No unused imports, annotations or parameters.
- Extract repeated literals to `private const val` (top level) or a `companion object`.

## Comments

Clear code is the documentation. Do not add a comment that restates what the code already says.

- Comment only logic that is complex, non-obvious or intentional: a workaround, an ordering
  constraint, a deliberate deviation from the usual pattern. Say *why*, not *what*.
- No comments that narrate a change ("now keeps the session", "moved from onStart").
  That belongs in the commit message or PR description.
- Required comments still apply: `@Suppress` reasons and design values (see `detekt-hygiene.md`).

```kotlin
// Wrong: repeats the code
// Cancel the load-more job
loadMoreJob?.cancel()

// Right: explains a constraint the code cannot show
// dataStore.data throws an IOException when reading fails
.catch { if (it is IOException) emit(null) else throw it }
```

## TODOs

Say who and why, or when:

```kotlin
// Right
TODO("AuthViewModel: handle biometric fallback after API < 28")
// TODO(vmh/2026-Q3): remove after migration to DataStore v2

// Wrong
TODO("fix this later")
```

## Time

- Prefer `java.time.Instant` over `Long` for `startTime` / `endTime` in repositories and APIs.
- Format with `ZoneId.systemDefault()` (device timezone), in the UI layer.
- Expose `ZoneId` from a ViewModel only when the UI must react to timezone changes.

## Commits and PR titles

Conventional Commits:

```
feat(search): add filter by category
fix(auth): handle token refresh race condition
refactor(home): extract video card to sub-component
```

Never `update`, `fix bug`, `change code`.
