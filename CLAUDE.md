# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew assembleDebug            # Build debug APK
./gradlew assembleRelease          # Build release APK
./gradlew clean build              # Clean full build
./gradlew spotlessCheck            # Check formatting (Ktlint via Spotless)
./gradlew spotlessApply            # Auto-fix formatting — run before every commit
./gradlew detekt                   # Static analysis (config: detekt.yml)
./gradlew test                     # All unit tests
./gradlew :<module>:test           # Single module (e.g. :core:data:test)
```

## Project Rules

Detailed rules are in [`.claude/rules/`](.claude/rules/):

| File | Covers |
|---|---|
| [`clean-architecture.md`](.claude/rules/clean-architecture.md) | Layer boundaries, dependency direction, Repository/DataSource pattern, AppError hierarchy |
| [`mvi-pattern.md`](.claude/rules/mvi-pattern.md) | UiState, SingleEvent, ViewModel structure, EventChannel |
| [`kotlin-style.md`](.claude/rules/kotlin-style.md) | Naming conventions (boolean, count, callback, feature/class), commit style |
| [`compose-rules.md`](.claude/rules/compose-rules.md) | Composable API design, state management, side-effect APIs |
| [`dependency-injection.md`](.claude/rules/dependency-injection.md) | Hilt rules — what to inject, module organization, event bus |
| [`navigation.md`](.claude/rules/navigation.md) | Nav3: mô hình hai lớp, NavKey/entry provider, vòng đời ViewModel |
| [`local-storage.md`](.claude/rules/local-storage.md) | DataStore Proto, Room, mapper patterns, token safety |
| [`pr-checklist.md`](.claude/rules/pr-checklist.md) | Full review checklist trước khi submit PR |

## Module Structure

```
app/                    # Entry point, MainActivity, MainViewModel, NavDisplay, MainNavigationBar
build-logic/convention/ # Gradle convention plugins (AndroidFeature, Hilt, Library, Compose...)
core/
  model/                # Domain entities (User, AuthenticationState, AppError) — pure Kotlin
  domain/               # Repository interfaces
  data/                 # Repository implementations (internal), DTO mappers
  network/              # Retrofit services, OkHttp interceptors, remote DataSources
  local/                # Room, DataStore, local DataSources
  ui/                   # Shared Compose components, theme, EventChannel
  navigation/           # Nav3: NavigationState, AppNavigationState, Navigator, toEntries()
  resource/             # Drawables, strings
  common/               # Kotlin utility extensions
feature/
  <name>/api/           # NavKey + Navigator.navigateToX(). No Hilt, no Compose
  <name>/impl/          # Screens, ViewModels, entry provider
  authentication/       # Sign in, sign up (MVI pattern reference implementation)
  home/ profile/ search/ webview/ language/
  main/api/             # MainNavKey — marker "đang ở vùng app có tab" (không có impl)
library/flowext/        # Shared Flow extensions
```

## Adding a New Feature Module

Mỗi feature gồm hai module: `api` (contract điều hướng) và `impl` (UI + ViewModel).

1. Create `feature/<name>/api/build.gradle.kts` — chỉ chứa route pattern / `NavKey` và `navigateToX()`:
```kotlin
plugins { id(libs.plugins.android.feature.api.get().pluginId) }
android { namespace = "com.vmh.mvvmjetpackcompose.feature.<name>.api" }
```
2. Create `feature/<name>/impl/build.gradle.kts`:
```kotlin
plugins { id(libs.plugins.android.feature.impl.get().pluginId) }
android { namespace = "com.vmh.mvvmjetpackcompose.feature.<name>" }
dependencies {
    api(projects.feature.<name>.api)

    implementation(projects.core.ui)
    implementation(projects.core.resource)
    implementation(projects.core.common)
    implementation(projects.core.domain)
    implementation(projects.core.model)
}
```
3. Register `:feature:<name>:api` và `:feature:<name>:impl` in `settings.gradle.kts`.
4. Create `presentation/<screen>/` with `<Screen>Contract.kt` + `<Screen>ViewModel.kt` trong `impl`.
5. Create `ui/` with Composable screens trong `impl`.
6. Create `<Name>NavKey` trong `api` và `<Name>EntryProvider` trong `impl`, thêm một dòng `<name>Entry(navigator)` vào `entryProvider { }` của `MainActivity`.

> Feature nào cần điều hướng tới feature khác thì phụ thuộc `:api` của feature đó, **không bao giờ** `:impl`.

> Reference implementation: `feature/authentication/presentation/signIn/`
