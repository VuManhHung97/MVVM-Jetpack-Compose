---
name: pr-checklist
description: Review checklist for Android / Kotlin / Compose changes in this repo. Use when reviewing code, or before committing or opening a pull request.
---

# PR Review Checklist

Go through every section against the diff (`git diff develop...HEAD`). Each item points to the rule
that defines it; read that rule when an item is unclear. Report violations with file and line.

## Naming & style (`kotlin-style.md`)
- [ ] Booleans positive with `is/has/can/should/was`; counts named `xCount`.
- [ ] Features / packages plural, classes singular.
- [ ] Callbacks `on + Noun + Verb`; lambda types name their parameters.
- [ ] Named arguments; one argument per line when more than one.
- [ ] Every failure branch logs with `Timber.e`.
- [ ] TODOs say who / why; commit message and PR title follow Conventional Commits.

## Compose (`compose-rules.md`)
- [ ] `modifier` is the first optional parameter and applies to the root only.
- [ ] Screens split into `<X>Route` (`internal`) + `<X>Content` (`private`); previews call `Content`.
- [ ] Events collected with `collectInLaunchedEffectWithLifecycle`; no `remember { collectAsStateWithLifecycle() }`.
- [ ] Lazy items have a stable `key` and a constant `contentType`.
- [ ] `derivedStateOf` only for pure UI state; no business logic recomputed in the UI.
- [ ] Effects keyed correctly; `DisposableEffect` has `onDispose`; no side effects in composition.
- [ ] Bottom sheets use `ModalBottomSheet`; dialogs reuse `DialogCommon`; snackbars via `SnackBarManager`.
- [ ] Components split only when reused or standalone; named by role; explicit empty state.

## MVI (`mvi-pattern.md`)
- [ ] UiState is `@Parcelize @Immutable data class` + `StateSaver` (forms) or `@Immutable` LCE sealed interface (data screens).
- [ ] No `List` in `@Immutable` state; list ops use `core.common.extension.ImmutableList` helpers.
- [ ] One-shot events go through `SingleEvent` / `EventChannel`, never UiState / `SharedFlow` / `StateFlow`.
- [ ] State updated only via `emitState`; sub-state updates use private extension functions.
- [ ] Domain → UI mapping via `toXxxUiItem()` extensions in the Contract file.

## Architecture & DI (`clean-architecture.md`, `dependency-injection.md`)
- [ ] `feature/*` doesn't depend on `core:data` / `core:network` / `core:local` / another feature's `:impl`.
- [ ] In `:impl`, `api(...)` is only for the feature's own `:api`.
- [ ] `core:model` / `core:domain` stay pure JVM.
- [ ] DTOs in `core:network`, entities in `core:local`; mappers in the right module.
- [ ] Implementations `internal`, interfaces `public`; DataModule binds interface → impl.
- [ ] Only external dependencies are injected.
- [ ] Call the PR "Clean Architecture" only if no layer boundary is violated. Otherwise say
      "MVVM with improved code organization".

## Errors
- [ ] Failing functions return `Result<Value, AppError>`; nothing thrown across layers.
- [ ] Correct `AppError` branch for the origin; `nonFatalOrThrow()` before mapping.
- [ ] Results chained with `coroutineBinding { .bind() }`, not nested `.fold()`.

## Navigation 3 (`navigation.md`)
- [ ] `NavKey` is `@Serializable data object` / `data class` in `:api`; entry provider in `:impl`.
- [ ] Each `NavKey` has exactly one `entry<>` and is registered in `MainActivity`'s `entryProvider { }`.
- [ ] `topLevelRoutes` contains tabs only.
- [ ] No navigation lambdas passed across modules; no translated text in keys.
- [ ] Nothing relies on "returning to X re-runs X".

## Data & storage (`data-layer.md`, `local-storage.md`)
- [ ] No mock / seed data in ViewModels or repositories; Fakes read dummy JSON from `resources/`.
- [ ] Profile updates don't overwrite stored tokens.
- [ ] DataStore read `IOException` handled; local errors mapped via `LocalErrorMapper`.

## Theming & strings (`theming-strings-resources.md`)
- [ ] No new color objects or scattered `Color(0x...)`; Material roles via `colorScheme`.
- [ ] Every visible string via `stringResource` / `getString`; enums use `@StringRes` / `@DrawableRes`.

## Build hygiene (`detekt-hygiene.md`)
- [ ] `./gradlew spotlessApply` and `./gradlew detekt` show `BUILD SUCCESSFUL`.
- [ ] No unused imports or dependencies; every `@Suppress` has a comment; `detekt.yml` untouched.
