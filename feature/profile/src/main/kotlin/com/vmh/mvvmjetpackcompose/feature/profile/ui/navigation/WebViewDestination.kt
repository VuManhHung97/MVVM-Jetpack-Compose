package com.vmh.mvvmjetpackcompose.feature.profile.ui.navigation

import androidx.annotation.StringRes
import com.vmh.mvvmjetpackcompose.core.resource.R

enum class WebViewDestination(val path: String, @StringRes val titleResId: Int) {
  FAQ(path = "faq", titleResId = R.string.web_view_faq_title),
  PrivacyPolicy(path = "privacy-policy", titleResId = R.string.web_view_privacy_policy_title),
  TermsOfUse(path = "terms-of-use", titleResId = R.string.web_view_term_of_use_title),
}
