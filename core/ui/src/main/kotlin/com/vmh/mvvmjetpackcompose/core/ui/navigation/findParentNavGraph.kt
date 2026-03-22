package com.vmh.mvvmjetpackcompose.core.ui.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph
import kotlin.sequences.drop
import kotlin.sequences.filterIsInstance
import kotlin.sequences.firstOrNull

/**
 * Finds the parent [NavGraph] of the given [NavDestination].
 */
fun NavDestination.findParentNavGraph(): NavGraph? = hierarchy
  .drop(1) // skips the destination itself to get the containing NavGraph
  .filterIsInstance<NavGraph>()
  .firstOrNull()
