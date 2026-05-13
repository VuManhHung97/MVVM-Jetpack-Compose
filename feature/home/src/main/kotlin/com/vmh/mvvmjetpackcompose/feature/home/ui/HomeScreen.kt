package com.vmh.mvvmjetpackcompose.feature.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.TopAppBarExpandedHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetPackComposeColors
import com.vmh.mvvmjetpackcompose.core.ui.theme.MVVMJetpackComposeTheme

@Composable
internal fun HomeRoute(onNavigateToSearchScreen: () -> Unit, modifier: Modifier = Modifier) {
  Surface(
    modifier = modifier.fillMaxSize(),
    color = MVVMJetPackComposeColors.NeutralBlack,
  ) {
    Box(
      modifier = Modifier
        .windowInsetsPadding(WindowInsets.statusBars)
        .consumeWindowInsets(WindowInsets.statusBars)
        .fillMaxSize(),
    ) {
      HomeContent(onNavigateToSearchScreen = onNavigateToSearchScreen)
    }
  }
}

@Composable
private fun HomeContent(onNavigateToSearchScreen: () -> Unit, modifier: Modifier = Modifier) {
  Scaffold(
    modifier = modifier,
    topBar = { HomeAppBar(onNavigateToSearchScreen = onNavigateToSearchScreen) },
    containerColor = MVVMJetPackComposeColors.Transparent,
  ) { paddingValues ->
    Box(
      modifier = Modifier.padding(paddingValues),
    ) {
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeAppBar(onNavigateToSearchScreen: () -> Unit, modifier: Modifier = Modifier) {
  TopAppBar(
    modifier = modifier,
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MVVMJetPackComposeColors.Transparent,
    ),
    expandedHeight = TopAppBarExpandedHeight,
    title = {
      Icon(
        imageVector = ImageVector.vectorResource(CoreResourceR.drawable.ic_android),
        contentDescription = null,
        tint = Color.Unspecified,
      )
    },
    actions = {
      IconButton(onClick = onNavigateToSearchScreen) {
        Icon(
          modifier = Modifier.size(24.dp),
          imageVector = ImageVector.vectorResource(CoreResourceR.drawable.ic_search),
          contentDescription = null,
          tint = MVVMJetPackComposeColors.Neutral10,
        )
      }
    },
  )
}

@Preview
@Composable
private fun HomeAppBarPreview() {
  MVVMJetpackComposeTheme {
    HomeAppBar(onNavigateToSearchScreen = {})
  }
}
