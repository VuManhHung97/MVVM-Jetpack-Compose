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

## Bước 0 — Đọc handoff, phân tích màu & chốt scope với user
1. Giải nén, `find <bundle> -type f` rồi **đọc hết mọi file** (README + file `*.dc.html` chính + file nó import + file đứng riêng). Nếu bundle **có** file theme/design-token/style riêng thì đọc luôn — nhưng **đừng phụ thuộc vào nó**: nhiều handoff không kèm bảng màu.
2. **Phân tích mã màu từ chính UI design** (không chờ có "bảng màu"): rà từng màn/component, liệt kê **tập màu design thực sự dùng** theo mục đích — nền/gradient, chữ (tiêu đề/nhãn/phụ), nút & trạng thái nhấn, viền & divider, focus/lỗi/thành công, màu minh hoạ (icon/illustration/brand). Gom trùng, đặt mục đích cho từng màu. Đây là input cho Bước 3 (đối chiếu theme).
3. Liệt kê: **mọi màn hình**, mọi **modal/bottom-sheet/dialog**, **bottom-nav** (mấy tab), overlay (toast, loading), state/data mẫu trong script.
4. **HỎI user trước khi code** (dùng AskUserQuestion) — 3 quyết định hay gặp:
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
- **Đối chiếu tập màu (rút ở Bước 0.2) với theme sẵn có → tái dùng / thêm / update.** Với mỗi màu design: tìm trong `ColorScheme`/palette của app xem **đã có** màu đúng vai trò chưa. Đã có → dùng lại. Chưa có / lệch giá trị → **update token sẵn có** hoặc thêm token đúng chỗ. Không phụ thuộc việc handoff có "bảng màu"; nguồn sự thật là **màu trên UI** + **theme của app**.
- **Ưu tiên ánh xạ vào `ColorScheme` Material sẵn có, KHÔNG tạo một loạt token semantic song song.** App đã có cấu trúc theme (`lightScheme`/`darkScheme` build từ token `*Light`/`*Dark`).
  - Design có bảng màu Material (role primary/secondary/tertiary/error/surface/outline…) → **cập nhật thẳng giá trị các token `*Light`/`*Dark` sẵn có** (hoặc dùng đúng role trong scheme). Component đọc `MaterialTheme.colorScheme.*`. **Đừng** thêm nhóm `XxxPrimary`/`XxxSurface`… mới vào palette (trùng vai trò Material → sẽ bị yêu cầu refactor).
  - App đang render 1 theme cố định (vd dark) và bạn cần theme khác cho nhóm màn này → dùng chính scheme còn lại (vd cập nhật `lightScheme` rồi render nhóm màn đó với `useDarkTheme = false`), thay vì dựng `ColorScheme`/object màu riêng.
  - Màu **không có role Material** (minh hoạ: mascot, gradient nền, bóng nút, brand mark) → không nhét vào palette trung tâm. Nếu chỉ **một màn dùng** thì khai báo **ngay trong package/file của màn đó** (dùng nhiều màn mới đưa vào theme dùng chung). Xem "Bài học".
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

### Tách component vừa đủ — đừng chẻ quá nhỏ
- Chỉ tách một Composable ra hàm/file riêng khi nó **được tái sử dụng (≥ 2 nơi)** hoặc là **một đơn vị UI độc lập có ý nghĩa**. Mẩu UI **dùng một lần, chỉ 1–2 dòng** (một nhãn, một dòng chú thích kèm link, một icon bấm…) → **viết thẳng trong parent**, không tạo composable/ file riêng.
- **Đặt tên theo vai trò/chức năng cụ thể** của component — mô tả nó *hiển thị/làm gì* trong ngữ cảnh. **Tránh** tính từ trang trí (`Playful…`, `Fancy…`, `Nice…`) và tên chung chung vô nghĩa. Mỗi component tái dùng = **một file** riêng.

### Không lặp logic đã có trong ViewModel
- Trạng thái **validate / dẫn xuất** (vd đã hợp lệ, nút có enable không, thông báo lỗi) đã được ViewModel tính và phơi trong `UiState` → Composable **đọc thẳng từ state**, **không tính lại** trong màn.

## Bước 5 — Modal
Theo mục Modal trong `compose-rules`:
- Bottom sheet → `AppModalBottomSheet` (Material3, ở core:ui). Không tự vẽ scrim + Box.
- Dialog → tái dùng `DialogCommon` (thêm slot/màu nếu thiếu). Không tự dựng dialog mới.
- Toast/snackbar → dùng `SnackbarManager` sẵn có; ViewModel emit SingleEvent → screen show.

