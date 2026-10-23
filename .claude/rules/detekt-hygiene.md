---
paths:
  - "**/*.kt"
---

# Detekt / Spotless Hygiene

Write code that passes `spotlessCheck` and `detekt` (`warningsAsErrors = true`) on the first run
instead of fixing lint in several rounds. Apply this before calling a task done.

## Workflow

1. `./gradlew spotlessApply`, then `./gradlew detekt`.
2. ktlint here does **not** remove unused imports. Remove them by hand, or `UnusedImports` fails.
3. Check for `BUILD SUCCESSFUL` / `BUILD FAILED` in the log. When piped through `grep`, `$?` is grep's.

## Common failures and how to avoid them

### Unstable collections in Compose
- `@Immutable` UiState classes and Composable parameters never use `List<T>`. Use
  `ImmutableList<T>` / `PersistentList<T>` (kotlinx.collections.immutable).
- Build them with `mapToPersistentList { }` and friends from `core.common.extension.ImmutableList`
  (see `mvi-pattern.md`). In previews, use `persistentListOf(...)`.

### UnusedImports / UnusedParameter
- Remove unused imports manually, especially after a refactor or a widget swap.
- A Composable parameter that is not rendered (e.g. `onNavigateBack` with no back button): use it,
  or `@Suppress("UnusedParameter")` with a comment saying why.
- Reusable components: drop `modifier` if it is not applied anywhere.

### MaxLineLength (120)
- Extract a local `val` for long expressions, or break the arguments onto separate lines.
  ```kotlin
  val statusColor = if (isDeposit) Colors.Green else Colors.Red
  Text(text = label, color = statusColor)
  ```
- Import lines are exempt.

### MagicNumber
- `.dp` / `.sp` values are ignored by detekt. Only raw `Int` / `Long` / `Float` literals are flagged
  (alpha `0.4f`, fractions, amounts, offsets).
- UI built from a design with many design literals: `@file:Suppress("MagicNumber")` with a
  comment such as `// Design pixel/opacity values`. Repeated values become `private const val`.

### TooManyFunctions / LongMethod / CognitiveComplexMethod / LongParameterList
- First try splitting into sub-composables or helper functions.
- If exceeding the threshold is intentional (MVI ViewModel with many actions, large stateless
  screen host from a design), `@Suppress(...)` with a comment explaining why.

### Seed / dummy data
- Long or repetitive literal data: `@Suppress("StringLiteralDuplication", "MaxLineLength")` plus
  `@Suppress("ktlint:standard:max-line-length")` on the seed object.

## `@Suppress` policy

- Only when the rule genuinely does not fit the context (design tokens, MVI holder, generated shapes).
- Always with a short comment explaining why.
- At the narrowest scope possible: expression, function, class, then file. Never globally.
- Never loosen `detekt.yml` or ktlint config unless explicitly asked.

## Done checklist

- [ ] `spotlessApply` ran; unused imports removed by hand.
- [ ] No `List<>` in `@Immutable` state or Composable params.
- [ ] Lines ≤ 120.
- [ ] Every `@Suppress` has a comment; `detekt.yml` untouched.
- [ ] `detekt` shows `BUILD SUCCESSFUL` in the log.
