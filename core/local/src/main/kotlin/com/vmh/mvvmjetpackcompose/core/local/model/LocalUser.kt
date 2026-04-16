package com.vmh.mvvmjetpackcompose.core.local.model

import com.vmh.mvvmjetpackcompose.core.local.LocalUser

internal fun LocalUser.isNotDefault() = hasId()
