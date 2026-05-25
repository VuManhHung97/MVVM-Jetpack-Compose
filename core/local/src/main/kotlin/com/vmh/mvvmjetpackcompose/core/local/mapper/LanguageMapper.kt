package com.vmh.mvvmjetpackcompose.core.local.mapper

import com.vmh.mvvmjetpackcompose.core.local.LocalLocale
import java.util.Locale

fun LocalLocale.toLocale(): Locale? = languageTag
  .takeIf { it.isNotBlank() }
  ?.let { Locale.forLanguageTag(it) }

fun Locale.toLocalLocale(): LocalLocale = LocalLocale.newBuilder()
  .setLanguageTag(toLanguageTag())
  .build()
