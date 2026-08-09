// Design-handoff pixel/opacity values recreated from the Material 3 SignIn/SignUp mockup.
@file:Suppress("MagicNumber")

package com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate

private const val FloatDurationMillis = 4000
private const val FloatTranslationPx = -26f

/**
 * A gentle up-and-down bobbing shared by both mascots — approximates the `floaty` CSS keyframes
 * from the handoff (translateY 0 → -10px → 0). Apply via `graphicsLayer { translationY = it }`.
 */
@Composable
internal fun rememberFloatyTranslation(): Float {
  val transition = rememberInfiniteTransition(label = "mascot-floaty")
  val translation by transition.animateFloat(
    initialValue = 0f,
    targetValue = FloatTranslationPx,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = FloatDurationMillis),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "mascot-floaty-translation",
  )
  return translation
}

/** Coordinates (against a [MascotFace.referenceSize] square from the handoff CSS) for one mascot. */
internal data class MascotFace(
  val referenceSize: Float,
  val eyeColor: Color,
  val eyeOpenTop: Float,
  val eyeOpenSide: Float,
  val eyeOpenWidth: Float,
  val eyeOpenHeight: Float,
  val eyeClosedTop: Float,
  val eyeClosedSide: Float,
  val eyeClosedWidth: Float,
  val cheekTop: Float,
  val cheekSide: Float,
  val smileTop: Float,
  val smileWidth: Float,
)

/**
 * Draws the eyes, cheeks and smile of a mascot. Coordinates in [face] are multiplied by [scale] to
 * fit the canvas; [eyesClosed] swaps open eyes for calm closed lids.
 */
internal fun DrawScope.drawMascotFace(face: MascotFace, scale: Float, eyesClosed: Boolean, cheekColor: Color) {
  val referenceSize = face.referenceSize
  val eyeColor = face.eyeColor

  if (eyesClosed) {
    val closedHeight = 6f * scale
    val closedLeft = face.eyeClosedSide * scale
    val closedRight = (referenceSize - face.eyeClosedSide - face.eyeClosedWidth) * scale
    drawRoundRect(
      color = eyeColor,
      topLeft = Offset(closedLeft, face.eyeClosedTop * scale),
      size = Size(face.eyeClosedWidth * scale, closedHeight),
      cornerRadius = CornerRadius(closedHeight / 2f),
    )
    drawRoundRect(
      color = eyeColor,
      topLeft = Offset(closedRight, face.eyeClosedTop * scale),
      size = Size(face.eyeClosedWidth * scale, closedHeight),
      cornerRadius = CornerRadius(closedHeight / 2f),
    )
  } else {
    val leftX = face.eyeOpenSide * scale
    val rightX = (referenceSize - face.eyeOpenSide - face.eyeOpenWidth) * scale
    val eyeSize = Size(face.eyeOpenWidth * scale, face.eyeOpenHeight * scale)
    drawOval(color = eyeColor, topLeft = Offset(leftX, face.eyeOpenTop * scale), size = eyeSize)
    drawOval(color = eyeColor, topLeft = Offset(rightX, face.eyeOpenTop * scale), size = eyeSize)
  }

  // Cheeks.
  val cheekWidth = 14f * scale
  val cheekHeight = 9f * scale
  val softCheek = cheekColor.copy(alpha = 0.7f)
  drawOval(
    color = softCheek,
    topLeft = Offset(face.cheekSide * scale, face.cheekTop * scale),
    size = Size(cheekWidth, cheekHeight),
  )
  drawOval(
    color = softCheek,
    topLeft = Offset((referenceSize - face.cheekSide) * scale - cheekWidth, face.cheekTop * scale),
    size = Size(cheekWidth, cheekHeight),
  )

  // Smile — a downward arc stroked along the bottom of the face.
  val smileLeft = (referenceSize / 2f - face.smileWidth / 2f) * scale
  val smileHeight = face.smileWidth * 0.9f * scale
  drawArc(
    color = eyeColor,
    startAngle = 20f,
    sweepAngle = 140f,
    useCenter = false,
    topLeft = Offset(smileLeft, face.smileTop * scale),
    size = Size(face.smileWidth * scale, smileHeight),
    style = Stroke(width = 5f * scale, cap = StrokeCap.Round),
  )
}

/** Draws a single leaf pointing away from the top of a mascot's head. */
internal fun DrawScope.drawMascotLeaf(
  center: Offset,
  faceRadius: Float,
  angleDegrees: Float,
  color: Color,
  scale: Float,
) {
  val leafWidth = 30f * scale
  val leafHeight = 18f * scale
  val anchor = Offset(center.x, center.y - faceRadius + 4f * scale)
  rotate(degrees = angleDegrees, pivot = anchor) {
    drawOval(
      color = color,
      topLeft = Offset(anchor.x - leafWidth, anchor.y - leafHeight),
      size = Size(leafWidth, leafHeight),
    )
  }
}
