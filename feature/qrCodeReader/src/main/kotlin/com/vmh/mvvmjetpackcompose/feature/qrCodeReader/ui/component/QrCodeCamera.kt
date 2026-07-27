package com.vmh.mvvmjetpackcompose.feature.qrCodeReader.ui.component

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.viewinterop.AndroidView
import com.scandit.datacapture.barcode.capture.BarcodeCapture
import com.scandit.datacapture.barcode.capture.BarcodeCaptureListener
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSession
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSettings
import com.scandit.datacapture.barcode.data.Symbology
import com.scandit.datacapture.core.area.RectangularLocationSelection
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.core.common.feedback.Feedback
import com.scandit.datacapture.core.common.geometry.FloatWithUnit
import com.scandit.datacapture.core.common.geometry.MeasureUnit
import com.scandit.datacapture.core.common.geometry.SizeWithUnit
import com.scandit.datacapture.core.data.FrameData
import com.scandit.datacapture.core.source.Camera
import com.scandit.datacapture.core.source.FrameSourceState
import com.scandit.datacapture.core.ui.DataCaptureView
import com.scandit.datacapture.core.ui.LogoStyle
import com.vmh.mvvmjetpackcompose.feature.qrCodeReader.BuildConfig
import java.util.concurrent.atomic.AtomicReference

@SuppressLint("ConfigurationScreenWidthHeight")
@Suppress("MagicNumber")
@Composable
internal fun QrCodeCamera(isCameraEnabled: Boolean, onScanSuccess: (String) -> Unit, modifier: Modifier = Modifier) {
  val configuration = LocalConfiguration.current
  val camera = remember {
    checkNotNull(
      Camera.getDefaultCamera(
        BarcodeCapture.createRecommendedCameraSettings().apply {
          shouldPreferSmoothAutoFocus = true
        },
      ),
    ) {
      "Sample depends on a camera, which failed to initialize."
    }
  }
  val barcodeCapture = remember { AtomicReference<BarcodeCapture?>() }

  val dataCaptureContext = remember {
    DataCaptureContext.forLicenseKey(BuildConfig.SCANDIT_LICENSE_KEY)
      .apply {
        setFrameSource(camera)
      }
  }

  val barcodeCaptureListener = remember {
    object : BarcodeCaptureListener {
      override fun onBarcodeScanned(barcodeCapture: BarcodeCapture, session: BarcodeCaptureSession, data: FrameData) {
        val barcodeData = session.newlyRecognizedBarcodes.firstOrNull()?.data ?: return
        barcodeCapture.isEnabled = false
        onScanSuccess(barcodeData)
      }
    }
  }

  LaunchedEffect(isCameraEnabled) {
    barcodeCapture.get()?.isEnabled = isCameraEnabled
  }

  LaunchedEffect(isCameraEnabled) {
    camera.switchToDesiredState(
      if (isCameraEnabled) {
        FrameSourceState.ON
      } else {
        FrameSourceState.OFF
      },
    )
  }

  AndroidView(
    modifier = modifier,
    factory = { context ->
      val widthInDp = configuration.screenWidthDp
      val heightInDp = configuration.screenHeightDp
      val widthPreview = widthInDp * PreviewFrameQrDefaults.WidthPercent
      val heightPreview = widthPreview / PreviewFrameQrDefaults.Ratio

      val barcodeCaptureSettings = BarcodeCaptureSettings().apply {
        enableSymbology(Symbology.QR, true)
        getSymbologySettings(Symbology.CODE39).apply {
          activeSymbolCounts = hashSetOf(7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
        }
        locationSelection = RectangularLocationSelection.withSize(
          SizeWithUnit(
            FloatWithUnit(PreviewFrameQrDefaults.WidthPercent, MeasureUnit.FRACTION),
            FloatWithUnit(heightPreview / heightInDp, MeasureUnit.FRACTION),
          ),
        )
      }

      barcodeCapture.set(
        BarcodeCapture.forDataCaptureContext(
          dataCaptureContext,
          barcodeCaptureSettings,
        ).apply {
          addListener(barcodeCaptureListener)
          feedback.success = Feedback(vibration = null, sound = null)
        },
      )

      DataCaptureView
        .newInstance(
          context = context,
          dataCaptureContext,
        )
        .apply {
          logoStyle = LogoStyle.MINIMAL
        }
    },
    onRelease = {
      barcodeCapture.getAndSet(null)
        ?.run {
          isEnabled = false
          removeListener(barcodeCaptureListener)
          dataCaptureContext.removeMode(this)
        }
    },
    update = {},
  )
}
