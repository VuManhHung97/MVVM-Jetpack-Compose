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

private const val ReferenceSize = 112f

/**
 * The sun mascot for the SignIn screen. Its eyes close while the user types a hidden password,
 * mirroring the prototype's "peeking" behaviour. Colors come from [AuthTheme.extendedColors].
 */
@Composable
internal fun SunMascot(eyesClosed: Boolean, modifier: Modifier = Modifier) {
  val colors = AuthTheme.extendedColors
  val translation = rememberFloatyTranslation()
  val face = MascotFace(
    referenceSize = ReferenceSize,
    eyeColor = colors.mascotEye,
    eyeOpenTop = 42f,
    eyeOpenSide = 30f,
    eyeOpenWidth = 13f,
    eyeOpenHeight = 18f,
    eyeClosedTop = 50f,
    eyeClosedSide = 27f,
    eyeClosedWidth = 19f,
    cheekTop = 62f,
    cheekSide = 18f,
    smileTop = 66f,
    smileWidth = 30f,
  )

  Canvas(
    modifier = modifier
      .size(112.dp)
      .graphicsLayer { translationY = translation },
  ) {
    val scale = size.minDimension / ReferenceSize
    val faceRadius = 56f * scale
    val center = Offset(size.width / 2f, size.height / 2f)

    drawCircle(color = colors.mascotGlow, radius = faceRadius + 12f * scale, center = center)
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
