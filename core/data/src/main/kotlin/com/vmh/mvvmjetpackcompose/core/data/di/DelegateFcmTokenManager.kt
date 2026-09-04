package com.vmh.mvvmjetpackcompose.core.data.di

import javax.inject.Qualifier

/**
 * Marks the raw [com.vmh.mvvmjetpackcompose.core.data.repository.fcm.DefaultFcmTokenManager] binding
 * that [com.vmh.mvvmjetpackcompose.core.data.repository.fcm.SynchronizedFcmTokenManager] wraps, so it
 * can be distinguished from the public (synchronized) [FcmTokenManager] binding.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
internal annotation class DelegateFcmTokenManager
