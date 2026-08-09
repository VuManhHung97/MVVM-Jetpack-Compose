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
| [`compose-rules.md`](.claude/rules/compose-rules.md) | Composable API design, state management, side-effect APIs, modal (bottom sheet/dialog) reuse |
| [`dependency-injection.md`](.claude/rules/dependency-injection.md) | Hilt rules — what to inject, module organization, event bus |
| [`navigation.md`](.claude/rules/navigation.md) | NavTypeContainer, lifecycle pitfalls, duplicate collector prevention |
| [`local-storage.md`](.claude/rules/local-storage.md) | DataStore Proto, Room, mapper patterns, token safety |
| [`data-layer.md`](.claude/rules/data-layer.md) | New feature data: API contract + Fake data source + dummy JSON (before real backend) |
| [`theming-strings-resources.md`](.claude/rules/theming-strings-resources.md) | Colors into central palette, mandatory `stringResource` + i18n, `@StringRes`/`@DrawableRes` enums |
| [`detekt-hygiene.md`](.claude/rules/detekt-hygiene.md) | Preempt detekt/spotless failures (ImmutableList state, unused imports, ≤120, `@Suppress` conventions) |
| [`pr-checklist.md`](.claude/rules/pr-checklist.md) | Full review checklist trước khi submit PR |

> Khi hiện thực một **Claude Design handoff** (bundle `*.dc.html`), chạy skill [`/implement-design-handoff`](.claude/commands/implement-design-handoff.md) — quy trình tách module theo màn + tái dùng module có sẵn + verify trên emulator.

## Module Structure

```
app/                    # Entry point, MainActivity, MainViewModel, NavHost, NavTypeContainer
build-logic/convention/ # Gradle convention plugins (AndroidFeature, Hilt, Library, Compose...)
core/
  model/                # Domain entities (User, AuthenticationState, AppError) — pure Kotlin
  domain/               # Repository interfaces
  data/                 # Repository implementations (internal), DTO mappers
  network/              # Retrofit services, OkHttp interceptors, remote DataSources
  local/                # Room, DataStore, local DataSources
  ui/                   # Shared Compose components, theme, EventChannel
  resource/             # Drawables, strings
  common/               # Kotlin utility extensions
feature/
  authentication/       # Sign in, sign up (MVI pattern reference implementation)
  home/ profile/ search/ webview/ main/
library/flowext/        # Shared Flow extensions
```

## Adding a New Feature Module

1. Create `feature/<name>/build.gradle.kts`:
```kotlin
plugins { id(libs.plugins.android.feature.get().pluginId) }
android { namespace = "com.vmh.mvvmjetpackcompose.feature.<name>" }
dependencies {
    implementation(projects.core.ui)
    implementation(projects.core.resource)
    implementation(projects.core.common)
    implementation(projects.core.domain)
    implementation(projects.core.model)
}
```
2. Register in `settings.gradle.kts`.
3. Create `presentation/<screen>/` with `<Screen>Contract.kt` + `<Screen>ViewModel.kt`.
4. Create `ui/` with Composable screens.
5. Add a nav graph, connect to root `NavHost` in `app/`.

> Reference implementation: `feature/authentication/presentation/signIn/`
