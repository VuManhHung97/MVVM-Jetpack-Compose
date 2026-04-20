package com.vmh.mvvmjetpackcompose.core.common.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.annotation.IntRange
import androidx.core.graphics.scale
import java.io.ByteArrayOutputStream

private const val MaxBitmapQuality = 100

fun Bitmap.compressToJpegBytes(@IntRange(from = 0, to = 100) quality: Int): ByteArray =
  ByteArrayOutputStream().use { baos ->
    this.compress(Bitmap.CompressFormat.JPEG, quality, baos)
    baos.toByteArray()
  }

fun Bitmap.compressWithEstimateAndResize(
  maxBytes: Int,
  @IntRange(from = 0, to = 100) minQuality: Int = 10,
  qualityStep: Int = 5,
  resizeFactor: Float = 0.8f,
  maxIterations: Int = 20,
): ByteArray {
  val originalCompressedBytes = this.compressToJpegBytes(quality = MaxBitmapQuality)
  if (originalCompressedBytes.size <= maxBytes) return originalCompressedBytes

  var quality = estimateInitialQuality(
    currentSize = originalCompressedBytes.size,
    maxBytes = maxBytes,
    minQuality = minQuality,
  )
  var bitmap = this
  var compressedBytes: ByteArray
  var iterationCount = 0

  while (true) {
    if (iterationCount++ >= maxIterations) {
      error("Cannot compress image below $maxBytes bytes")
    }

    compressedBytes = bitmap.compressToJpegBytes(quality)

    when {
      compressedBytes.size > maxBytes && quality > minQuality ->
        quality = (quality - qualityStep).coerceAtLeast(minQuality)

      compressedBytes.size > maxBytes -> {
        val oldBitmap = bitmap
        bitmap = oldBitmap.resize(factor = resizeFactor)

        // Recycle the old bitmap if it's not the same as the new one and not already recycled
        if (oldBitmap != this@compressWithEstimateAndResize && oldBitmap != bitmap && !oldBitmap.isRecycled) {
          oldBitmap.recycle()
        }

        quality = MaxBitmapQuality
      }

      else -> return compressedBytes
    }
  }
}

fun Bitmap.resize(factor: Float): Bitmap = this.scale(
  width = (this.width * factor).toInt(),
  height = (this.height * factor).toInt(),
)

fun Context.decodeBitmap(uri: Uri): Bitmap? = this.contentResolver
  .openInputStream(uri)
  ?.use { BitmapFactory.decodeStream(it) }

private fun estimateInitialQuality(
  currentSize: Int,
  maxBytes: Int,
  @IntRange(from = 0, to = 100) minQuality: Int,
): Int {
  val ratio = maxBytes.toFloat() / currentSize
  return (ratio * MaxBitmapQuality).toInt().coerceIn(minQuality..MaxBitmapQuality)
}
