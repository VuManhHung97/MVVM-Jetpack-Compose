package com.vmh.mvvmjetpackcompose.core.notification.internal

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.google.android.gms.tasks.Task
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Awaits a Google Play Services [Task] and adapts it to the project's [Result]/[AppError] model,
 * so the module never leans on `kotlinx-coroutines-play-services` or leaks a raw [Task] outward.
 *
 * A failed or cancelled task is mapped to [AppError.UnknownException] — Firebase messaging failures
 * (token fetch, topic subscription) are non-fatal and carry no domain-specific error subtype.
 */
internal suspend fun <T> Task<T>.awaitResult(): Result<T, AppError> = suspendCancellableCoroutine { continuation ->
  addOnCompleteListener { task ->
    when {
      task.isCanceled -> continuation.resume(Err(AppError.UnknownException(task.exception)))
      task.isSuccessful -> continuation.resume(Ok(task.result))
      else -> continuation.resume(Err(AppError.UnknownException(task.exception)))
    }
  }
}
