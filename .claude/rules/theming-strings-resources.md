---
paths:
  - "feature/**/*.kt"
  - "core/ui/**/*.kt"
  - "core/resource/**"
  - "app/src/**"
---

# Theming, Strings & Resources

Reuse the existing theme and resources, support i18n from day one, and keep text and icons out of
enums.

## 1. Colors: extend the existing theme, never create a new one

Theme lives in `core:ui` `theme/`: Material token values (`primaryLight`, `surfaceDark`, ...) that
build `lightScheme` / `darkScheme`, plus the central palette object `MVVMJetPackComposeColors`.

**Colors that match a Material role** (primary, secondary, tertiary, error, surface, outline, ...):
- Update the existing scheme token values (`primaryLight`, `secondaryLight`, ... or `*Dark`) and
  read them with `MaterialTheme.colorScheme.*`.
- **Don't** add parallel semantic tokens (`XxxPrimary`, `XxxSurface`, ...) to the palette. They
  duplicate Material roles and will need a refactor.
- If the app renders one fixed theme (e.g. dark) and a group of screens needs the other, use the
  other scheme (update `lightScheme`, render those screens with `useDarkTheme = false`). Don't build
  a separate `ColorScheme` or color object.
- Pick the role at the **screen's call site** (`colorScheme.primary` on one screen,
  `colorScheme.tertiary` on another), not inside a reusable component.

**Colors with no Material role** (illustrations, mascots, background gradients, 3D button shadows,
Google / Facebook brand marks):
- Used by **one screen only** → declare `private val` in that screen's package or file.
- Used by **two or more screens** → add to the shared theme (palette object or an extended-colors
  group next to the theme).

Always:
- Never create a parallel color object or file for one feature (`FooColors`).
- Brushes, gradients and font families go in the same `core:ui` `theme/` package.
- No scattered `Color(0xFF...)` literals in screens; reference tokens.

## 2. Text: every visible string through resources from the start

- Never hardcode Vietnamese or English UI text in a Composable or ViewModel. Define it in
  `core:resource` `res/values/strings.xml` and use `stringResource(R.string.x)`.
- Dynamic strings use positional placeholders:
  ```xml
  <string name="deposit_success">Deposited %1$s coins to %2$s</string>
  ```
  ```kotlin
  stringResource(R.string.deposit_success, amountFormatted, username)
  ```
- Outside composition (e.g. handling a SingleEvent in a coroutine): `context.getString(R.string.x, args)`.
- Define every key in `values/` (default language). Translations go in `values-<locale>/` later;
  nothing else is needed.
- A literal `%` that is not a format specifier stays single (`1.7% of total`). Don't write `%%`:
  `getString` without args prints it as `%%`.

## 3. Enums and display data: `@StringRes` / `@DrawableRes`

Enums and display data classes hold resource ids, never text, glyphs or icon characters:

```kotlin
enum class StatusFilter(@param:StringRes val labelResId: Int, val status: Status?) {
  All(R.string.filter_all, null),
  Active(R.string.filter_active, Status.Active),
}
```

- Render with `stringResource(item.labelResId)` and `Icon(ImageVector.vectorResource(item.iconResId), ...)`.
- Icons: add a vector `res/drawable/ic_*.xml` (`fillColor="@android:color/white"`, tint at the call
  site). Don't use unicode characters as icons.
- Keep resource ids out of domain models (`core:model`); they belong on UiModels.

## 4. Keep translatable text out of mappers

- A composed phrase with translatable words (e.g. "Character · Clan · Lv{n}") is **not** built in
  the mapper. The UiItem keeps the raw fields; the Composable formats them with a resource template
  `"%1$s · %2$s · Lv%3$d"`.
- Pure separators (`·`, `+`, `−`) may live in code, but prefer a resource template when the whole
  thing reads as a sentence.

## Checklist

- [ ] No `Color(0x...)` literals in screens and no new color objects.
- [ ] Material-role colors go through `MaterialTheme.colorScheme`.
- [ ] No hardcoded display strings; everything via `stringResource` / `getString`.
- [ ] Enums / display data use `@StringRes` / `@DrawableRes`.
- [ ] Non-format `%` is single; dynamic strings use `%1$s`.
