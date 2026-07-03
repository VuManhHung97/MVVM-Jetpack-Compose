package com.vmh.mvvmjetpackcompose.core.data.repository

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getError
import com.github.michaelbull.result.onSuccess
import com.google.protobuf.UInt64Value
import com.vmh.mvvmjetpackcompose.core.common.coroutine.AppCoroutineDispatchers
import com.vmh.mvvmjetpackcompose.core.data.repository.fcm.DefaultFcmTokenManager
import com.vmh.mvvmjetpackcompose.core.local.LocalFcmToken
import com.vmh.mvvmjetpackcompose.core.local.LocalUser
import com.vmh.mvvmjetpackcompose.core.local.datasource.AuthLocalDataSource
import com.vmh.mvvmjetpackcompose.core.local.datasource.FcmTokenLocalDataSource
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.FcmTokenRemoteDataSource
import com.vmh.mvvmjetpackcompose.core.notification.NotificationTokenProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

private const val TOKEN = "fcm-token-123"
private const val NEW_TOKEN = "fcm-token-456"

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultFcmTokenManagerTest {

  @Test
  fun `onNewToken while authenticated persists token as synced and registers it once`() = runTest {
    val fixture = fixture(isAuthenticated = true, currentToken = TOKEN)

    val result = fixture.manager.sendRegistrationToServerOnNewToken(NEW_TOKEN)

    assertNull(result.getError())
    assertEquals(listOf(NEW_TOKEN), fixture.remote.sentTokens)
    assertEquals(NEW_TOKEN, fixture.local.stored?.token)
    assertTrue(fixture.local.stored?.isSynced == true)
  }

  @Test
  fun `onNewToken while signed out persists token as unsynced and does not hit the network`() = runTest {
    val fixture = fixture(isAuthenticated = false, currentToken = TOKEN)

    val result = fixture.manager.sendRegistrationToServerOnNewToken(NEW_TOKEN)

    assertNull(result.getError())
    assertTrue(fixture.remote.sentTokens.isEmpty())
    assertEquals(NEW_TOKEN, fixture.local.stored?.token)
    assertFalse(fixture.local.stored?.isSynced == true)
  }

  @Test
  fun `onLaunch skips the network when the current token is already synced`() = runTest {
    val fixture = fixture(
      isAuthenticated = true,
      currentToken = TOKEN,
      storedToken = TOKEN,
      storedSynced = true,
    )

    val result = fixture.manager.sendRegistrationToServerOnLaunch()

    assertNull(result.getError())
    assertTrue(fixture.remote.sentTokens.isEmpty())
  }

  @Test
  fun `onLaunch registers the token when it has not been synced yet`() = runTest {
    val fixture = fixture(
      isAuthenticated = true,
      currentToken = TOKEN,
      storedToken = TOKEN,
      storedSynced = false,
    )

    val result = fixture.manager.sendRegistrationToServerOnLaunch()

    assertNull(result.getError())
    assertEquals(listOf(TOKEN), fixture.remote.sentTokens)
    assertTrue(fixture.local.stored?.isSynced == true)
  }

  @Test
  fun `onLaunch while signed out never registers`() = runTest {
    val fixture = fixture(isAuthenticated = false, currentToken = TOKEN)

    val result = fixture.manager.sendRegistrationToServerOnLaunch()

    assertNull(result.getError())
    assertTrue(fixture.remote.sentTokens.isEmpty())
  }

  @Test
  fun `onSuccessfulLogin re-registers even if the token is already synced`() = runTest {
    val fixture = fixture(
      isAuthenticated = true,
      currentToken = TOKEN,
      storedToken = TOKEN,
      storedSynced = true,
    )

    val result = fixture.manager.sendRegistrationToServerOnSuccessfulLogin()

    assertNull(result.getError())
    assertEquals(listOf(TOKEN), fixture.remote.sentTokens)
  }

  @Test
  fun `clearFcmToken wipes local state and detaches the token on the backend`() = runTest {
    val fixture = fixture(
      isAuthenticated = true,
      currentToken = TOKEN,
      storedToken = TOKEN,
      storedSynced = true,
    )

    val result = fixture.manager.clearFcmToken()

    assertNull(result.getError())
    assertEquals(listOf(""), fixture.remote.sentTokens)
    assertNull(fixture.local.stored)
  }

  @Test
  fun `a failing backend call propagates the error and leaves the token unsynced`() = runTest {
    val fixture = fixture(isAuthenticated = true, currentToken = TOKEN)
    fixture.remote.result = Err(AppError.ApiException.NetworkException(cause = null))

    val result = fixture.manager.sendRegistrationToServerOnNewToken(NEW_TOKEN)

    assertNotNull(result.getError())
    assertFalse(fixture.local.stored?.isSynced == true)
  }

  // ------------------------------------------- Fixtures -------------------------------------------

  private fun TestScope.fixture(
    isAuthenticated: Boolean,
    currentToken: String,
    storedToken: String = "",
    storedSynced: Boolean = false,
  ): Fixture {
    val dispatchers = TestAppCoroutineDispatchers(UnconfinedTestDispatcher(testScheduler))
    val tokenProvider = FakeNotificationTokenProvider(currentToken = currentToken)
    val initialLocal = storedToken
      .takeIf { it.isNotEmpty() }
      ?.let { LocalFcmToken.newBuilder().setToken(it).setIsSynced(storedSynced).build() }
    val local = FakeFcmTokenLocalDataSource(initial = initialLocal)
    val remote = FakeFcmTokenRemoteDataSource()
    val auth = FakeAuthLocalDataSource(isAuthenticated = isAuthenticated)

    return Fixture(
      manager = DefaultFcmTokenManager(
        notificationTokenProvider = tokenProvider,
        fcmTokenLocalDataSource = local,
        fcmTokenRemoteDataSource = remote,
        authLocalDataSource = auth,
        appCoroutineDispatchers = dispatchers,
      ),
      tokenProvider = tokenProvider,
      local = local,
      remote = remote,
    )
  }

  private data class Fixture(
    val manager: DefaultFcmTokenManager,
    val tokenProvider: FakeNotificationTokenProvider,
    val local: FakeFcmTokenLocalDataSource,
    val remote: FakeFcmTokenRemoteDataSource,
  )
}

