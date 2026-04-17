package com.vmh.mvvmjetpackcompose

import android.annotation.SuppressLint
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.authentication.navigation.AuthenticationRoutePattern
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.authentication.navigation.authenticationScreen
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.authentication.navigation.navigateToAuthenticationScreen
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signIn.navigation.navigateToSignInScreen
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signIn.navigation.signInScreen
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signup.navigation.navigateToSignUpScreen
import com.vmh.mvvmjetpackcompose.feature.authentication.presentation.signup.navigation.signUpScreen
import com.vmh.mvvmjetpackcompose.feature.main.ui.MainNavigationBar
import com.vmh.mvvmjetpackcompose.feature.main.ui.MainState
import com.vmh.mvvmjetpackcompose.feature.main.ui.navigation.MainGraphRoutePattern
import com.vmh.mvvmjetpackcompose.feature.main.ui.navigation.MainTopScreenTopLevelDestination
import com.vmh.mvvmjetpackcompose.feature.main.ui.navigation.mainGraph
import com.vmh.mvvmjetpackcompose.feature.main.ui.navigation.navigateToMainGraph
import com.vmh.mvvmjetpackcompose.feature.main.ui.rememberMainState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.first

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    // Call installSplashScreen in the starting activity before calling super.onCreate().
    val splashScreen = installSplashScreen()

    // Manually enable edge-to-edge by calling enableEdgeToEdge in onCreate of your Activity.
    // It should be called before setContentView.
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)

    splashScreen.setKeepOnScreenCondition { viewModel.startDestinationStateFlow.value is StartDestinationState.Loading }

    setContent {
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

          MVVMJetpackComposeApp(startDestination = startDestination)
        }
      }
    }
  }
}

private const val MainNavigationBarAnimationDurationMillis = 300

@Composable
private fun MVVMJetpackComposeApp(
  startDestination: String,
  modifier: Modifier = Modifier,
  navController: NavHostController = rememberNavController(),
  mainState: MainState = rememberMainState(navController = navController),
) {
  val mainTopScreenTopLevelDestinations = MainTopScreenTopLevelDestination.entries.toPersistentList()

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
        onNavigateToSearchScreen = {
          // TODO: handle later
        },
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
    }
  }
}
