package com.vmh.mvvmjetpackcompose.core.deeplink

/**
 * Result of parsing a deeplink into a plain "destination" — independent of NavController/route/Compose.
 *
 * The presentation layer (app) is responsible for mapping each destination to a concrete navigation
 * action, keeping this module decoupled from navigation details.
 */
sealed interface DeepLinkDestination {
  data class Search(val keyword: String?) : DeepLinkDestination
}
