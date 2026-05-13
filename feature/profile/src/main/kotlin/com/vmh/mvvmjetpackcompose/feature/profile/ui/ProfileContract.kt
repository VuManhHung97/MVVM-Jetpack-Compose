package com.vmh.mvvmjetpackcompose.feature.profile.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vmh.mvvmjetpackcompose.core.model.error.AppError
import com.vmh.mvvmjetpackcompose.core.resource.R
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class ProfileUiState(val profilesContent: ProfilesContentUiState) {
  @Immutable
  sealed interface ProfilesContentUiState {

    @Immutable
    data object Loading : ProfilesContentUiState

    @Immutable
    data class Content(val items: PersistentList<ProfileUiItem>) : ProfilesContentUiState

    @Immutable
    data class Error(val error: AppError) : ProfilesContentUiState
  }

  @Immutable
  sealed interface ProfileUiItem {
    @Immutable
    sealed interface Profile : ProfileUiItem {
      val url: String?
      val name: String
      val email: String

      @Immutable
      data object Empty : Profile {
        override val url get() = ""
        override val name get() = ""
        override val email get() = ""
      }

      @Immutable
      data class Info(override val url: String?, override val name: String, override val email: String) : Profile
    }

    @Immutable
    sealed interface HeaderTitle : ProfileUiItem {
      @get:StringRes
      val textResId: Int

      @Immutable
      data object Preferences : HeaderTitle {
        override val textResId: Int get() = R.string.profile_preferences
      }

      @Immutable
      data object Support : HeaderTitle {
        override val textResId: Int get() = R.string.profile_support
      }

      @Immutable
      data object Other : HeaderTitle {
        override val textResId: Int get() = R.string.profile_other
      }
    }

    @Immutable
    sealed interface Option : ProfileUiItem {
      @get:StringRes
      val textResId: Int

      @get:DrawableRes
      val iconResId: Int?

      @Immutable
      data object Language : Option {
        override val textResId: Int get() = R.string.profile_language
        override val iconResId: Int get() = R.drawable.ic_setting_language
      }

      @Immutable
      data object FAQ : Option {
        override val textResId: Int get() = R.string.profile_faq
        override val iconResId: Int get() = R.drawable.ic_setting_faq
      }

      @Immutable
      data object ContactUs : Option {
        override val textResId: Int get() = R.string.profile_contact_us
        override val iconResId: Int get() = R.drawable.ic_setting_contact
      }

      @Immutable
      data object TermsOfUse : Option {
        override val textResId: Int get() = R.string.profile_terms_of_use
        override val iconResId: Int? get() = null
      }

      @Immutable
      data object PrivacyPolicy : Option {
        override val textResId: Int get() = R.string.profile_privacy_policy
        override val iconResId: Int? get() = null
      }
    }

    @Immutable
    data object Divider : ProfileUiItem {
      val thickness: Dp get() = 8.dp
    }

    @Immutable
    data object SignOut : ProfileUiItem

    companion object {
      val InitialItems
        get() = persistentListOf(
          Profile.Empty,
          Divider,
          HeaderTitle.Preferences,
          Option.Language,
          Divider,
          HeaderTitle.Support,
          Option.FAQ,
          Option.ContactUs,
          Divider,
          HeaderTitle.Other,
          Option.TermsOfUse,
          Option.PrivacyPolicy,
        )
    }
  }
}

sealed interface ProfileSingleEvent {
  data object LogoutSuccess : ProfileSingleEvent

  @JvmInline
  value class LogoutFailure(val error: AppError) : ProfileSingleEvent
}
