// Design-handoff pixel/opacity values recreated from the Material 3 SignIn/SignUp mockup.
@file:Suppress("MagicNumber")

package com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.feature.authentication.ui.common.AuthTheme

/** A single soft cloud drifting horizontally. */
@Composable
private fun DriftingCloud(
  width: Dp,
  height: Dp,
  cloudAlpha: Float,
  driftDurationMillis: Int,
  reversed: Boolean,
  modifier: Modifier = Modifier,
) {
  val transition = rememberInfiniteTransition(label = "cloud-drift")
  val drift by transition.animateFloat(
    initialValue = if (reversed) 14f else 0f,
    targetValue = if (reversed) 0f else 14f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = driftDurationMillis),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "cloud-drift-offset",
  )
  Box(
    modifier = modifier
      .offset(x = drift.dp)
      .size(width = width, height = height)
      .alpha(cloudAlpha)
      .background(color = MVVMJetPackComposeColors.NeutralWhite, shape = RoundedCornerShape(20.dp)),
  )
}

/** The three decorative clouds pinned to the top of the auth backgrounds. */
@Composable
private fun AuthClouds(modifier: Modifier = Modifier) {
  Box(modifier = modifier.fillMaxSize()) {
    DriftingCloud(
      modifier = Modifier
        .align(Alignment.TopStart)
        .offset(x = 24.dp, y = 36.dp),
      width = 84.dp,
      height = 30.dp,
      cloudAlpha = 0.9f,
      driftDurationMillis = 7000,
      reversed = false,
    )
    DriftingCloud(
      modifier = Modifier
        .align(Alignment.TopStart)
        .offset(x = 52.dp, y = 62.dp),
      width = 52.dp,
      height = 22.dp,
      cloudAlpha = 0.75f,
      driftDurationMillis = 9000,
      reversed = true,
    )
    DriftingCloud(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .offset(x = (-28).dp, y = 90.dp),
      width = 68.dp,
      height = 26.dp,
      cloudAlpha = 0.85f,
      driftDurationMillis = 8000,
      reversed = false,
    )
  }
}

/** Full-screen vertical gradient (from [AuthTheme.extendedColors]) with drifting clouds. */
@Composable
internal fun AuthGradientBackground(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
  val extendedColors = AuthTheme.extendedColors
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          listOf(extendedColors.backgroundTop, extendedColors.backgroundMid, extendedColors.backgroundBottom),
        ),
      ),
  ) {
    AuthClouds()
    content()
  }
}
