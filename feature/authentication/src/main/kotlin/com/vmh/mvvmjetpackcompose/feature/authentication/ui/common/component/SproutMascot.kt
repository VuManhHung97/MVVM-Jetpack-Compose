// Design-handoff pixel/opacity values recreated from the Material 3 SignIn/SignUp mockup.
@file:Suppress("MagicNumber")

package com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.AuthTheme

private const val ReferenceSize = 100f

/**
 * The sprout mascot for the SignUp screen — a leafy face that also closes its eyes while a hidden
 * password is being typed. Colors come from [AuthTheme.extendedColors].
 */
@Composable
internal fun SproutMascot(eyesClosed: Boolean, modifier: Modifier = Modifier) {
  val colors = AuthTheme.extendedColors
  val translation = rememberFloatyTranslation()
  val face = MascotFace(
    referenceSize = ReferenceSize,
    eyeColor = colors.mascotEye,
    eyeOpenTop = 38f,
    eyeOpenSide = 26f,
    eyeOpenWidth = 12f,
    eyeOpenHeight = 16f,
    eyeClosedTop = 45f,
    eyeClosedSide = 23f,
    eyeClosedWidth = 18f,
    cheekTop = 56f,
    cheekSide = 15f,
    smileTop = 58f,
    smileWidth = 26f,
  )

  Canvas(
    modifier = modifier
      .size(100.dp)
      .graphicsLayer { translationY = translation },
  ) {
    val scale = size.minDimension / ReferenceSize
    val faceRadius = 50f * scale
    val center = Offset(size.width / 2f, size.height / 2f)

    drawMascotLeaf(
      center = center,
      faceRadius = faceRadius,
      angleDegrees = -28f,
      color = colors.mascotLeafDark,
      scale = scale,
    )
    drawMascotLeaf(
      center = center,
      faceRadius = faceRadius,
      angleDegrees = 28f,
      color = colors.mascotLeafLight,
      scale = scale,
    )

    drawCircle(color = colors.mascotGlow, radius = faceRadius + 10f * scale, center = center)
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(colors.mascotHighlight, colors.mascotBase),
        center = Offset(size.width * 0.35f, size.height * 0.3f),
        radius = faceRadius * 1.3f,
      ),
      radius = faceRadius,
      center = center,
    )
    drawMascotFace(face = face, scale = scale, eyesClosed = eyesClosed, cheekColor = colors.cheek)
  }
}
