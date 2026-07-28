# Feature `camera` — Sơ đồ kiến trúc

Tài liệu mô tả module `feature/camera`: chụp ảnh bằng CameraX → review ảnh → gọi `updatePhoto()` (hiện đang mock).

> Các sơ đồ dưới viết bằng Mermaid — GitHub, VS Code (extension Markdown Preview Mermaid) và IntelliJ đều render được.

---

## 1. Kiến trúc — ai giữ CameraX, ai giữ state

```mermaid
flowchart TB
  subgraph UI["ui/camera — tầng UI"]
    Route["CameraRoute<br/>· permission launcher<br/>· collect eventFlow"]
    Screen["CameraScreen (stateless)"]
    Preview["CameraPreview"]
    Captured["CapturedPhotoContent"]
    ErrorContent["CommonAppErrorContent"]
    Controller["CameraCaptureController @Stable<br/>· Preview + ImageCapture use case<br/>· bind() / unbind()<br/>· capture(): Result&lt;Uri, AppError&gt;<br/>· onDisplayRotationChange()"]
  end

  subgraph Presentation["presentation/camera — tầng MVI"]
    VM["CameraViewModel<br/>uiStateFlow / eventFlow<br/>onPhotoCapture · onPhotoRetake · updatePhoto"]
  end

  subgraph Framework["CameraX / hệ thống"]
    Provider["ProcessCameraProvider"]
    Cache[("cacheDir/photos/*.jpg")]
  end

  VM -- "uiState + event" --> Route
  Route -- "gọi hàm ViewModel" --> VM
  Route --> Screen
  Screen -- "Capturing" --> Preview
  Screen -- "Captured" --> Captured
  Screen -- "Error" --> ErrorContent
  Preview --> Controller
  Controller --> Provider
  Provider --> Cache
```

**Vì sao CameraX không nằm trong ViewModel:** `bindToLifecycle()` cần một `LifecycleOwner`, thứ chỉ tồn tại ở tầng UI. ViewModel chỉ nhận `Uri` của ảnh đã lưu — nó hoàn toàn không biết CameraX tồn tại, nên test được và thay được nguồn ảnh bất cứ lúc nào.

---

## 2. State machine

```mermaid
stateDiagram-v2
  [*] --> Capturing

  Capturing --> Captured: capture() thành công<br/>onPhotoCapture(uri)
  Capturing --> Error: capture() lỗi<br/>onPhotoCaptureFailure(err)

  Captured --> Capturing: onPhotoRetake()<br/>(+ xoá file cũ)
  Error --> Capturing: onPhotoRetake()

  state Captured {
    [*] --> Idle
    Idle --> Updating: updatePhoto()<br/>isUpdating = true
    Updating --> Idle: mock delay 1.5s xong<br/>isUpdating = false
  }
```

Nguyên tắc phân biệt:

| Thứ | Model ở đâu | Lý do |
|---|---|---|
| `isUpdating` | **UiState** | trạng thái liên tục, phải sống qua recomposition |
| `PhotoUpdateSuccess` / `PhotoUpdateFailure` | **SingleEvent** (EventChannel) | one-shot: snackbar, điều hướng — không được lặp lại khi recompose |

---

## 3. Sequence — từ lúc bấm shutter đến khi bấm "Use photo"

```mermaid
sequenceDiagram
  actor User
  participant P as CameraPreview
  participant C as CameraCaptureController
  participant X as CameraX
  participant VM as CameraViewModel
  participant R as CameraRoute

  User->>P: bấm shutter
  P->>C: capture() (suspend)
  C->>X: takePicture(outputFileOptions, executor, cb)
  X->>X: ghi JPEG vào cacheDir/photos
  X-->>C: onImageSaved(uri)
  C-->>P: Ok(uri)
  P->>VM: onPhotoCapture(uri)
  VM->>VM: state = Captured(uri, isUpdating = false)

  Note over P,X: CameraPreview rời composition<br/>onDispose → controller.unbind() → camera tắt

  User->>VM: bấm "Use photo" → updatePhoto()
  VM->>VM: isUpdating = true
  VM-->>VM: delay(1500) ← MOCK<br/>(chỗ sau này gọi repository)
  VM->>R: SingleEvent.PhotoUpdateSuccess
  R->>User: snackbar "Photo updated"
  R->>R: popBackStack()
```

