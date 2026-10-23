---
name: implement-design-handoff
description: Implement a Claude Design handoff bundle (HTML/CSS/JS, usually *.dc.html + README.md) as real screens in this project, with the correct multi-module architecture from the start and dp-accurate layout.
argument-hint: <path-to-handoff-zip-or-folder>
disable-model-invocation: true
---

# Implement Design Handoff

Turn the Claude Design handoff at `$ARGUMENTS` into real app screens on this base project:
correct multi-module architecture from the start, matching the design to the dp.

## Core principles (read before starting)

- **No "god module"** that draws its own tab bar / login / modals. Each screen or screen group is a
  feature module following the existing pattern; tab bar and navigation use the existing
  coordination (`feature/main` + `navigation.md`).
- **Reuse existing modules** when a design screen matches an existing feature (login →
  `authentication`, profile → `profile`, tab bar → `main` / navigation). For those, **restyle the UI
  and rewire only**; keep the business flow (real auth, logout, ...).
- This is a **pixel-perfect recreation** in Compose, **not** a copy of the HTML structure. Read
  the HTML/CSS for dp, colors and layout; screenshots are not required.
- Follow every rule in `.claude/rules/`, especially `clean-architecture`, `mvi-pattern`,
  `compose-rules`, `data-layer`, `theming-strings-resources`, `detekt-hygiene`, `navigation`.

## Step 0: read the handoff, extract colors, agree on scope

1. Unzip, `find <bundle> -type f`, and **read every file** (README, the main `*.dc.html`, the files
   it imports, standalone files). Read any theme / design-token file too, but **don't depend on one
   existing**: many handoffs have no palette.
2. **Extract the colors from the UI itself.** Go through each screen and component and list the
   colors actually used, by purpose: backgrounds / gradients, text (title / label / secondary),
   buttons and pressed states, borders and dividers, focus / error / success, illustration colors
   (icons, artwork, brand). Deduplicate and give each a purpose. This feeds Step 3.
3. List **every screen**, every **modal / bottom sheet / dialog**, the **bottom nav** (how many
   tabs), overlays (toast, loading), and sample state / data in the scripts.
4. **Ask the user before coding** (AskUserQuestion), covering the three usual decisions:
   - **Data layer**: repository + Fake data source + dummy JSON (recommended), or temporary state
     in the ViewModel?
   - **Integration**: does this replace the base app's main flow (restyle sign-in, change `main`
     tabs) or run alongside it as a new entry point?
   - **Scope**: all screens, or a subset first?

## Step 1: map each screen to a module

Build a `design screen → target` table:
- Matches an existing feature → **reuse that module**, restyle + change text / colors only.
- New screen group (dashboard, list, detail, history, ...) → **new feature module** per group,
  scaffolded with `/new-feature-module`.
