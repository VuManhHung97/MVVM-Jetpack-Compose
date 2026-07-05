package com.vmh.mvvmjetpackcompose.feature.account.ui.component

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.vmh.mvvmjetpackcompose.core.model.gameaccount.AccountStatus
import com.vmh.mvvmjetpackcompose.core.resource.R
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors

@Immutable
data class AccountStatusMeta(
  @param:StringRes val labelResId: Int,
  val background: Color,
  val contentColor: Color,
  @param:StringRes val lockLabelResId: Int,
  val lockColor: Color,
)

internal fun statusMetaOf(status: AccountStatus): AccountStatusMeta = when (status) {
  AccountStatus.Active -> AccountStatusMeta(
    labelResId = R.string.account_status_active,
    background = MVVMJetPackComposeColors.StatusActiveBg,
    contentColor = MVVMJetPackComposeColors.Green,
    lockLabelResId = R.string.account_action_lock,
    lockColor = MVVMJetPackComposeColors.Red,
  )
  AccountStatus.Locked -> AccountStatusMeta(
    labelResId = R.string.account_status_locked,
    background = MVVMJetPackComposeColors.StatusLockedBg,
    contentColor = MVVMJetPackComposeColors.Red,
    lockLabelResId = R.string.account_action_unlock,
    lockColor = MVVMJetPackComposeColors.Green,
  )
}