---

## 4. Navigation

```mermaid
flowchart LR
  NavHost["NavHost<br/>(MainActivity)"]
  MainGraph["mainGraph"]
  ProfileGraph["profileGraph"]
  ProfileRoute["ProfileRoute"]
  CameraScreen["cameraScreen<br/>route = camera_route"]

  NavHost --> MainGraph
  NavHost --> CameraScreen
  MainGraph --> ProfileGraph
  ProfileGraph --> ProfileRoute
  ProfileRoute -- "click hàng avatar<br/>(ProfileUiItem.Profile.Info)" --> CameraScreen
  CameraScreen -- "onNavigateBack = popBackStack()" --> ProfileRoute
```

Callback `onNavigateToCameraScreen` được truyền xuyên tầng: `MainActivity` → `mainGraph` → `profileGraph` → `ProfileRoute`. Feature không tự giữ `NavController`.

---

## 5. Vòng đời CameraX cần chú ý

```mermaid
flowchart LR
  A["LaunchedEffect(previewView)"] --> B["awaitInstance(context)"]
  B --> C["unbindAll()"]
  C --> D["bindToLifecycle(owner, BACK_CAMERA,<br/>preview, imageCapture)"]
  D --> E["camera chạy"]
  E -- "rời composition" --> F["onDispose → unbind()"]

  G["DisplayManager.DisplayListener"] -. "xoay máy" .-> H["imageCapture.targetRotation = rotation"]
```

| Bẫy | Cách xử lý trong module này |
|---|---|
| Bind lại use case đang bound → exception | luôn `unbindAll()` trước `bindToLifecycle()` |
| CameraX bind theo **lifecycle**, không theo composition → camera vẫn mở khi review ảnh | `DisposableEffect` gọi `controller.unbind()` |
| Activity khoá orientation → ảnh lưu bị lệch 90° | `DisplayListener` cập nhật `targetRotation` |
| `Executor` của `takePicture` bị rò | `Executors.newSingleThreadExecutor()` + `shutdown()` trong `onDispose` |
| Quyền cấp từ màn Settings không được nhận lại | `LifecycleResumeEffect` đọc lại permission mỗi lần resume |

---

## 6. Chỗ sẽ đổi khi có API thật

```mermaid
flowchart TB
  subgraph Now["Hiện tại (mock)"]
    VM1["ViewModel.updatePhoto()"] --> D1["delay(1500) + Ok"]
  end

  subgraph Later["Sau khi có backend"]
    VM2["ViewModel.updatePhoto()"] --> Repo["PhotoRepository<br/>core:domain"]
    Repo --> Impl["DefaultPhotoRepository<br/>core:data"]
    Impl --> DS["PhotoRemoteDataSource<br/>core:network"]
    DS --> Fake["FakePhotoRemoteDataSource<br/>đọc dummy JSON"]
    DS --> Real["RealPhotoRemoteDataSource<br/>ApiService"]
  end

  Now -.->|"đổi ruột updatePhoto()"| Later
```

Chỉ thân hàm `updatePhoto()` thay đổi — UI, state machine và `CameraCaptureController` giữ nguyên. Xem [`.claude/rules/data-layer.md`](../.claude/rules/data-layer.md) cho pattern đầy đủ.

---

## 7. Bản đồ file

```
feature/camera/src/main/
├── AndroidManifest.xml                    CAMERA permission + uses-feature
└── kotlin/.../feature/camera/
    ├── presentation/camera/
    │   ├── CameraContract.kt              UiState (LCE) + SingleEvent
    │   ├── CameraViewModel.kt             updatePhoto() mock nằm ở đây
    │   └── navigation/navigation.kt       CameraRoutePattern, cameraScreen()
    └── ui/camera/
        ├── CameraScreen.kt                CameraRoute + CameraScreen + permission
        ├── CameraCaptureController.kt     toàn bộ CameraX
        └── component/
            ├── CameraPreview.kt           AndroidView + PreviewView + shutter
            ├── CapturedPhotoContent.kt    ảnh + Retake / Use photo
            └── CameraPermissionContent.kt rationale + mở Settings
```
