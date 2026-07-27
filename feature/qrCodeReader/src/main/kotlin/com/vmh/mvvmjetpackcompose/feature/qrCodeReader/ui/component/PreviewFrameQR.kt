package com.vmh.mvvmjetpackcompose.feature.qrCodeReader.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme

internal object PreviewFrameQrDefaults {
  const val WidthPercent = 0.56f
  const val Ratio = 1f
}

@Composable
internal fun PreviewFrameQR(painter: Painter, modifier: Modifier = Modifier) {
  Canvas(modifier = modifier) {
    val canvasWidth = size.width
    val canvasHeight = size.height

    val width = canvasWidth * PreviewFrameQrDefaults.WidthPercent
    val height = width / PreviewFrameQrDefaults.Ratio

    val x = (canvasWidth - width) / 2
    val y = (canvasHeight - height) / 2
    val xRight = x + width
    val yBottom = y + height

    val sizeLine = 35.dp.toPx()
    // draw top left
    drawLine(
      color = Color.White,
      start = Offset(x = x, y = y),
      end = Offset(x = x + sizeLine, y = y),
      strokeWidth = Stroke.HairlineWidth,
      cap = StrokeCap.Butt,
    )

    drawLine(
      color = Color.White,
      start = Offset(x = x, y = y),
      end = Offset(x = x, y = y + sizeLine),
      strokeWidth = Stroke.HairlineWidth,
      cap = StrokeCap.Butt,
    )

    // draw bottom left
    drawLine(
      color = Color.White,
      start = Offset(x = x, y = yBottom),
      end = Offset(x = x, y = yBottom - sizeLine),
      strokeWidth = Stroke.HairlineWidth,
      cap = StrokeCap.Butt,
    )

    drawLine(
      color = Color.White,
      start = Offset(x = x, y = yBottom),
      end = Offset(x = x + sizeLine, y = yBottom),
      strokeWidth = Stroke.HairlineWidth,
      cap = StrokeCap.Butt,
    )

    // draw top right
    drawLine(
      color = Color.White,
      start = Offset(x = xRight, y = y),
      end = Offset(x = xRight - sizeLine, y = y),
      strokeWidth = Stroke.HairlineWidth,
      cap = StrokeCap.Butt,
    )

    drawLine(
      color = Color.White,
      start = Offset(x = xRight, y = y),
      end = Offset(x = xRight, y = y + sizeLine),
      strokeWidth = Stroke.HairlineWidth,
      cap = StrokeCap.Butt,
    )

    // draw bottom right
    drawLine(
      color = Color.White,
      start = Offset(x = xRight, y = yBottom),
      end = Offset(x = xRight, y = yBottom - sizeLine),
      strokeWidth = Stroke.HairlineWidth,
      cap = StrokeCap.Butt,
    )

    drawLine(
      color = Color.White,
      start = Offset(x = xRight, y = yBottom),
      end = Offset(x = xRight - sizeLine, y = yBottom),
      strokeWidth = Stroke.HairlineWidth,
      cap = StrokeCap.Butt,
    )

    translate(left = x + 80, top = y + 70) {
      with(painter) {
        draw(
          size = Size(
            width = width - 160,
            height = height - 140,
          ),
        )
      }
    }
  }
}

@Preview
@Composable
private fun PreviewFrameQRPreview() {
  MVVMJetpackComposeTheme {
    PreviewFrameQR(
      modifier = Modifier.fillMaxSize(),
      painter = painterResource(id = CoreResourceR.drawable.ic_qrcode),
    )
  }
}
