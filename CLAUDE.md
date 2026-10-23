# CLAUDE.md

Android base project: Kotlin, Jetpack Compose, multi-module Clean Architecture, MVI presentation,
Hilt, Navigation 3, kotlin-result.

## Build commands

```bash
./gradlew assembleDebug            # Build debug APK
./gradlew assembleRelease          # Build release APK
./gradlew clean build              # Full clean build
./gradlew spotlessCheck            # Check formatting (ktlint via Spotless)
./gradlew spotlessApply            # Auto-fix formatting; run before every commit
./gradlew detekt                   # Static analysis (config: detekt.yml)
./gradlew test                     # All unit tests
./gradlew :<module>:test           # One module, e.g. :core:data:test
```

When piping Gradle output through `grep`, check for `BUILD SUCCESSFUL` / `BUILD FAILED` in the log:
the exit code you see is grep's, not Gradle's.

## Module structure

```
app/                    # Entry point: MainActivity (entryProvider + NavDisplay), MainViewModel, MainNavigationBar
build-logic/convention/ # Gradle convention plugins (android.feature.api/impl, android.hilt, jvm.library, ...)
core/
  model/                # Domain models + AppError. Pure JVM
  domain/               # Repository / Manager interfaces. Pure JVM
  data/                 # Repository implementations (internal), DTO/entity -> domain mappers
  network/              # Retrofit services, DTOs, OkHttp interceptors, remote DataSources
  local/                # DataStore (Proto), Room, local DataSources
  ui/                   # Shared Compose widgets, theme, EventChannel, lifecycle helpers
  navigation/           # Nav3: NavigationState, AppNavigationState, Navigator, toEntries()
  resource/             # Strings, drawables
  common/               # Kotlin extensions (ImmutableList helpers, nonFatalOrThrow, ...)
  analytics/            # AnalyticsTracker (Firebase / no-op)
  deeplink/             # Deep link schemes and resolver
  notification/         # FCM push, notification channels
feature/
  <name>/api/           # NavKey + Navigator.navigateToX(). No Hilt, no Compose
  <name>/impl/          # Screens, ViewModels, Contracts, entry provider
  main/api/             # MainNavKey: marker for the tabbed app area (no impl)
  authentication/ home/ profile/ search/ webview/ language/
library/flowext/        # Shared Flow extensions
```

Reference implementations:
- MVI screen with form + process-death state: `feature/authentication/impl/.../presentation/signIn/` and `ui/signin/SignInScreen.kt`
- LCE screen with selection: `feature/language/`

## Rules

Detailed rules live in `.claude/rules/`. Each one is scoped with `paths:` frontmatter, so it loads
only when Claude reads or edits a matching file.

| File | Covers |
|---|---|
| `clean-architecture.md` | Layer boundaries, dependency direction, Repository/DataSource, `AppError`, interceptors |
| `dependency-injection.md` | Hilt: what to inject, module layout, convention plugins |
| `kotlin-style.md` | Naming, argument style, logging, TODOs, time types, commit style |
| `detekt-hygiene.md` | Passing spotless + detekt on the first try, `@Suppress` policy |
| `mvi-pattern.md` | UiState, SingleEvent, ViewModel structure, state mappers |
| `compose-rules.md` | Composable API, Route/Content split, state, side effects, modals |
| `navigation.md` | Navigation 3 two-layer model, NavKey, entry providers, ViewModel lifetime |
| `theming-strings-resources.md` | Colors, strings / i18n, `@StringRes` / `@DrawableRes` |
| `data-layer.md` | New data without a backend: DTO + ApiService + Fake data source + dummy JSON |
| `local-storage.md` | DataStore Proto, Room, local mappers, token safety |

## Skills and agents

- `/new-feature-module <name>`: scaffold an empty `api` + `impl` feature module.
- `/create-screen-ui <Name>`: build the LCE UI layer for an existing feature module.
- `/feature-vertical-slice <domain>`: full slice from domain model to UI, backed by dummy JSON.
- `/implement-design-handoff <path>`: turn a Claude Design handoff bundle into feature modules.
- `/pr-checklist`: review checklist before opening or reviewing a PR.
- Agent `android-quality`: runs spotless + detekt + compile and fixes mechanical violations.
- Agent `researcher`: compares libraries and approaches, ends with a recommendation.

## Non-negotiables

- `feature/*` never depends on `core:data`, `core:network`, `core:local`, or another feature's `:impl`.
- No UseCase layer: ViewModels call repository interfaces directly.
- Every failing operation returns `Result<Value, AppError>`; never throw across layer boundaries.
- Every user-visible string goes through `stringResource` / `getString`.
- Do not edit `detekt.yml` or ktlint config to make a check pass.
