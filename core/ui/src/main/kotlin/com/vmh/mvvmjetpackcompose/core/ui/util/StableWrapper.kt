package com.vmh.mvvmjetpackcompose.core.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember

@Stable
data class StableWrapper<T>(val value: T)

@Composable
fun <T> rememberStableValueAsWrapper(value: T): StableWrapper<T> = remember(value) { StableWrapper(value) }
