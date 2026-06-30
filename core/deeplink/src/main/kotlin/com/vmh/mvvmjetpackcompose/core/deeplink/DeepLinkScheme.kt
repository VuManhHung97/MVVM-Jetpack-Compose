  package com.vmh.mvvmjetpackcompose.core.deeplink

/**
 * Single source of truth for the app's deeplink scheme/host.
 *
 * Every URI template is built from [PREFIX] to avoid scattered hard-coding. This is a mock scheme
 * for demo purposes: `dld://androidbase/...`.
 */
object DeepLinkScheme {
  const val SCHEME = "dld"
  const val HOST = "androidbase"

  const val PREFIX = "$SCHEME://$HOST"
}
