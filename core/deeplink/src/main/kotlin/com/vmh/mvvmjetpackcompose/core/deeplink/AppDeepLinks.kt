package com.vmh.mvvmjetpackcompose.core.deeplink

import android.content.Context
import android.content.Intent
import com.airbnb.deeplinkdispatch.DeepLink

/**
 * Central declaration of every URI template the app supports.
 *
 * [DefaultDeepLinkResolver] only uses DeepLinkDispatch's matching (`findEntry`), so the annotated
 * functions below are **never invoked** — they exist solely to register their URI templates. That is
 * why each body is a stub.
 *
 * To add a new deeplink: add a `const val` template (composed from [DeepLinkScheme.PREFIX]) plus a
 * stub `@DeepLink` method, then map that template to a [DeepLinkDestination] in [DefaultDeepLinkResolver].
 */
internal object AppDeepLinks {
  const val SEARCH_PATH = "search"

  const val SEARCH_TEMPLATE = "${DeepLinkScheme.PREFIX}/$SEARCH_PATH?keyword={keyword}"

  // `context` is required by DeepLinkDispatch's method-deeplink signature even though the stub ignores it.
  @Suppress("UnusedParameter")
  @DeepLink(SEARCH_TEMPLATE)
  @JvmStatic
  fun search(context: Context): Intent = Intent()
}
