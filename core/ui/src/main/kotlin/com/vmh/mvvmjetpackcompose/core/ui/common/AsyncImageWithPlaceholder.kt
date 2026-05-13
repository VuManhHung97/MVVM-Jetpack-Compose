package com.vmh.mvvmjetpackcompose.core.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter.State
import coil3.request.ImageRequest
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR

@Composable
fun AsyncImageWithPlaceholder(
  data: Any?,
  modifier: Modifier = Modifier,
  @DrawableRes placeholderResId: Int = CoreResourceR.drawable.ic_default_image,
  onSuccess: ((State.Success) -> Unit)? = null,
  contentScale: ContentScale = ContentScale.Crop,
) {
  val context = LocalContext.current
  val imageRequest = remember(data, context) {
    ImageRequest.Builder(context)
      .data(data)
      .build()
  }

  AsyncImage(
    modifier = modifier,
    model = imageRequest,
    placeholder = painterResource(id = placeholderResId),
    error = painterResource(id = placeholderResId),
    contentDescription = null,
    contentScale = contentScale,
    onSuccess = onSuccess,
  )
}
