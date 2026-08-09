package com.vmh.mvvmjetpackcompose.core.ui.theme

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

val primaryLight = Color(0xFF2C638B)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFCCE5FF)
val onPrimaryContainerLight = Color(0xFF001D31)
val secondaryLight = Color(0xFF006874)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFF9EEFFD)
val onSecondaryContainerLight = Color(0xFF001F24)
val tertiaryLight = Color(0xFF2C638B)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFCCE5FF)
val onTertiaryContainerLight = Color(0xFF001D31)
val errorLight = Color(0xFF904A42)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD5)
val onErrorContainerLight = Color(0xFF3B0906)
val backgroundLight = Color(0xFFF7F9FF)
val onBackgroundLight = Color(0xFF181C20)
val surfaceLight = Color(0xFFF7F9FF)
val onSurfaceLight = Color(0xFF181C20)
val surfaceVariantLight = Color(0xFFDBE4E6)
val onSurfaceVariantLight = Color(0xFF3F484A)
val outlineLight = Color(0xFF6F797A)
val outlineVariantLight = Color(0xFFBFC8CA)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF2D3135)
val inverseOnSurfaceLight = Color(0xFFEEF1F6)
val inversePrimaryLight = Color(0xFF99CCF9)
val surfaceDimLight = Color(0xFFD7DADF)
val surfaceBrightLight = Color(0xFFF7F9FF)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFF1F4F9)
val surfaceContainerLight = Color(0xFFEBEEF3)
val surfaceContainerHighLight = Color(0xFFE6E8EE)
val surfaceContainerHighestLight = Color(0xFFE0E2E8)

val primaryDark = Color(0xFF99CCF9)
val onPrimaryDark = Color(0xFF003351)
val primaryContainerDark = Color(0xFF074B72)
val onPrimaryContainerDark = Color(0xFFCCE5FF)
val secondaryDark = Color(0xFF82D3E0)
val onSecondaryDark = Color(0xFF00363D)
val secondaryContainerDark = Color(0xFF004F58)
val onSecondaryContainerDark = Color(0xFF9EEFFD)
val tertiaryDark = Color(0xFF99CCF9)
val onTertiaryDark = Color(0xFF003351)
val tertiaryContainerDark = Color(0xFF064B72)
val onTertiaryContainerDark = Color(0xFFCCE5FF)
val errorDark = MVVMJetPackComposeColors.red40
val onErrorDark = Color(0xFF561E18)
val errorContainerDark = Color(0xFF73342C)
val onErrorContainerDark = Color(0xFFFFDAD5)
val backgroundDark = Color(0xFF171717)
val onBackgroundDark = Color(0xFFE0E2E8)
val surfaceDark = Color(0xFF171717)
val onSurfaceDark = Color(0xFFE0E2E8)
val surfaceVariantDark = Color(0xFF3F484A)
val onSurfaceVariantDark = Color(0xFFBFC8CA)
val outlineDark = Color(0xFF899294)
val outlineVariantDark = Color(0xFF3F484A)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFE0E2E8)
val inverseOnSurfaceDark = Color(0xFF2D3135)
val inversePrimaryDark = Color(0xFF2C638B)
val surfaceDimDark = Color(0xFF101418)
val surfaceBrightDark = Color(0xFF36393E)
val surfaceContainerLowestDark = Color(0xFF0B0F12)
val surfaceContainerLowDark = Color(0xFF181C20)
val surfaceContainerDark = Color(0xFF1C2024)
val surfaceContainerHighDark = Color(0xFF272A2E)
val surfaceContainerHighestDark = Color(0xFF313539)

object MVVMJetPackComposeColors {
  val Primary10 = Color(0x1AE7F1FC)
  val Primary20 = Color(0xFFCEE2F8)
  val Primary30 = Color(0xFF9AC6F1)
  val Primary40 = Color(0xFF7EB9EE)
  val Primary50 = Color(0xFF5DABEA)
  val Primary60 = Color(0xFF2D9EE6)
  val Primary70 = Color(0xFF2C86C2)
  val Primary80 = Color(0xCC2A6E9E)
  val Primary90 = Color(0xE626587D)
  val Primary100 = Color(0xFF21425C)

  val NeutralWhite = Color(0xFFFFFFFF)
  val Neutral10 = Color(0xFFECECEC)
  val Neutral20 = Color(0xFFAFAFAF)
  val Neutral30 = Color(0xFF898989)
  val Neutral40 = Color(0xFF808080)
  val Neutral50 = Color(0xFF6D6D6D)
  val Neutral60 = Color(0xFF5A5A5A)
  val Neutral70 = Color(0xFF484848)
  val Neutral80 = Color(0xFF373737)
  val Neutral90 = Color(0xFF262626)
  val Neutral100 = Color(0xFF171717)
  val NeutralBlack = Color(0xFF000000)

  val Transparent = Color(0x00000000)

  val TransparentBlack5 = Color(0x0D000000)
  val TransparentBlack10 = Color(0x1A000000)
  val TransparentBlack20 = Color(0x33000000)
  val TransparentBlack30 = Color(0x4D000000)
  val TransparentBlack40 = Color(0x66000000)
  val TransparentBlack50 = Color(0x80000000)
  val TransparentBlack60 = Color(0x99000000)
  val TransparentBlack70 = Color(0xB3000000)
  val TransparentBlack80 = Color(0xCC000000)
  val TransparentBlack90 = Color(0xE6000000)

