package com.vmh.mvvmjetpackcompose

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.vmh.mvvmjetpackcompose.core.analytics.AnalyticsTracker
import com.vmh.mvvmjetpackcompose.core.deeplink.DeepLinkResolver
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.analytics.LocalAnalyticsTracker
import com.vmh.mvvmjetpackcompose.core.ui.common.CustomSnackbarHost
import com.vmh.mvvmjetpackcompose.core.ui.common.LocalSnackbarManager
import com.vmh.mvvmjetpackcompose.core.ui.common.SnackbarManager
import com.vmh.mvvmjetpackcompose.core.ui.common.rememberSnackbarManager
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme
import com.vmh.mvvmjetpackcompose.core.ui.util.openAppInPlayStore
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.authentication.navigation.AuthenticationRoutePattern
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.authentication.navigation.authenticationScreen
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.authentication.navigation.navigateToAuthenticationScreen
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signIn.navigation.navigateToSignInScreen
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signIn.navigation.signInScreen
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signup.navigation.navigateToSignUpScreen
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signup.navigation.signUpScreen
import com.vmh.mvvmjetpackcompose.feature.language.presentation.language.navigation.languageScreen
import com.vmh.mvvmjetpackcompose.feature.language.presentation.language.navigation.navigateToLanguageScreen
import com.vmh.mvvmjetpackcompose.feature.main.ui.MainNavigationBar
import com.vmh.mvvmjetpackcompose.feature.main.ui.MainState
import com.vmh.mvvmjetpackcompose.feature.main.ui.navigation.MainGraphRoutePattern
import com.vmh.mvvmjetpackcompose.feature.main.ui.navigation.MainTopScreenTopLevelDestination
import com.vmh.mvvmjetpackcompose.feature.main.ui.navigation.mainGraph
import com.vmh.mvvmjetpackcompose.feature.main.ui.navigation.navigateToMainGraph
import com.vmh.mvvmjetpackcompose.feature.main.ui.rememberMainState
import com.vmh.mvvmjetpackcompose.feature.qrCodeReader.presentation.navigation.QRCodeReaderRoutePattern
import com.vmh.mvvmjetpackcompose.feature.qrCodeReader.presentation.navigation.qrCodeReaderScreen
import com.vmh.mvvmjetpackcompose.feature.search.ui.navigation.navigateToSearchScreen
import com.vmh.mvvmjetpackcompose.feature.search.ui.navigation.searchScreen
import com.vmh.mvvmjetpackcompose.feature.webview.ui.navigation.WebViewArgs
import com.vmh.mvvmjetpackcompose.feature.webview.ui.navigation.navigateToWebViewScreen
import com.vmh.mvvmjetpackcompose.feature.webview.ui.navigation.webViewScreen
import com.vmh.mvvmjetpackcompose.lifecycle.collectInLaunchedEffectWithLifecycle
import com.vmh.mvvmjetpackcompose.locale.LocaleController
import com.vmh.mvvmjetpackcompose.notification.NotificationPermissionEffect
import com.vmh.mvvmjetpackcompose.ui.widget.common.DialogCommon
import com.vmh.mvvmjetpackcompose.ui.widget.common.UnauthorizedErrorDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.first

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  private val viewModel: MainViewModel by viewModels()

  @Inject
  internal lateinit var navTypeContainer: NavTypeContainer

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

            val startDestination = when (startDestinationState) {
              StartDestinationState.AuthenticationScreen ->
                AuthenticationRoutePattern

              StartDestinationState.MainScreen ->
                MainGraphRoutePattern

              StartDestinationState.Loading ->
                return@Surface
            }

            MVVMJetpackComposeApp(
              startDestination = QRCodeReaderRoutePattern,
              navTypeContainer = navTypeContainer,
            )
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
  startDestination: String,
  navTypeContainer: NavTypeContainer,
  modifier: Modifier = Modifier,
  viewModel: MainViewModel = hiltViewModel(),
  navController: NavHostController = rememberNavController(),
  mainState: MainState = rememberMainState(navController = navController),
  localSnackbarManager: SnackbarManager = LocalSnackbarManager.current,
) {
  val mainTopScreenTopLevelDestinations = MainTopScreenTopLevelDestination.entries.toPersistentList()
  val context = LocalContext.current
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
        navController.navigateToAuthenticationScreen(
          navOptions = navOptions {
            popUpTo(navController.graph.id) { inclusive = true }

            launchSingleTop = true
          },
        )
      }

      is MainSingleEvent.NavigateToSearch -> navController.navigateToSearchScreen()
    }
  }

  Scaffold(
    modifier = modifier,
    contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(WindowInsets.statusBars),
    bottomBar = {
      val isMainNavigationBarVisible = mainState.currentDestination
        ?.hierarchy
        ?.any { navDestination ->
          mainTopScreenTopLevelDestinations.any { destination ->
            navDestination.route == destination.graphRoutePattern
          }
        }
        ?: false

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
          onNavigateToDestination = {
            mainState.navigateToTopLevelDestination(
              targetTopLevelDestination = it,
            )
          },
          currentDestination = mainState.currentDestination,
        )
      }
    },
    snackbarHost = { CustomSnackbarHost(snackbarState = localSnackbarManager.snackbarHostState) },
  ) { innerPadding ->
    NavHost(
      modifier = Modifier
        .padding(innerPadding)
        .consumeWindowInsets(innerPadding)
        .fillMaxSize(),
      navController = navController,
      startDestination = startDestination,
    ) {
      mainGraph(
        onNavigateToSearchScreen = navController::navigateToSearchScreen,
        onNavigateToAuthenticationScreen = {
          navController.navigateToAuthenticationScreen(
            navOptions = navOptions {
              popUpTo(id = navController.graph.id) { inclusive = true }

              launchSingleTop = true
            },
          )
        },
        onNavigateToWebViewScreen = { webViewDestination ->
          navController.navigateToWebViewScreen(
            navType = navTypeContainer.webViewArgsNavType,
            args = WebViewArgs(
              path = webViewDestination.path,
              title = context.getString(webViewDestination.titleResId),
            ),
          )
        },
        onNavigateToLanguageScreen = navController::navigateToLanguageScreen,
      )

      searchScreen(
        onNavigateBack = navController::popBackStack,
      )

      authenticationScreen(
        onNavigateToSignInScreen = navController::navigateToSignInScreen,
        onNavigateToHomeScreen = {
          navController.navigateToMainGraph(
            navOptions {
              popUpTo(navController.graph.id) { inclusive = true }

              launchSingleTop = true
            },
          )
        },
      )

      signInScreen(
        onNavigateBack = navController::popBackStack,
        onNavigateToSignUpScreen = navController::navigateToSignUpScreen,
        navigateToAuthenticationScreen = {
          navController.navigateToAuthenticationScreen(
            navOptions = navOptions {
              popUpTo(navController.graph.id) { inclusive = true }

              launchSingleTop = true
            },
          )
        },
      )

      signUpScreen(
        onNavigateBack = navController::popBackStack,
        navigateToAuthenticationScreen = {
          navController.navigateToAuthenticationScreen(
            navOptions = navOptions {
              popUpTo(id = navController.graph.id) { inclusive = true }

              launchSingleTop = true
            },
          )
        },
      )

      webViewScreen(
        navType = navTypeContainer.webViewArgsNavType,
        onNavigateBack = navController::popBackStack,
      )

      languageScreen(
        onNavigateBack = navController::popBackStack,
      )

      qrCodeReaderScreen(
        onNavigateBack = navController::popBackStack,
      )
    }

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
