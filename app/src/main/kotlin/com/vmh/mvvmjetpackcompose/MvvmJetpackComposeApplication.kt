package com.vmh.mvvmjetpackcompose

import android.app.Application
import com.vmh.mvvmjetpackcompose.core.notification.NotificationInitializer
import com.vmh.mvvmjetpackcompose.locale.LocaleInitializer
import com.vmh.mvvmjetpackcompose.notification.FcmTokenCoordinator
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import timber.log.Timber

@HiltAndroidApp
class MvvmJetpackComposeApplication : Application() {
  @Inject
  internal lateinit var localeInitializer: LocaleInitializer

  @Inject
  internal lateinit var notificationInitializer: NotificationInitializer

  @Inject
  internal lateinit var fcmTokenCoordinator: FcmTokenCoordinator

  override fun onCreate() {
    super.onCreate()
    setupTimber()
    localeInitializer.initialize()
    notificationInitializer.initialize()
    fcmTokenCoordinator.start()
  }

  private fun setupTimber() {
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }

    Timber.d("Initialized Timber")
  }
}