- Bottom nav with N tabs → configure `MainTopScreenTopLevelDestination` + the main nav bar (each tab
  points to a feature's `NavKey`). Tab enum uses `@DrawableRes` / `@StringRes`.

## Step 2: data layer (if the user chose repository + dummy JSON)

Follow `data-layer.md`: domain model + repository interface → DTO + ApiService + Fake data source
reading dummy JSON + mapper + repository impl (`@Singleton`, observed via Flow, optimistic writes).
One source of truth, so tabs stay in sync.

## Step 3: theme + strings (right from the start)

Follow `theming-strings-resources.md`:
- **Match the extracted colors against the existing theme.** For each design color, look for an
  existing `ColorScheme` / palette token with the same role. Exists → reuse. Missing or different →
  update the existing token or add one in the right place. The source of truth is **the colors in
  the UI** plus **the app's theme**, not a palette file.
- **Map to the existing Material `ColorScheme`; don't add parallel semantic tokens.** Material-role
  colors update the existing `*Light` / `*Dark` token values; components read
  `MaterialTheme.colorScheme.*`. If the app renders one fixed theme and these screens need the
  other, use the other scheme instead of building a new one.
- Colors **without a Material role** (mascot, background gradient, button shadow, brand mark) stay
  out of the central palette; if only one screen uses them, declare them in that screen's package.
- **All text via `stringResource`** from `core:resource` from the start.
- Fonts: if the design uses a special font and you don't want to bundle a `.ttf`, approximate with
  `FontFamily.Serif` / default and note it; ask the user if an exact match matters.

## Step 4: each screen with MVI

Follow `mvi-pattern` + `compose-rules`:
- `<Screen>Contract.kt`: UiState (LCE `@Immutable sealed interface`, `PersistentList`), SingleEvent
  in the Contract, `internal fun toXxxUiItem()` mappers.
- `<Screen>ViewModel.kt` with `emitState`; `init` collects the repository flow.
- `<Screen>Screen.kt` with `Route` + `Content`, plus the entry provider. Reuse `core:ui` widgets;
  components reused across features go in `core:ui` `widget/`.
- **Search** (if any): `savedStateHandle.getStateFlow(KEY, "")` + `.debounce()` like the existing
  search module, filtered through `emitState`.
- Split components only as much as needed and name them by role (see `compose-rules.md`).
- Validation / derived state comes from UiState; never recompute it in the Composable.

## Step 5: modals

Per the Modals section of `compose-rules.md`: `ModalBottomSheet` (create `AppModalBottomSheet` in
`core:ui` once a second sheet appears), reuse `DialogCommon`, snackbars via `SnackBarManager`.

## Step 6: wire + clean build

- Register `api` + `impl` (`settings.gradle.kts`, `app/build.gradle.kts`), add
  `<name>Entry(navigator)` to `entryProvider { }` in `MainActivity`.
- Before reporting done: **`spotlessApply` → `detekt` → `assembleDebug`** all green
  (`detekt-hygiene.md`).

## Step 7: verify on a device (required)

- `adb devices` → `adb install -r <apk>` → open the app.
- `adb exec-out screencap -p > shot.png` for each screen and modal; read it with the Read tool and
  **compare with the design** (layout, colors, spacing). Fix until it matches.
- Check the data flow (an action in tab A shows in tab B if they share a repository).
- If sign-in needs an unreachable real backend: verify the sign-in UI, temporarily point the start
  destination at the main area to capture the tabs, **then revert**.

## Lessons learned (apply to every module)

1. **Wrong color source.** The source is the **UI design + the app theme**, not a "palette in the
   handoff" (often missing). Extract from the UI, match against the theme, reuse or update.
2. **Parallel semantic color tokens** while a `ColorScheme` already exists. Material-role colors
   update the existing scheme tokens; only non-role colors are declared separately, in the screen's
   package if only one screen uses them.
3. **Over-splitting components / decorative names.** Split only when reused or standalone; name by
   role; inline one-off UI.
4. **Recomputing ViewModel logic in the Composable.** Read it from UiState.
5. **Wrong scope.** A handoff that redraws an existing screen is **UI-only** work (restyle the
   module, keep the ViewModel and flow), not a new vertical slice. Confirm with the user if the
   request doesn't match the handoff.

After finishing a handoff, if you hit a new recurring mistake, **add it to this skill** (Lessons
learned + the relevant step).

## Final checklist

- [ ] Read every bundle file, **extracted colors from the UI**, asked about scope / integration / data before coding.
- [ ] Right call on reuse + restyle vs new module; no god module.
- [ ] Data through repository + Fake + dummy JSON (no mock in the ViewModel).
- [ ] Colors **mapped onto the existing `ColorScheme`**; one-screen illustration colors in that screen's package; text via `stringResource`; enums use `@StringRes` / `@DrawableRes`.
- [ ] Components split just enough, named by role, one reusable component per file.
- [ ] Composables read validation / derived state from the ViewModel.
- [ ] ViewModels use `emitState`; SingleEvent in the Contract; `PersistentList` in state.
- [ ] Bottom sheets / dialogs use the shared components.
- [ ] Business flow unchanged (restyle only) unless the user asked otherwise.
- [ ] spotless + detekt + build green; app run on a device and screenshots compared with the design.
