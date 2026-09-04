package com.vmh.mvvmjetpackcompose.core.local.model

import com.vmh.mvvmjetpackcompose.core.local.LocalFcmToken

internal fun LocalFcmToken.isNotDefault() = token.isNotEmpty()
