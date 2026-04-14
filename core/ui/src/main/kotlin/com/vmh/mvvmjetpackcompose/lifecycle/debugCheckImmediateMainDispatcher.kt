package com.vmh.mvvmjetpackcompose.lifecycle

import com.vmh.mvvmjetpackcompose.core.ui.BuildConfig
import kotlin.coroutines.ContinuationInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext

suspend fun debugCheckImmediateMainDispatcher() {
  if (BuildConfig.DEBUG) {
    val interceptor = currentCoroutineContext()[ContinuationInterceptor]

    check(interceptor === Dispatchers.Main.immediate) {
      "Expected ContinuationInterceptor to be Dispatchers.Main.immediate but was $interceptor"
    }
  }
}
