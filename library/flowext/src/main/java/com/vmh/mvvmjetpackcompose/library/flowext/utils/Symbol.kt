package com.vmh.mvvmjetpackcompose.library.flowext.utils

import com.vmh.mvvmjetpackcompose.library.flowext.DelicateFlowExtApi
import kotlin.jvm.JvmField

/**
 * A symbol class that is used to define unique constants that are self-explanatory in debugger.
 */
@DelicateFlowExtApi
public class Symbol(@JvmField public val symbol: String) {
  override fun toString(): String = "<$symbol>"

  @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
  public inline fun <T> unbox(value: Any?): T = if (value === this) null as T else value as T
}