  val TransparentWhite5 = Color(0x0DFFFFFF)
  val TransparentWhite10 = Color(0x1AFFFFFF)
  val TransparentWhite20 = Color(0x33FFFFFF)
  val TransparentWhite30 = Color(0x4DFFFFFF)
  val TransparentWhite40 = Color(0x66FFFFFF)
  val TransparentWhite50 = Color(0x80FFFFFF)
  val TransparentWhite60 = Color(0x99FFFFFF)
  val TransparentWhite70 = Color(0xB3FFFFFF)
  val TransparentWhite80 = Color(0xCCFFFFFF)
  val TransparentWhite90 = Color(0xE6FFFFFF)

  val blue10 = Color(0xFFEAF5FF)
  val blue20 = Color(0xFFBCDFFF)
  val blue30 = Color(0xFF5FB3FF)
  val blue40 = Color(0xFF1B7ED6)
  val blue50 = Color(0xFF64646D)
  val blue60 = Color(0xFF38383D)

  val green10 = Color(0xFFEFFFF4)
  val green20 = Color(0xFFD7FAE0)
  val green30 = Color(0xFF78DA90)
  val green40 = Color(0xFF00AB56)
  val green50 = Color(0xFF007D3A)
  val green60 = Color(0xFF03401F)

  val orange10 = Color(0xFFFFF5EB)
  val orange20 = Color(0xFFFFE2C6)
  val orange30 = Color(0xFFFFB56C)
  val orange40 = Color(0xFFFC820A)
  val orange50 = Color(0xFFB64F00)
  val orange60 = Color(0xFF6C2F00)

  val red10 = Color(0xFFFEF3F2)
  val red20 = Color(0xFFFECDCA)
  val red30 = Color(0xFFFF99A0)
  val red40 = Color(0xFFF04438)
  val red50 = Color(0xFFB42318)
  val red60 = Color(0xFF7A271A)

  val yellow10 = Color(0xFFFFFCED)
  val yellow20 = Color(0xFFFFF5C7)
  val yellow30 = Color(0xFFFFD530)
  val yellow40 = Color(0xFFFFB700)
  val yellow50 = Color(0xFFCC8100)
  val yellow60 = Color(0xFF995200)

  val black100 = Color(0xFF121212)

  // ---------------- Võ Lâm 2 admin palette (parchment / gold) ----------------
  // Backgrounds
  val PageTop = Color(0xFFFAF4E6)
  val PageMid = Color(0xFFF5EEDD)
  val CardBg = Color(0xFFFDF9EF)
  val TileBg = Color(0xFFF0E7D2)
  val InputBg = Color(0xFFFBF6EA)
  val PanelTint = Color(0xFFFBF3DD)
  val PanelTint2 = Color(0xFFF3E7C8)

  // Borders
  val Border = Color(0xFFE3D7BA)
  val BorderStrong = Color(0xFFD4C29A)
  val BorderSoft = Color(0xFFD8C9A6)
  val BorderHover = Color(0xFFC6AF80)
  val Divider = Color(0xFFF0E7D2)

  // Text / ink
  val Ink = Color(0xFF2E2618)
  val InkTitle = Color(0xFF2A2214)
  val Muted = Color(0xFF8A7A58)
  val Faint = Color(0xFFA2926E)

  // Accent (gold)
  val Accent = Color(0xFFB4893C)
  val AccentDeep = Color(0xFF8A6A2E)
  val AccentSoft = Color(0xFFA8813B)
  val GoldValue = Color(0xFFA67C22)
  val OnAccent = Color(0xFF1A130A)

  // Semantic
  val Green = Color(0xFF3E8E5F)
  val Red = Color(0xFFC4553F)
  val BarIdle = Color(0xFFD4C29A)

  // Status pills
  val StatusActiveBg = Color(0x243E8E5F)
  val StatusLockedBg = Color(0x21C4553F)

  // Scrim
  val Scrim = Color(0x663E321E)
}

/** Page background gradient: parchment top glow into a flat mid tone. */
@Stable
val GameAdminPageBrush: Brush
  get() = Brush.verticalGradient(
    0f to MVVMJetPackComposeColors.PageTop,
    0.18f to MVVMJetPackComposeColors.PageMid,
    1f to MVVMJetPackComposeColors.PageMid,
  )

/** Gold button gradient (accent → soft gold). */
@Stable
val GameAdminGoldButtonBrush: Brush
  get() = Brush.linearGradient(
    colors = listOf(MVVMJetPackComposeColors.Accent, MVVMJetPackComposeColors.AccentSoft),
  )

/** Logo badge gradient (accent → darker gold). */
@Stable
val GameAdminLogoBrush: Brush
  get() = Brush.linearGradient(
    colors = listOf(MVVMJetPackComposeColors.Accent, MVVMJetPackComposeColors.AccentDeep),
  )

/**
 * The design uses "Noto Serif" for headings. To stay offline without bundling font binaries we
 * approximate with the platform serif; layout, weights and colors carry the visual identity.
 */
@Stable
val GameAdminSerif: FontFamily = FontFamily.Serif

@Stable
object ContentAlpha {
  @Stable
  const val High: Float = 1.00f

  @Stable
  const val Medium: Float = 0.74f

  // Value lifted from
  // https://developer.android.com/jetpack/compose/designsystems/material2-material3#emphasis-and
  @Stable
  const val Disabled: Float = 0.38f
}
