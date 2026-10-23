---
paths:
  - "feature/**/*.kt"
  - "feature/**/build.gradle.kts"
  - "core/navigation/**/*.kt"
  - "app/src/**/*.kt"
---

# Navigation Rules (Jetpack Navigation 3)

The project uses **Navigation 3**. The back stack is a `List` owned by our code, not a `NavController`.
Navigate = `add()`. Back = `removeLastOrNull()`. No route strings, no nested graphs.

## Two-layer model

```
┌────────────────────────────────────────────────────────────┐
│  OUTER — rootStack: NavBackStack<NavKey>                   │
│  [ Auth ] · [ Auth, SignIn ] · [ Auth, SignUp ] · [ Main ] │
│  entered / left with add / remove → ViewModel is cleared   │
├────────────────────────────────────────────────────────────┤
│  INNER — NavigationState (exists only under MainNavKey)    │
│  startRoute    = HomeNavKey          (tab that exits app)  │
│  topLevelRoute : MutableState        = Home | Profile      │
│  backStacks    = { Home: [...], Profile: [...] }           │
└────────────────────────────────────────────────────────────┘
```

**`MainNavKey` is a marker** and is never rendered, so there is no `entry<MainNavKey>`.

Invariant: **`topLevelRoutes` contains tabs only.** Screens outside the tab system (auth flow,
onboarding, ...) belong in `rootStack`. Putting a non-tab screen in `topLevelRoutes` means its entry
never leaves the back stack, so its ViewModel lives forever.

## Modules

| Module | Contains | Depends on |
|---|---|---|
| `:core:navigation` | `NavigationState`, `AppNavigationState`, `Navigator`, `toEntries()` | `navigation3-runtime` only. Knows no feature keys |
| `feature/x/api` | `@Serializable data object XNavKey : NavKey` + `fun Navigator.navigateToX()` | `:core:navigation` |
| `feature/x/impl` | `xEntry()`, Screen, ViewModel, Contract | `x/api` + the `api` of each feature it navigates to |

A feature that navigates to another feature depends on that feature's `:api`, **never** `:impl`.

## NavKey (in `:api`)

```kotlin
@Serializable
data object HomeNavKey : NavKey

fun Navigator.navigateToHome() = navigate(HomeNavKey)
```

- `data object`, not `object`, so `toString()` is readable when logging the back stack.
- Keys with arguments are `data class`; the arguments are properties, never encoded into a string.
- **No display text in keys.** Keys are serialized and restored after process death, so a
  translated string would freeze and ignore language changes. Store a `@StringRes` or an enum and
  resolve it in the screen.
- `@Serializable` only works when the module applies `kotlin.plugin.serialization`.
  `android.feature.api` does this. If a `NavKey` lives anywhere else, check with `javap` that a
  `serializer()` is generated: a missing one crashes at process death, not at compile time.

## Entry provider (in `:impl`)

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

- One `<Feature>EntryProvider.kt` per screen, exposing `fun EntryProviderScope<NavKey>.xEntry(navigator: Navigator)`.
- `:app` calls each one directly inside `entryProvider { }` in `MainActivity`. **No** Hilt
  multibinding (nowinandroid style).
- **Don't pass navigation lambdas across modules.** The entry builder already has `navigator`.
- Screen Composables keep `onNavigateToX: () -> Unit` signatures; only the wiring changes.
- `entry<T>` accepts any `NavKey` and the compiler won't catch a wrong key. Check by eye: each key
  has exactly one entry and none is missing.

## `Navigator` API

| Function | Use when |
|---|---|
| `navigate(navKey)` | Go to any screen. Picks the right stack based on the current area |
| `resetRootTo(navKey)` | Switch area: signed in → `MainNavKey`; sign out / 401 → `AuthenticationNavKey`. Also resets every tab stack |
| `goBack(): Boolean` | `false` = stack exhausted, the caller decides whether to `finish()` |
| `clearCurrentStack()` | User re-selected the current tab |

`navigate()` needs no marker: in the auth area the key goes on `rootStack`, in the app area it goes
on the current tab's stack.

## ViewModel lifetime

> *"when an entry is removed from the back stack the `ViewModelStoreNavEntryDecorator`
> clears its associated `ViewModelStore`"*, [Nav3 docs](https://developer.android.com/guide/navigation/navigation-3/naventrydecorators)

Consequences:

- **`clear()` then `add()` of the same key in one frame does not count as leaving the stack.**
  Compose only sees the final snapshot. To clear a ViewModel, the entry must actually disappear.
- **Tab entries live forever.** That is by design (tabs keep state). Don't expect a tab's ViewModel
  to be recreated when switching tabs.
- **Nav2 destroyed destinations on `popUpTo(inclusive = true)`; Nav3 does not.** Any old code
  assuming "returning to X re-runs X from scratch" is wrong. Work that must re-run goes in a
  `LaunchedEffect` in the Route, not in the ViewModel's `init`.

## Where navigation starts

| Kind | Examples | How |
|---|---|---|
| User action | back, open WebView, pick a language | Call `navigator` in the entry builder |
| ViewModel, after async work | sign-in success, logout, deep link, 401 | `SingleEvent` via `EventChannel` (see `mvi-pattern.md` for the queued-event pitfall) |

## Bottom bar

```kotlin
val currentTopLevelKey = navigationState.tabs.topLevelRoute
val currentKey = if (navigationState.isInAppArea) navigationState.tabs.currentStack.last() else null
val isMainNavigationBarVisible = topLevelDestinations.any { it.navKey == currentKey }
```

The bar shows only in the app area **and** on a tab's root screen. Switching tabs and re-selecting
the current tab are different intents, so they get separate callbacks (`onDestinationSelect` /
`onDestinationReselect`).

## Invariants

- A tab's stack is **never empty**; it always keeps at least the tab's root key.
- `startRoute` is in `topLevelRoutes`.
- `resetRootTo` only accepts `rootStack` keys (Auth or Main), never a tab key.
- A new tab needs its key in `topLevelRoutes` **and** in `MainTopScreenTopLevelDestination`.
  A non-tab screen never touches `topLevelRoutes`.
