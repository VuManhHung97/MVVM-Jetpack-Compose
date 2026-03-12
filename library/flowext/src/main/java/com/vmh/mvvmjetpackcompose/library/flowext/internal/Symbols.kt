package com.vmh.mvvmjetpackcompose.library.flowext.internal

import kotlin.jvm.JvmField
import com.vmh.mvvmjetpackcompose.library.flowext.utils.Symbol

/**
 * Symbol used to indicate that the flow is complete.
 * It should never leak to the outside world.
 */
@JvmField
internal val DONE_VALUE = Symbol("DONE_VALUE")

/**
 * This is a work-around for having nested nulls in generic code.
 * This allows for writing faster generic code instead of using `Option`.
 * This is only used as an optimisation technique in low-level code.
 *
 * This is internal and should not be used outside of the library.
 */
@JvmField
internal val INTERNAL_NULL_VALUE = Symbol("INTERNAL_NULL_VALUE")
