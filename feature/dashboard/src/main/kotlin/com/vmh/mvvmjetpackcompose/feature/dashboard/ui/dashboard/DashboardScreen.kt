// Design-handoff opacity/dimension/chart values are encoded inline as one-off literals.
@file:Suppress("MagicNumber")

package com.vmh.mvvmjetpackcompose.feature.dashboard.ui.dashboard

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vmh.mvvmjetpackcompose.core.resource.R
import com.vmh.mvvmjetpackcompose.core.ui.common.DefaultGetAppErrorMessageForInline
import com.vmh.mvvmjetpackcompose.core.ui.common.LoadingIndicator
import com.vmh.mvvmjetpackcompose.core.ui.theme.GameAdminPageBrush
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme
import com.vmh.mvvmjetpackcompose.feature.dashboard.presentation.dashboard.DashboardUiState
import com.vmh.mvvmjetpackcompose.feature.dashboard.presentation.dashboard.DashboardViewModel
import com.vmh.mvvmjetpackcompose.ui.widget.common.CommonAppErrorContent
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.GameAdminTopBar
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.InitialBadge
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.SectionTitle
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.SerifValue
import com.vmh.mvvmjetpackcompose.ui.widget.gameadmin.adminCard
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun DashboardRoute(
  onNavigateToHistory: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: DashboardViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
  DashboardScreen(
    uiState = uiState,
    onNavigateToHistory = onNavigateToHistory,
    modifier = modifier,
  )
}

@Composable
internal fun DashboardScreen(
  uiState: DashboardUiState,
  onNavigateToHistory: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(brush = GameAdminPageBrush),
  ) {
    GameAdminTopBar(title = stringResource(R.string.dashboard_title))
    when (uiState) {
      DashboardUiState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LoadingIndicator()
      }

      is DashboardUiState.Error -> CommonAppErrorContent(
        modifier = Modifier.fillMaxSize(),
        appError = uiState.error,
        getAppErrorMessage = DefaultGetAppErrorMessageForInline,
      )

      is DashboardUiState.Content -> Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        HeroCard()
        StatGrid()
        ChartCard()
        RecentTransactionsCard(
          items = uiState.recentTransactions,
          onSeeAll = onNavigateToHistory,
        )
      }
    }
  }
}

private data class StatCard(
  @param:StringRes val labelResId: Int,
  @param:StringRes val valueResId: Int,
  @param:StringRes val deltaResId: Int,
  val deltaColor: Color,
  val valueColor: Color,
)

private data class ChartBar(val day: String, val value: String, val fraction: Float, val isHighlighted: Boolean)

private val statCards = listOf(
  StatCard(
    R.string.dashboard_stat_accounts_created_label,
    R.string.dashboard_stat_accounts_created_value,
    R.string.dashboard_stat_accounts_created_delta,
    MVVMJetPackComposeColors.Green,
    MVVMJetPackComposeColors.Ink,
  ),
  StatCard(
    R.string.dashboard_stat_accounts_locked_label,
    R.string.dashboard_stat_accounts_locked_value,
    R.string.dashboard_stat_accounts_locked_delta,
    MVVMJetPackComposeColors.Muted,
    MVVMJetPackComposeColors.Red,
  ),
  StatCard(
    R.string.dashboard_stat_revenue_label,
    R.string.dashboard_stat_revenue_value,
    R.string.dashboard_stat_revenue_delta,
    MVVMJetPackComposeColors.Green,
    MVVMJetPackComposeColors.Ink,
  ),
  StatCard(
    R.string.dashboard_stat_transactions_label,
    R.string.dashboard_stat_transactions_value,
    R.string.dashboard_stat_transactions_delta,
    MVVMJetPackComposeColors.Green,
    MVVMJetPackComposeColors.Ink,
  ),
)

private val chartBars: List<ChartBar> = run {
  val raw = listOf("T2" to 3.2f, "T3" to 2.6f, "T4" to 4.1f, "T5" to 3.4f, "T6" to 5.6f, "T7" to 6.8f, "CN" to 4.9f)
  val max = raw.maxOf { it.second }
  raw.mapIndexed { index, (day, value) ->
    ChartBar(
      day = day,
      value = String.format(java.util.Locale.forLanguageTag("vi-VN"), "%.1f", value),
      fraction = value / max,
      isHighlighted = index == raw.lastIndex - 1,
    )
  }
}

