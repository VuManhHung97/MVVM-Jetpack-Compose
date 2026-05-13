package com.vmh.mvvmjetpackcompose.ui.widget.navigation

import android.os.Parcelable
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.core.os.BundleCompat
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import java.util.Locale
import kotlin.jvm.java
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.text.lowercase
import kotlin.text.replace
import kotlin.text.toRegex

/**
 * NavRouteDefinition is a generic class for defining navigation routes
 * with typed arguments in Jetpack Compose Navigation.
 *
 * @param Args The Parcelable type representing the arguments for the route.
 * @property baseRoute The base route string used for navigation.
 * @property argsType The KClass of the argument type.
 * @param argsKey Optional key for the arguments, defaults to the snake_case version of the simple name of [argsType].
 *
 * @see androidx.navigation.NavBackStackEntry
 * @see androidx.lifecycle.SavedStateHandle
 */
class NavRouteDefinition<Args : Parcelable>(
  private val baseRoute: String,
  private val argsType: KClass<Args>,
  argsKey: String? = null,
) {
  /**
   * The key used to store and retrieve arguments in navigation.
   */
  private val actualArgsKey = argsKey
    ?: checkNotNull(argsType.simpleName) { "Cannot get simpleName from $argsType" }
      .camelToSnakeCase()

  /**
   * The route pattern for navigation, including the argument key.
   */
  @DelicateNavRouteDefinitionApi
  val routePattern = "$baseRoute/{$actualArgsKey}"

  /**
   * Creates a route string with encoded arguments for navigation.
   *
   * @param args The arguments to encode.
   * @param argsNavType The navigation type used for encoding.
   * @return The route string with encoded arguments.
   */
  fun createRoute(args: Args, argsNavType: MoshiNavType<Args>): String {
    val encoded = argsNavType.encodeToString(args)
    return "$baseRoute/$encoded"
  }

  /**
   * Retrieves and casts arguments from a SavedStateHandle.
   *
   * @param savedStateHandle The SavedStateHandle containing arguments.
   * @return The typed arguments.
   */
  fun requireArgs(savedStateHandle: SavedStateHandle): Args = argsType.cast(
    checkNotNull(savedStateHandle.get<Args>(actualArgsKey)) {
      "$actualArgsKey with type $argsType not found in SavedStateHandle $savedStateHandle"
    },
  )

  /**
   * Retrieves and casts arguments from a NavBackStackEntry.
   *
   * @param entry The NavBackStackEntry containing arguments.
   * @return The typed arguments.
   */
  fun requireArgs(entry: NavBackStackEntry): Args = checkNotNull(
    BundleCompat.getParcelable(
      checkNotNull(entry.arguments) { "Arguments for NavBackStackEntry $entry are null" },
      actualArgsKey,
      argsType.java,
    ),
  ) { "$actualArgsKey with type $argsType not found in arguments of NavBackStackEntry $entry" }

  /**
   * Adds a composable destination to the NavGraph with the defined route and arguments.
   *
   * @param argsNavType The navigation type for the arguments.
   * @param content The composable content for the destination.
   */
  @OptIn(DelicateNavRouteDefinitionApi::class)
  fun NavGraphBuilder.composable(
    argsNavType: MoshiNavType<Args>,
    content: @Composable AnimatedContentScope.(navBackStackEntry: NavBackStackEntry) -> Unit,
  ) {
    composable(
      route = routePattern,
      arguments = listOf(
        navArgument(name = actualArgsKey) { type = argsNavType },
      ),
      content = content,
    )
  }
}

private val ConsecutiveUppercaseRegex by lazy { """([A-Z]+)([A-Z][a-z])""".toRegex() }
private val CamelCaseRegex by lazy { """([a-z\d])([A-Z])""".toRegex() }

private fun String.camelToSnakeCase(): String = this
  // Split consecutive uppercase words: APIKey -> API_Key
  .replace(ConsecutiveUppercaseRegex, "$1_$2")
  // Split regular camel case: myFieldName -> my_Field_Name
  .replace(CamelCaseRegex, "$1_$2")
  .lowercase(Locale.US)

/**
 * Marks a [NavRouteDefinition] API as **delicate**.
 *
 * By convention, [NavRouteDefinition]s are intended for internal use within their module and
 * should not be accessed from other modules. However, in special cases where exposure is necessary,
 * this annotation highlights that the API is delicate and should be used with caution.
 *
 * Carefully review the documentation and understand all implications before using any API marked with this annotation.
 */
@MustBeDocumented
@Retention(value = AnnotationRetention.BINARY)
@RequiresOptIn(
  level = RequiresOptIn.Level.WARNING,
  message = "This is a delicate API and its use requires care." +
    " Make sure you fully read and understand documentation of the declaration that is marked as a delicate API.",
)
annotation class DelicateNavRouteDefinitionApi
