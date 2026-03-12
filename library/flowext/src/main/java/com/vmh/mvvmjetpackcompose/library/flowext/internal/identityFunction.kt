package com.vmh.mvvmjetpackcompose.library.flowext.internal

import kotlin.jvm.JvmField

@Suppress("unused")
@JvmField
internal val identityFunction: (Any?) -> Any? = { it }

@JvmField
internal val identitySuspendFunction: suspend (Any?) -> Any? = { it }
