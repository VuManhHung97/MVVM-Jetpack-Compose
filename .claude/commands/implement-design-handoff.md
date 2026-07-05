# Implement Design Handoff

Hiện thực một **Claude Design handoff** (bundle HTML/CSS/JS, thường có file `*.dc.html` + `README.md`) thành app thật trên base project này — đúng kiến trúc multi-module ngay từ đầu, khớp design từng dp.

## Usage
```
/implement-design-handoff <đường-dẫn-zip-hoặc-thư-mục-handoff>
```

## Nguyên tắc cốt lõi (đọc trước khi làm)
- **KHÔNG dồn cả app vào 1 "god module"** tự vẽ tab bar / login / modal. Mỗi màn/nhóm màn = 1 feature module theo pattern có sẵn; tab bar và luồng điều hướng dùng cơ chế điều phối sẵn có của project.
- **Tái dùng module có sẵn** khi màn trong design tương ứng chức năng đã có (login → module authentication; profile → module profile; tab bar → module main/navigation). Với các module này **chỉ restyle UI + wire lại**, giữ nguyên luồng nghiệp vụ (auth thật, logout…).
- Đây là **recreate pixel-perfect** bằng công nghệ của project (Compose), **không** copy cấu trúc HTML. Đọc HTML/CSS lấy dp/màu/layout; không cần screenshot.
- Tuân thủ toàn bộ rule trong `.claude/rules/` — đặc biệt: `clean-architecture`, `mvi-pattern`, `compose-rules`, `data-layer`, `theming-strings-resources`, `detekt-hygiene`, `navigation`.

## Bước 0 — Đọc handoff & chốt scope với user
1. Giải nén, đọc `README.md` và **đọc hết** file `*.dc.html` chính (file user đang mở khi export) + các file nó import (component/script/css).
2. Liệt kê: **mọi màn hình**, mọi **modal/bottom-sheet/dialog**, **bottom-nav** (mấy tab), overlay (toast, loading), state/data mẫu trong script.
3. **HỎI user trước khi code** (dùng AskUserQuestion) — 3 quyết định hay gặp:
   - **Tầng data**: dùng repository + Fake datasource + dummy JSON (khuyến nghị) hay tạm state trong ViewModel?
   - **Tích hợp**: app này thay luồng chính của base (restyle signin, đổi tab của module main) hay chạy song song/thêm entry point?
   - **Phạm vi**: làm hết các màn hay subset trước?

## Bước 1 — Map từng màn → module (quyết định kiến trúc)
Lập bảng `màn design → đích`:
- Màn trùng chức năng đã có → **reuse module** (authentication/profile/main…), chỉ restyle + đổi text/màu.
- Nhóm màn mới (dashboard, danh sách, chi tiết, lịch sử…) → **tạo feature module mới** cho mỗi nhóm, theo scaffold của project (xem skill tạo feature module nếu có, ví dụ `/new-feature-module`).
- Bottom-nav N tab → cấu hình qua enum top-level-destination + nav bar của module điều phối (mỗi tab trỏ 1 feature graph). Enum tab dùng `@DrawableRes`/`@StringRes` (xem `theming-strings-resources`).

## Bước 2 — Data layer (nếu chọn repository + dummy JSON)
Theo **`.claude/rules/data-layer.md`**: domain model + repository interface → DTO + ApiService + Fake datasource đọc dummy JSON + mapper + repository impl (`@Singleton`, observe qua Flow, write optimistic). Một nguồn sự thật, các tab tự đồng bộ.

## Bước 3 — Theme + strings (làm đúng từ đầu, tránh refactor)
Theo **`.claude/rules/theming-strings-resources.md`**:
- Màu design → thêm token vào palette trung tâm có sẵn (không tạo object màu mới).
- **Mọi text → `stringResource`** từ `core:resource` ngay từ đầu (đừng hardcode rồi extract sau).
- Font: nếu design dùng font đặc thù (vd serif) mà không muốn bundle .ttf → xấp xỉ bằng `FontFamily.Serif`/default và ghi chú; hỏi user nếu cần khớp tuyệt đối.

## Bước 4 — Từng màn theo MVI
Theo `mvi-pattern` + `compose-rules`:
- `<Screen>Contract.kt`: UiState (LCE `@Immutable sealed interface`, dùng `ImmutableList`), **SingleEvent định nghĩa TRONG contract**, mapper `internal fun toXxxUiItem()`.
- `<Screen>ViewModel.kt`: format `emitState`:
  ```kotlin
  private val _uiStateFlow = MutableStateFlow(XUiState.initial)
  val uiStateFlow: StateFlow<XUiState> = _uiStateFlow.asStateFlow()
  private inline fun emitState(f: (XUiState) -> XUiState) = _uiStateFlow.update(f)
  init { observe...() }   // collect repo flow → emitState { ... }
  ```
- `navigation.kt` + `<Screen>Screen.kt` (Route + Screen). Widget dùng lại từ core:ui; component tái dùng nhiều nơi để ở core:ui/widget.
- **Search** (nếu có): dùng `savedStateHandle.getStateFlow(KEY, "")` + `.debounce()` (tham khảo module search sẵn có), lọc qua emitState.

## Bước 5 — Modal
Theo mục Modal trong `compose-rules`:
- Bottom sheet → `AppModalBottomSheet` (Material3, ở core:ui). Không tự vẽ scrim + Box.
- Dialog → tái dùng `DialogCommon` (thêm slot/màu nếu thiếu). Không tự dựng dialog mới.
- Toast/snackbar → dùng `SnackbarManager` sẵn có; ViewModel emit SingleEvent → screen show.

## Bước 6 — Wire + build sạch
- Đăng ký module (`settings.gradle.kts`, `app/build.gradle.kts`), wire graph vào module main/NavHost.
- Trước khi báo xong: **`spotlessApply` → `detekt` → `assembleDebug`** đều xanh (theo `.claude/rules/detekt-hygiene.md` để tránh vòng lặp).

## Bước 7 — Verify trên thiết bị (bắt buộc)
- `adb devices` → `adb install -r <apk>` → mở app.
- `adb exec-out screencap -p > shot.png` từng màn + modal; đọc bằng Read tool, **so với design** (layout/màu/spacing) → sửa lệch tới khi khớp.
- Kiểm chứng luồng data (vd thao tác ở tab A phản ánh ở tab B nếu cùng repository).
- Nếu login dùng backend thật không reachable offline: verify UI signin; tạm trỏ start destination sang màn chính để chụp các tab, **rồi hoàn tác**.

## Checklist tổng
- [ ] Đã hỏi scope/tích hợp/data trước khi code.
- [ ] Mỗi màn ở đúng module; reuse authentication/profile/main; không god-module.
- [ ] Data qua repository + Fake + dummy JSON (không mock trong VM).
- [ ] Màu vào palette có sẵn; text qua `stringResource`; enum `@StringRes`/`@DrawableRes`.
- [ ] ViewModel dùng `emitState`; SingleEvent trong contract; `ImmutableList` trong state.
- [ ] Bottom sheet/dialog dùng/ tái dùng component chuẩn.
- [ ] Luồng auth giữ nguyên (chỉ restyle signin) nếu user không yêu cầu khác.
- [ ] spotless + detekt + build xanh; đã chạy app + screenshot so design.

---
*Ví dụ trong repo hiện tại (tham khảo): module tab `feature/main` (enum top-level destination + nav bar); reuse `feature/authentication`/`feature/profile`; data-layer của `language`.*
