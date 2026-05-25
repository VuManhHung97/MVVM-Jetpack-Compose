package com.vmh.mvvmjetpackcompose

import android.app.Application
import com.vmh.mvvmjetpackcompose.locale.LocaleInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import timber.log.Timber

@HiltAndroidApp
class MvvmJetpackComposeApplication : Application() {
  @Inject
  internal lateinit var localeInitializer: LocaleInitializer

  override fun onCreate() {
    super.onCreate()
    setupTimber()
    localeInitializer.initialize()
  }

  private fun setupTimber() {
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }

    Timber.d("Initialized Timber")
  }
}
