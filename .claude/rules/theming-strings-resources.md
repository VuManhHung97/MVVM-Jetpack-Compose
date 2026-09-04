---
description: Theming, strings & resources — màu vào palette trung tâm (không tạo theme mới), mọi text qua stringResource + i18n, enum dùng @StringRes/@DrawableRes.
globs: feature/**/*.kt, core/ui/**/*.kt, core/resource/**
---

# Theming, Strings & Resources

Rule cho màu, text và resource khi thêm màn/tính năng mới. Mục tiêu: dùng lại hệ theme + resource sẵn có, i18n từ đầu, enum không nhúng text/icon.

## 1. Màu — mở rộng theme cũ, KHÔNG tạo theme mới

- App có sẵn 1 **palette object trung tâm** (ví dụ `MVVMJetPackComposeColors` trong `core:ui/theme/Color.kt`) và/hoặc Material color scheme. Màu mới cho tính năng/app mới → **thêm token vào object trung tâm đó**, đặt tên rõ nghĩa (`PageTop`, `CardBg`, `Accent`, `GoldValue`…).
- **Không** tạo object/file màu song song (kiểu `FooColors`) chỉ cho một feature. App cũ chỉ là nền — cập nhật/ bổ sung token vào theme hiện có.
- Brush/gradient/font-family bổ trợ đặt trong **cùng package theme** (`core:ui/theme/`), không tạo "theme" riêng.
- Composable tham chiếu token trực tiếp (`MVVMJetPackComposeColors.Accent`). Không hardcode `Color(0xFF...)` rải rác trong screen.
- Chỉ đổi Material `lightScheme/darkScheme` khi thực sự cần đổi nền toàn app; nếu screen tự vẽ background bằng token thì không cần.

### Màu tương ứng role Material → ánh xạ vào `ColorScheme`, đừng tạo token semantic song song
- Rút tập màu **từ UI design** (nền, chữ, nút, viền, focus/lỗi…) rồi đối chiếu theme app. Màu nào ứng với **role Material** (primary/secondary/tertiary/error/surface/outline…): **cập nhật thẳng giá trị token của `ColorScheme` sẵn có** (`primaryLight`, `secondaryLight`, `surfaceLight`, … hoặc bản `*Dark`) và cho Composable đọc `MaterialTheme.colorScheme.*`. **KHÔNG** thêm một loạt token semantic mới (`XxxPrimary`/`XxxSurface`…) vào palette — trùng vai trò Material, sẽ phải refactor.
- App đang cố định 1 theme (vd dark) mà một nhóm màn cần theme khác → dùng **scheme còn lại** (vd cập nhật `lightScheme` rồi render nhóm màn đó với `useDarkTheme = false`), không dựng `ColorScheme`/object màu riêng.
- Chọn role đúng cho từng phần tử ở **call-site của màn** (vd nút chính `colorScheme.primary`; màn khác dùng `colorScheme.tertiary`) thay vì nhét logic chọn role vào trong component tái dùng.

### Màu ngoài role Material & màu chỉ 1 màn dùng → đặt gần nơi dùng
- Màu **không có slot Material** (minh hoạ: mascot, gradient nền, bóng nút 3D, brand mark Google/Facebook): không đưa vào palette trung tâm. Nếu **chỉ một màn dùng** → khai báo **ngay trong package/file của màn đó** (`private val …`). Dùng ở ≥ 2 màn mới đưa lên theme dùng chung (vd nhóm "extended colors" cạnh theme).

```kotlin
// core:ui/theme/Color.kt — thêm vào object có sẵn
object MVVMJetPackComposeColors {
  // ... token cũ ...
  // ---- <Tên app/feature> palette ----
  val PageTop = Color(0xFFFAF4E6)
  val Accent = Color(0xFFB4893C)
  val Ink = Color(0xFF2E2618)
}
// Brush/serif để chung file theme, tham chiếu token trên.
```

## 2. Text — mọi chuỗi hiển thị qua `stringResource` NGAY TỪ ĐẦU

- **Không** hardcode chuỗi tiếng Việt/English trong Composable/ViewModel. Định nghĩa trong `core:resource` `res/values/strings.xml`, dùng `stringResource(R.string.x)`.
- Chuỗi động: dùng placeholder positional và truyền tham số:
  ```xml
  <string name="deposit_success">Đã nạp %1$s Xu cho %2$s</string>
  ```
  ```kotlin
  stringResource(R.string.deposit_success, amountFormatted, username)
  ```
- Trong coroutine/không có scope Composable (vd xử lý SingleEvent) → `context.getString(R.string.x, args)`.
- **Đa ngôn ngữ**: cứ định nghĩa đủ key ở `values/` (ngôn ngữ mặc định của project); bản dịch bỏ vào `values-<locale>/` sau. Cơ chế `stringResource` đã sẵn sàng đa ngôn ngữ, không cần code thêm.
- Ký tự `%` trong chuỗi **không dùng làm format** phải để đơn (`1,7% tổng số`), KHÔNG viết `%%` (getString không-arg sẽ in ra `%%`).

## 3. Enum / data hiển thị — dùng `@StringRes` / `@DrawableRes`, không nhúng trực tiếp

- Enum không giữ text/glyph string hay icon char. Giữ **resId**:
  ```kotlin
  enum class StatusFilter(@param:StringRes val labelResId: Int, val status: Status?) {
    All(R.string.filter_all, null),
    Active(R.string.filter_active, Status.Active),
  }

  enum class Tab(@param:DrawableRes val iconResId: Int, @param:StringRes val titleResId: Int, val route: String) {
    Home(R.drawable.ic_tab_home, R.string.tab_home, HomeGraphRoute),
  }
  ```
- Screen render: `stringResource(item.labelResId)`, `Icon(ImageVector.vectorResource(item.iconResId), ...)`.
- Data class hiển thị (stat card, row…) cũng giữ `@StringRes`/`@DrawableRes` thay vì String literal, để i18n.
- Icon cần drawable → thêm vector `res/drawable/ic_*.xml` (fillColor `@android:color/white`, tint ở call-site). Không dùng ký tự unicode làm icon nếu enum yêu cầu `@DrawableRes`.

## 4. Tách text khỏi mapper/domain

- Chuỗi ghép có từ ngữ dịch được (vd "Nhân vật · Môn phái · Lv{n}") → **không** ghép sẵn trong mapper (presentation không có Context). Cho UiItem giữ field thô (character, clan, level), format ở Composable bằng resource `"%1$s · %2$s · Lv%3$d"`.
- Dấu phân cách thuần (`·`, `+`, `−`) là ký tự, có thể để trong code hoặc trong resource template — ưu tiên template resource khi cả cụm là câu.

## Checklist
- [ ] Không có `Color(0x...)` hay object màu mới ngoài palette trung tâm.
- [ ] Không còn chuỗi hiển thị hardcode; tất cả qua `stringResource`/`getString`.
- [ ] Enum/stat data dùng `@StringRes`/`@DrawableRes`.
- [ ] `%` non-format để đơn; string động dùng `%1$s`.

---
*Ví dụ trong repo hiện tại (tham khảo): palette `MVVMJetPackComposeColors`; `strings.xml` ở `core:resource`; enum tab `MainTopScreenTopLevelDestination` dùng `@DrawableRes`/`@StringRes`.*
