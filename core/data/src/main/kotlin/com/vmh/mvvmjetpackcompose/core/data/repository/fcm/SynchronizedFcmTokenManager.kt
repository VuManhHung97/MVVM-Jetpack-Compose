package com.vmh.mvvmjetpackcompose.core.data.repository.fcm

import com.vmh.mvvmjetpackcompose.core.data.di.DelegateFcmTokenManager
import com.vmh.mvvmjetpackcompose.core.domain.repository.FcmTokenManager
import javax.inject.Inject
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * A thread-safe [FcmTokenManager] decorator that serializes every call to the [delegate] through a
 * single [Mutex], so the concurrent launch / login / token-refresh triggers cannot interleave and
 * produce duplicate or out-of-order backend calls.
 *
 * Kept separate from [DefaultFcmTokenManager] so the sync logic stays free of locking concerns.
 */
internal class SynchronizedFcmTokenManager @Inject constructor(
  @param:DelegateFcmTokenManager private val delegate: FcmTokenManager,
) : FcmTokenManager {

  private val mutex = Mutex()

  override suspend fun sendRegistrationToServerOnSuccessfulLogin() =
    mutex.withLock { delegate.sendRegistrationToServerOnSuccessfulLogin() }

  override suspend fun sendRegistrationToServerOnLaunch() =
    mutex.withLock { delegate.sendRegistrationToServerOnLaunch() }

  override suspend fun sendRegistrationToServerOnNewToken(token: String) =
    mutex.withLock { delegate.sendRegistrationToServerOnNewToken(token) }

  override suspend fun clearFcmToken() = mutex.withLock { delegate.clearFcmToken() }
}
