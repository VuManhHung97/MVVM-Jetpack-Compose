---
description: Detekt/Spotless hygiene — preempt các lỗi lint hay gặp (ImmutableList trong state, unused import, dòng >120, @Suppress có lý do) để qua spotless+detekt ngay lần đầu.
globs: **/*.kt
---

# Detekt / Spotless Hygiene — tránh vòng lặp sửa lint

Checklist để code Compose/Kotlin qua `spotlessCheck` + `detekt` (thường `warningsAsErrors = true`) ngay lần đầu, thay vì sửa nhiều vòng. Áp dụng **trước khi coi task là xong**.

## Quy trình
1. `./gradlew spotlessApply` (tự format) **rồi** `./gradlew detekt`.
2. Lưu ý: một số setup ktlint **KHÔNG tự xoá unused import** → phải tự xoá tay, nếu không detekt `UnusedImports` sẽ fail.
3. Khi grep kết quả gradle, **đừng** để `| grep` che exit code (`$?` sẽ là của grep). Kiểm tra `BUILD SUCCESSFUL/FAILED` trong log, không chỉ exit code lệnh cuối.

## Các lỗi hay gặp & cách preempt

### Compose stability (unstable collections)
- `@Immutable` UiState / tham số Composable **không dùng `List<T>`** → dùng `ImmutableList<T>`/`PersistentList<T>` (kotlinx.collections.immutable).
- Map ở ViewModel: `.mapToPersistentList { }` hoặc `.map { }.toPersistentList()`. Preview: `persistentListOf(...)`.

### UnusedImports / UnusedParameter
- Xoá import không dùng **thủ công** (đặc biệt sau khi refactor/đổi widget).
- Composable nhận param mà không render (vd `onNavigateBack` khi UI không có nút back) → hoặc dùng, hoặc `@Suppress("UnusedParameter")` **kèm comment lý do**.
- Reusable component: bỏ `modifier` nếu không dùng (đừng để `modifier: Modifier = Modifier` thừa).

### MaxLineLength (≤ 120, ktlint)
- Dòng dài (thường do tên object màu dài + biểu thức `if`): **tách local val**, hoặc xuống dòng.
  ```kotlin
  val statusColor = if (isDeposit) { Colors.Green } else { Colors.Red }
  Text(..., color = statusColor)
  ```
- Import statement được ktlint/detekt bỏ qua độ dài — không cần sửa.

### MagicNumber
- `.dp`/`.sp` là extension → detekt **bỏ qua** (không cần sửa). Chỉ **literal float/long/int thô** (alpha `0.4f`, fraction, số tiền, offset random…) mới bị bắt.
- UI dựng-từ-design nhiều literal thiết kế → `@file:Suppress("MagicNumber")` **kèm comment** "Design-handoff pixel/opacity values…". Constant lặp lại thì tách `private const val`.

### TooManyFunctions / LongMethod / CognitiveComplexMethod / LongParameterList
- MVI single-holder nhiều action, hoặc screen host lớn dựng-từ-design: `@Suppress(...)` **kèm comment giải thích** vì sao vượt ngưỡng là chủ đích (vd "single stateless host fan-out theo design prototype").
- Nhưng ưu tiên **tách sub-composable** khi hợp lý trước khi suppress.

### StringLiteralDuplication / MaxLineLength ở seed/dummy
- File dữ liệu dummy nhiều dòng dài/lặp: `@Suppress("StringLiteralDuplication", "MaxLineLength")` (và `@Suppress("ktlint:standard:max-line-length")` cho ktlint) trên object seed.

## Nguyên tắc @Suppress
- Chỉ suppress khi rule **thực sự không áp dụng được** cho ngữ cảnh (design token, MVI holder, generated-shape). **Luôn có comment** ngắn giải thích.
- Ưu tiên đúng ngưỡng level nhỏ nhất (file / class / function) thay vì tắt global.
- **Không** sửa `detekt.yml` để nới rule trừ khi được yêu cầu.

## Checklist trước khi báo xong
- [ ] `spotlessApply` pass; đã xoá unused import tay.
- [ ] Không `List<>` trong `@Immutable` state/param.
- [ ] Dòng ≤ 120 (đã tách local val cho biểu thức dài).
- [ ] `@Suppress` đều có comment; không đụng `detekt.yml`.
- [ ] `detekt` `BUILD SUCCESSFUL` (kiểm tra log, không chỉ exit code).
