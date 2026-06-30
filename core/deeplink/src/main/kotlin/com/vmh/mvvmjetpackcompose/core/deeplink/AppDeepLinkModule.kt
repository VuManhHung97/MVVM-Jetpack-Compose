package com.vmh.mvvmjetpackcompose.core.deeplink

import com.airbnb.deeplinkdispatch.DeepLinkModule

/**
 * Marks the module so DeepLinkDispatch generates the `AppDeepLinkModuleRegistry` class at
 * compile-time (containing every URI template declared in [AppDeepLinks]). That registry is used by
 * [DefaultDeepLinkResolver] to match URIs.
 */
@DeepLinkModule
class AppDeepLinkModule
