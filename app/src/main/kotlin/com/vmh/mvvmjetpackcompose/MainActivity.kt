package com.vmh.mvvmjetpackcompose

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.vmh.mvvmjetpackcompose.core.analytics.AnalyticsTracker
import com.vmh.mvvmjetpackcompose.core.deeplink.DeepLinkResolver
import com.vmh.mvvmjetpackcompose.core.navigation.Navigator
import com.vmh.mvvmjetpackcompose.core.navigation.rememberAppNavigationState
import com.vmh.mvvmjetpackcompose.core.navigation.toEntries
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.analytics.LocalAnalyticsTracker
import com.vmh.mvvmjetpackcompose.core.ui.common.CustomSnackbarHost
import com.vmh.mvvmjetpackcompose.core.ui.common.LocalSnackbarManager
import com.vmh.mvvmjetpackcompose.core.ui.common.SnackbarManager
import com.vmh.mvvmjetpackcompose.core.ui.common.rememberSnackbarManager
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme
import com.vmh.mvvmjetpackcompose.core.ui.util.openAppInPlayStore
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.authentication.navigation.AuthenticationNavKey
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.authentication.navigation.authenticationEntry
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signIn.navigation.signInEntry
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signup.navigation.signUpEntry
import com.vmh.mvvmjetpackcompose.feature.home.ui.navigation.HomeNavKey
import com.vmh.mvvmjetpackcompose.feature.home.ui.navigation.homeEntry
import com.vmh.mvvmjetpackcompose.feature.language.presentation.language.navigation.languageEntry
import com.vmh.mvvmjetpackcompose.feature.main.ui.navigation.MainNavKey
import com.vmh.mvvmjetpackcompose.feature.profile.ui.navigation.ProfileNavKey
import com.vmh.mvvmjetpackcompose.feature.profile.ui.navigation.profileEntry
import com.vmh.mvvmjetpackcompose.feature.search.ui.navigation.navigateToSearch
import com.vmh.mvvmjetpackcompose.feature.search.ui.navigation.searchEntry
import com.vmh.mvvmjetpackcompose.feature.webview.ui.navigation.webViewEntry
import com.vmh.mvvmjetpackcompose.lifecycle.collectInLaunchedEffectWithLifecycle
import com.vmh.mvvmjetpackcompose.locale.LocaleController
import com.vmh.mvvmjetpackcompose.navigation.MainNavigationBar
import com.vmh.mvvmjetpackcompose.navigation.MainTopScreenTopLevelDestination
import com.vmh.mvvmjetpackcompose.notification.NotificationPermissionEffect
import com.vmh.mvvmjetpackcompose.ui.widget.common.DialogCommon
import com.vmh.mvvmjetpackcompose.ui.widget.common.UnauthorizedErrorDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.first

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  private val viewModel: MainViewModel by viewModels()

  @Inject
  internal lateinit var deepLinkResolver: DeepLinkResolver

  @Inject
  internal lateinit var analyticsTracker: AnalyticsTracker

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    handleIntent(intent)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    // Call installSplashScreen in the starting activity before calling super.onCreate().
    val splashScreen = installSplashScreen()

    // Manually enable edge-to-edge by calling enableEdgeToEdge in onCreate of your Activity.
    // It should be called before setContentView.
    enableEdgeToEdge()

    val localeController = LocaleController.fromApplication(application)
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
      localeController.initBelow33()
      super.onCreate(savedInstanceState)
      localeController.observeCurrentLocaleBelow33(this)
    } else {
      super.onCreate(savedInstanceState)
      localeController.initSince33(this)
    }

    splashScreen.setKeepOnScreenCondition { viewModel.startDestinationStateFlow.value is StartDestinationState.Loading }

    setContent {
      val snackbarManager = rememberSnackbarManager()

      CompositionLocalProvider(
        LocalSnackbarManager provides snackbarManager,
        LocalAnalyticsTracker provides analyticsTracker,
      ) {
        MVVMJetpackComposeTheme {
          Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface,
          ) {
            @SuppressLint("StateFlowValueCalledInComposition")
            val startDestinationState by produceState(initialValue = viewModel.startDestinationStateFlow.value) {
              value = viewModel.startDestinationStateFlow.first { it !is StartDestinationState.Loading }
            }

            val rootStartKey = when (startDestinationState) {
              StartDestinationState.AuthenticationScreen -> AuthenticationNavKey
              StartDestinationState.MainScreen -> MainNavKey
              StartDestinationState.Loading -> return@Surface
            }

            MVVMJetpackComposeApp(rootStartKey = rootStartKey)
          }
        }
      }
    }

    if (savedInstanceState == null) {
      handleIntent(intent)
    }
  }

  private fun handleIntent(intent: Intent?) {
    intent ?: return
    deepLinkResolver.resolve(intent)
      ?.let(viewModel::handleDeepLinkDestination)
  }
}