@Composable
private fun HeroCard(modifier: Modifier = Modifier) {
  val shape = RoundedCornerShape(16.dp)
  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(shape)
      .background(
        brush = Brush.linearGradient(listOf(MVVMJetPackComposeColors.PanelTint, MVVMJetPackComposeColors.PanelTint2)),
        shape = shape,
      )
      .border(width = 1.dp, color = MVVMJetPackComposeColors.BorderStrong, shape = shape)
      .padding(18.dp),
  ) {
    Text(
      text = stringResource(R.string.dashboard_today_deposit_label),
      color = MVVMJetPackComposeColors.Muted,
      fontSize = 11.sp,
    )
    SerifValue(
      text = stringResource(R.string.dashboard_today_deposit_value),
      color = MVVMJetPackComposeColors.GoldValue,
      fontSize = 30,
      modifier = Modifier.padding(top = 4.dp),
    )
    Text(
      text = stringResource(R.string.dashboard_today_deposit_delta),
      color = MVVMJetPackComposeColors.Green,
      fontSize = 12.sp,
      modifier = Modifier.padding(top = 4.dp),
    )
  }
}

@Composable
private fun StatGrid(modifier: Modifier = Modifier) {
  Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
    statCards.chunked(2).forEach { rowCards ->
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        rowCards.forEach { card ->
          StatCardItem(card = card, modifier = Modifier.weight(1f))
        }
      }
    }
  }
}

@Composable
private fun StatCardItem(card: StatCard, modifier: Modifier = Modifier) {
  Column(modifier = modifier.adminCard().padding(14.dp)) {
    Text(text = stringResource(card.labelResId), color = MVVMJetPackComposeColors.Muted, fontSize = 11.sp)
    SerifValue(
      text = stringResource(card.valueResId),
      color = card.valueColor,
      fontSize = 19,
      modifier = Modifier.padding(top = 4.dp),
    )
    Text(
      text = stringResource(card.deltaResId),
      color = card.deltaColor,
      fontSize = 11.sp,
      modifier = Modifier.padding(top = 2.dp),
    )
  }
}

@Composable
private fun ChartCard(modifier: Modifier = Modifier) {
  Column(modifier = modifier.fillMaxWidth().adminCard().padding(16.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.Bottom,
    ) {
      SectionTitle(text = stringResource(R.string.dashboard_chart_title))
      Text(
        text = stringResource(R.string.dashboard_chart_unit),
        color = MVVMJetPackComposeColors.Muted,
        fontSize = 11.sp,
      )
    }
    Row(
      modifier = Modifier.fillMaxWidth().height(110.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.Bottom,
    ) {
      chartBars.forEach { bar -> BarColumn(bar = bar, modifier = Modifier.weight(1f)) }
    }
  }
}

@Composable
private fun BarColumn(bar: ChartBar, modifier: Modifier = Modifier) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(6.dp),
  ) {
    Text(text = bar.value, color = MVVMJetPackComposeColors.Muted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    val barModifier = Modifier
      .fillMaxWidth()
      .height((bar.fraction * 80).dp)
      .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp, bottomStart = 2.dp, bottomEnd = 2.dp))
    Box(
      modifier = if (bar.isHighlighted) {
        barModifier.background(
          brush = Brush.verticalGradient(listOf(MVVMJetPackComposeColors.Accent, MVVMJetPackComposeColors.AccentDeep)),
        )
      } else {
        barModifier.background(color = MVVMJetPackComposeColors.BarIdle)
      },
    )
    Text(text = bar.day, color = MVVMJetPackComposeColors.Faint, fontSize = 10.sp)
  }
}

@Composable
private fun RecentTransactionsCard(
  items: ImmutableList<DashboardUiState.RecentTransactionUiItem>,
  onSeeAll: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier.fillMaxWidth().adminCard().padding(16.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      SectionTitle(text = stringResource(R.string.dashboard_recent_transactions_title))
      Text(
        text = stringResource(R.string.dashboard_recent_transactions_see_all),
        color = MVVMJetPackComposeColors.Accent,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.clickable(onClick = onSeeAll),
      )
    }
    items.forEach { item ->
      Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        InitialBadge(text = item.initial, size = 34, radius = 10, fontSize = 13)
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = item.username,
            color = MVVMJetPackComposeColors.Ink,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
          Text(text = item.subtitle, color = MVVMJetPackComposeColors.Muted, fontSize = 11.sp)
        }
        Text(
          text = item.signedAmount,
          color = if (item.isDeposit) MVVMJetPackComposeColors.GoldValue else MVVMJetPackComposeColors.Red,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
        )
      }
      Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(color = MVVMJetPackComposeColors.Divider))
    }
  }
}

@Preview
@Composable
private fun DashboardScreenPreview() {
  MVVMJetpackComposeTheme {
    DashboardScreen(
      uiState = DashboardUiState.Content(recentTransactions = persistentListOf()),
      onNavigateToHistory = {},
    )
  }
}
