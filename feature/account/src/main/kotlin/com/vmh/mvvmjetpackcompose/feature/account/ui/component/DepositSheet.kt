// Design-handoff opacity/dimension values are encoded inline as one-off literals.
@file:Suppress("MagicNumber")

package com.vmh.mvvmjetpackcompose.feature.account.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vmh.mvvmjetpackcompose.core.resource.R
import com.vmh.mvvmjetpackcompose.core.ui.theme.GameAdminSerif
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.feature.account.presentation.account.DepositUiState
import com.vmh.mvvmjetpackcompose.ui.widget.common.AppModalBottomSheet
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.AdminTextField
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.GoldButton
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.formatXu

private const val VND_PER_XU = 100L
private val quickAmounts = listOf(50_000L, 100_000L, 200_000L, 500_000L, 1_000_000L, 2_000_000L)

@Composable
internal fun DepositSheet(
  state: DepositUiState,
  onDismiss: () -> Unit,
  onAmountChange: (String) -> Unit,
  onQuickSelect: (Long) -> Unit,
  onNoteChange: (String) -> Unit,
  onConfirm: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val amountValue = state.amountValue
  val amountDisplay = if (state.amount.isEmpty()) "" else formatXu(amountValue)
  AppModalBottomSheet(onDismissRequest = onDismiss, modifier = modifier) {
    Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 34.dp)) {
      Text(
        text = stringResource(R.string.deposit_title),
        color = MVVMJetPackComposeColors.Ink,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = GameAdminSerif,
      )
      Text(
        text = stringResource(R.string.deposit_target, state.username, state.balanceFormatted),
        color = MVVMJetPackComposeColors.Muted,
        fontSize = 13.sp,
        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
      )

      SheetLabel(text = stringResource(R.string.deposit_quick_label))
      Column(
        modifier = Modifier.padding(top = 8.dp, bottom = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        quickAmounts.chunked(3).forEach { row ->
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            row.forEach { amount ->
              QuickAmountChip(
                label = quickAmountLabel(amount),
                isSelected = amountValue == amount,
                onClick = { onQuickSelect(amount) },
                modifier = Modifier.weight(1f),
              )
            }
          }
        }
      }

      SheetLabel(text = stringResource(R.string.deposit_or_input_label))
      AdminTextField(
        value = amountDisplay,
        onValueChange = onAmountChange,
        placeholder = stringResource(R.string.deposit_amount_placeholder),
        background = MVVMJetPackComposeColors.InputBg,
        fontSize = 17,
        fontWeight = FontWeight.Bold,
        isNumeric = true,
        modifier = Modifier.padding(top = 8.dp, bottom = 6.dp),
      )
      Text(
        text = stringResource(R.string.deposit_vnd_hint, formatXu(amountValue * VND_PER_XU)),
        color = MVVMJetPackComposeColors.Muted,
        fontSize = 12.sp,
        modifier = Modifier.padding(bottom = 14.dp),
      )

      AdminTextField(
        value = state.note,
        onValueChange = onNoteChange,
        placeholder = stringResource(R.string.deposit_note_placeholder),
        background = MVVMJetPackComposeColors.InputBg,
        fontSize = 13,
        paddingVertical = 12.dp,
        modifier = Modifier.padding(bottom = 18.dp),
      )

      GoldButton(
        text = if (amountValue > 0L) {
          stringResource(R.string.deposit_confirm_with_amount, formatXu(amountValue))
        } else {
          stringResource(R.string.deposit_confirm)
        },
        onClick = onConfirm,
        enabled = amountValue > 0L,
        fontSize = 15,
        minHeight = 50.dp,
        modifier = Modifier.fillMaxWidth(),
      )
    }
  }
}

@Composable
private fun quickAmountLabel(amount: Long): String = if (amount >= 1_000_000L) {
  stringResource(R.string.deposit_quick_million, amount / 1_000_000L)
} else {
  stringResource(R.string.deposit_quick_thousand, amount / 1_000L)
}

@Composable
private fun SheetLabel(text: String, modifier: Modifier = Modifier) {
  Text(
    modifier = modifier,
    text = text,
    color = MVVMJetPackComposeColors.Muted,
    fontSize = 11.sp,
    fontWeight = FontWeight.Bold,
  )
}

@Composable
private fun QuickAmountChip(label: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
  val background = if (isSelected) MVVMJetPackComposeColors.Accent else MVVMJetPackComposeColors.InputBg
  val contentColor = if (isSelected) MVVMJetPackComposeColors.OnAccent else MVVMJetPackComposeColors.Muted
  val border = if (isSelected) MVVMJetPackComposeColors.Accent else MVVMJetPackComposeColors.BorderSoft
  Box(
    modifier = modifier
      .height(44.dp)
      .clip(RoundedCornerShape(10.dp))
      .background(color = background)
      .border(width = 1.dp, color = border, shape = RoundedCornerShape(10.dp))
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center,
  ) {
    Text(text = label, color = contentColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
  }
}
