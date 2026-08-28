package com.vmh.mvvmjetpackcompose.feature.language.presentation.language.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.vmh.mvvmjetpackcompose.core.navigation.Navigator
import com.vmh.mvvmjetpackcompose.feature.language.ui.language.LanguageRoute

fun EntryProviderScope<NavKey>.languageEntry(navigator: Navigator) {
  entry<LanguageNavKey> {
    LanguageRoute(onNavigateBack = navigator::goBack)
  }
}