private class TestAppCoroutineDispatchers(dispatcher: CoroutineDispatcher) : AppCoroutineDispatchers {
  override val main = dispatcher
  override val immediateMain = dispatcher
  override val io = dispatcher
  override val default = dispatcher
  override val unconfined = dispatcher
}

private class FakeNotificationTokenProvider(private val currentToken: String) : NotificationTokenProvider {
  override val tokenFlow: Flow<String> = emptyFlow()

  override suspend fun currentToken(): Result<String, AppError> = Ok(currentToken)

  override suspend fun deleteToken(): Result<Unit, AppError> = Ok(Unit)

  override suspend fun subscribeToTopic(topic: String): Result<Unit, AppError> = Ok(Unit)

  override suspend fun unsubscribeFromTopic(topic: String): Result<Unit, AppError> = Ok(Unit)
}

private class FakeFcmTokenLocalDataSource(initial: LocalFcmToken?) : FcmTokenLocalDataSource {
  var stored: LocalFcmToken? = initial
    private set

  override fun observeFcmToken(): Flow<Result<LocalFcmToken?, AppError.LocalStorageException>> = flowOf(Ok(stored))

  override suspend fun readFcmToken(): Result<LocalFcmToken?, AppError.LocalStorageException> = Ok(stored)

  override suspend fun update(
    transform: (LocalFcmToken?) -> LocalFcmToken?,
  ): Result<Unit, AppError.LocalStorageException> {
    stored = transform(stored)
    return Ok(Unit)
  }
}

private class FakeFcmTokenRemoteDataSource : FcmTokenRemoteDataSource {
  val sentTokens = mutableListOf<String>()
  var result: Result<Unit, AppError.ApiException> = Ok(Unit)

  override suspend fun registerFcmToken(token: String): Result<Unit, AppError.ApiException> {
    result.onSuccess { sentTokens += token }
    return result
  }
}

private class FakeAuthLocalDataSource(isAuthenticated: Boolean) : AuthLocalDataSource {
  private val localUser: LocalUser? =
    if (isAuthenticated) LocalUser.newBuilder().setId(UInt64Value.of(1L)).build() else null

  override fun observeLocalUser(): Flow<Result<LocalUser?, AppError.LocalStorageException>> = emptyFlow()

  override suspend fun readLocalUser(): Result<LocalUser?, AppError.LocalStorageException> = Ok(localUser)

  override suspend fun update(transform: (LocalUser?) -> LocalUser?): Result<Unit, AppError.LocalStorageException> =
    Ok(Unit)
}