## Bước 6 — Wire + build sạch
- Đăng ký module `api` + `impl` (`settings.gradle.kts`, `app/build.gradle.kts`), thêm `<name>Entry(navigator)` vào `entryProvider { }` của `MainActivity`.
- Trước khi báo xong: **`spotlessApply` → `detekt` → `assembleDebug`** đều xanh (theo `.claude/rules/detekt-hygiene.md` để tránh vòng lặp).

## Bước 7 — Verify trên thiết bị (bắt buộc)
- `adb devices` → `adb install -r <apk>` → mở app.
- `adb exec-out screencap -p > shot.png` từng màn + modal; đọc bằng Read tool, **so với design** (layout/màu/spacing) → sửa lệch tới khi khớp.
- Kiểm chứng luồng data (vd thao tác ở tab A phản ánh ở tab B nếu cùng repository).
- Nếu login dùng backend thật không reachable offline: verify UI signin; tạm trỏ start destination sang màn chính để chụp các tab, **rồi hoàn tác**.

## Bài học (lỗi thực tế — tránh lặp lại, áp dụng cho MỌI module sau)
Rút ra từ các lần refactor tốn thời gian; đây là nguyên tắc chung, không riêng màn/thực thể nào:
1. **Lấy màu sai nguồn.** Nguồn màu là **UI design + theme của app**, không phải "bảng màu trong handoff" (nhiều handoff không có). Rút tập màu **từ chính UI**, rồi **đối chiếu theme app**: đã có thì tái dùng, chưa có/lệch thì thêm/update. Đọc hết file bundle để không sót thông tin, nhưng đừng phụ thuộc vào một file bảng màu.
2. **Tạo token màu semantic song song** trong khi app đã có `ColorScheme`. Màu có vai trò Material → **cập nhật token scheme sẵn có**, component đọc `MaterialTheme.colorScheme.*`. Chỉ màu ngoài role Material (minh hoạ) mới khai báo riêng, và **đặt trong package nơi dùng** nếu chỉ một màn dùng.
3. **Chẻ component quá nhỏ / đặt tên bằng tính từ trang trí.** Chỉ tách khi tái dùng/độc lập; đặt tên theo vai trò-chức năng; UI một-lần thì inline.
4. **Lặp lại logic của ViewModel trong Composable** (tự tính lại validate/derived). Đọc thẳng từ `UiState`.
5. **Chốt scope sai**: handoff vẽ lại màn đã tồn tại là việc **UI-only** (restyle module cũ, giữ ViewModel/luồng), **không** scaffold vertical-slice/module mới. Xác nhận với user nếu command được gọi không khớp bản chất handoff.

Sau khi hoàn thành một handoff, nếu phát hiện lỗi lặp mới → **cập nhật chính file skill này** (mục Bài học + bước liên quan) để lần sau tốt hơn.

## Checklist tổng
- [ ] Đã đọc hết file bundle + **rút tập màu từ UI** + hỏi scope/tích hợp/data trước khi code.
- [ ] Chốt đúng bản chất: reuse+restyle module cũ hay tạo module mới; không god-module.
- [ ] Data qua repository + Fake + dummy JSON (không mock trong VM).
- [ ] Màu **đối chiếu & ánh xạ vào `ColorScheme` sẵn có** (không thêm token semantic song song); màu minh hoạ chỉ-1-màn để trong package màn đó; text qua `stringResource`; enum `@StringRes`/`@DrawableRes`.
- [ ] Component tách **vừa đủ** (tái dùng/độc lập), tên theo vai trò, UI một-lần inline; mỗi component tái dùng 1 file.
- [ ] Composable **đọc validate/derived state từ ViewModel**, không tính lại.
- [ ] ViewModel dùng `emitState`; SingleEvent trong contract; `ImmutableList` trong state.
- [ ] Bottom sheet/dialog dùng/ tái dùng component chuẩn.
- [ ] Luồng nghiệp vụ giữ nguyên (chỉ restyle) nếu user không yêu cầu khác.
- [ ] spotless + detekt + build xanh; đã chạy app + screenshot so design.

---
*Ví dụ trong repo hiện tại (tham khảo): module tab `feature/main` (enum top-level destination + nav bar); reuse `feature/authentication`/`feature/profile`; data-layer của `language`.*
