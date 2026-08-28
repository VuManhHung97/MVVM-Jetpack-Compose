package com.vmh.mvvmjetpackcompose.feature.language.presentation.language.navigation

import androidx.navigation3.runtime.NavKey
import com.vmh.mvvmjetpackcompose.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object LanguageNavKey : NavKey

fun Navigator.navigateToLanguage() = navigate(LanguageNavKey)