private const val MainNavigationBarAnimationDurationMillis = 300

@Suppress("LongMethod")
@Composable
private fun MVVMJetpackComposeApp(
  rootStartKey: NavKey,
  modifier: Modifier = Modifier,
  viewModel: MainViewModel = hiltViewModel(),
  localSnackbarManager: SnackbarManager = LocalSnackbarManager.current,
) {
  val mainTopScreenTopLevelDestinations = MainTopScreenTopLevelDestination.entries.toPersistentList()
  val context = LocalContext.current
  val activity = LocalActivity.current

  val navigationState = rememberAppNavigationState(
    appRootKey = MainNavKey,
    rootStartKey = rootStartKey,
    tabStartRoute = HomeNavKey,
    topLevelRoutes = persistentSetOf(HomeNavKey, ProfileNavKey),
  )

  val navigator = remember(navigationState) { Navigator(navigationState) }

  var isUnauthorizedErrorDialogVisible by rememberSaveable { mutableStateOf(false) }
  var isForceUpdateDialogVisible by rememberSaveable { mutableStateOf(false) }

  NotificationPermissionEffect()

  viewModel.unauthorizedErrorEventFlow.collectInLaunchedEffectWithLifecycle {
    isUnauthorizedErrorDialogVisible = true
  }

  viewModel.forceUpdateErrorEventFlow.collectInLaunchedEffectWithLifecycle {
    isForceUpdateDialogVisible = true
  }

  viewModel.eventFlow.collectInLaunchedEffectWithLifecycle { event ->
    when (event) {
      MainSingleEvent.NavigateToAuthentication -> {
        isUnauthorizedErrorDialogVisible = false
        navigator.resetRootTo(AuthenticationNavKey)
      }

      is MainSingleEvent.NavigateToSearch -> navigator.navigateToSearch()
    }
  }

  Scaffold(
    modifier = modifier,
    contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(WindowInsets.statusBars),
    bottomBar = {
      val currentTopLevelKey = navigationState.tabs.topLevelRoute
      val currentKey = if (navigationState.isInAppArea) {
        navigationState.tabs.currentStack.last()
      } else {
        null
      }
      val isMainNavigationBarVisible =
        mainTopScreenTopLevelDestinations.any { it.navKey == currentKey }

      AnimatedVisibility(
        visible = isMainNavigationBarVisible,
        enter = slideInVertically(
          initialOffsetY = { it },
          animationSpec = tween(durationMillis = MainNavigationBarAnimationDurationMillis),
        ),
        exit = slideOutVertically(
          targetOffsetY = { it },
          animationSpec = tween(durationMillis = MainNavigationBarAnimationDurationMillis),
        ),
      ) {
        MainNavigationBar(
          destinations = mainTopScreenTopLevelDestinations,
          currentTopLevelKey = currentTopLevelKey,
          onDestinationSelect = { destination -> navigator.navigate(destination.navKey) },
          onDestinationReselect = { navigator.clearCurrentStack() },
        )
      }
    },
    snackbarHost = { CustomSnackbarHost(snackbarState = localSnackbarManager.snackbarHostState) },
  ) { innerPadding ->

    val entryProvider = remember(navigator) {
      entryProvider {
        authenticationEntry(navigator)
        signInEntry(navigator)
        signUpEntry(navigator)
        homeEntry(navigator)
        profileEntry(navigator)
        searchEntry(navigator)
        languageEntry(navigator)
        webViewEntry(navigator)
      }
    }

    NavDisplay(
      modifier = Modifier
        .padding(innerPadding)
        .consumeWindowInsets(innerPadding)
        .fillMaxSize(),
      entries = navigationState.toEntries(entryProvider),
      onBack = { if (!navigator.goBack()) activity?.finish() },
    )

    if (isUnauthorizedErrorDialogVisible) {
      UnauthorizedErrorDialog(
        onDismiss = { isUnauthorizedErrorDialogVisible = false },
        onConfirm = viewModel::logout,
      )
    }

    if (isForceUpdateDialogVisible) {
      DialogCommon(
        title = stringResource(CoreResourceR.string.app_error_force_update_title),
        content = stringResource(CoreResourceR.string.app_error_force_update_message),
        confirm = stringResource(CoreResourceR.string.app_error_force_update_positive_button),
        iconIdRes = CoreResourceR.drawable.ic_warning_filled,
        iconTint = MVVMJetPackComposeColors.yellow40,
        dismissOnBackPress = false,
        dismissOnClickOutside = false,
        onDismiss = {},
        onClick = context::openAppInPlayStore,
      )
    }
  }
}
