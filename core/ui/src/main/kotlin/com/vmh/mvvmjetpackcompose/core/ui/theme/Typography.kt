/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vmh.mvvmjetpackcompose.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.vmh.mvvmjetpackcompose.core.resource.R as CoreResourceR

internal val DefaultTypography = Typography()

@Immutable
data class Typography(
  val textStyleXSmallRegular: TextStyle = TextStyle(
    fontSize = 10.sp,
    fontWeight = FontWeight.Normal,
    fontFamily = RobotoRegular400,
  ),
  val textStyleXSmallBold: TextStyle = TextStyle(
    fontSize = 10.sp,
    fontWeight = FontWeight.Bold,
    fontFamily = RobotoBold700,
  ),
  val textStyleSmallRegular: TextStyle = TextStyle(
    fontSize = 12.sp,
    fontWeight = FontWeight.Normal,
    fontFamily = RobotoRegular400,
  ),
  val textStyleSmallMedium: TextStyle = TextStyle(
    fontSize = 12.sp,
    fontWeight = FontWeight.Medium,
    fontFamily = RobotoMedium500,
  ),
  val textStyleSmallBold: TextStyle = TextStyle(
    fontSize = 12.sp,
    fontWeight = FontWeight.Bold,
    fontFamily = RobotoBold700,
  ),
  val textStyleBaseRegular: TextStyle = TextStyle(
    fontSize = 14.sp,
    fontWeight = FontWeight.Normal,
    fontFamily = RobotoRegular400,
  ),
  val textStyleBaseMedium: TextStyle = TextStyle(
    fontSize = 14.sp,
    fontWeight = FontWeight.Medium,
    fontFamily = RobotoMedium500,
  ),
  val textStyleBaseBold: TextStyle = TextStyle(
    fontSize = 14.sp,
    fontWeight = FontWeight.Bold,
    fontFamily = RobotoBold700,
  ),
  val textStyleMediumRegular: TextStyle = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.Normal,
    fontFamily = RobotoRegular400,
  ),
  val textStyleMediumMedium: TextStyle = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.Medium,
    fontFamily = RobotoMedium500,
  ),
  val textStyleMediumBold: TextStyle = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.Bold,
    fontFamily = RobotoBold700,
  ),
  val textStyleLargeMedium: TextStyle = TextStyle(
    fontSize = 18.sp,
    fontWeight = FontWeight.Medium,
    fontFamily = RobotoMedium500,
  ),
  val textStyleLargeBold: TextStyle = TextStyle(
    fontSize = 18.sp,
    fontWeight = FontWeight.Bold,
    fontFamily = RobotoBold700,
  ),
  val textStyleXLargeRegular: TextStyle = TextStyle(
    fontSize = 20.sp,
    fontWeight = FontWeight.Normal,
    fontFamily = RobotoRegular400,
  ),
  val textStyleXLargeMedium: TextStyle = TextStyle(
    fontSize = 20.sp,
    fontWeight = FontWeight.Medium,
    fontFamily = RobotoMedium500,
  ),
  val textStyleXLargeBold: TextStyle = TextStyle(
    fontSize = 20.sp,
    fontWeight = FontWeight.Bold,
    fontFamily = RobotoBold700,
  ),
  val textStyleXXLargeRegular: TextStyle = TextStyle(
    fontSize = 24.sp,
    fontWeight = FontWeight.Normal,
    fontFamily = RobotoRegular400,
  ),
  val textStyleXXLargeMedium: TextStyle = TextStyle(
    fontSize = 24.sp,
    fontWeight = FontWeight.Medium,
    fontFamily = RobotoMedium500,
  ),
  val textStyleXXLargeBold: TextStyle = TextStyle(
    fontSize = 24.sp,
    fontWeight = FontWeight.Bold,
    fontFamily = RobotoBold700,
  ),
  val textStyleXXXLargeRegular: TextStyle = TextStyle(
    fontSize = 28.sp,
    fontWeight = FontWeight.Normal,
    fontFamily = RobotoRegular400,
  ),
  val textStyleXXXLargeMedium: TextStyle = TextStyle(
    fontSize = 28.sp,
    fontWeight = FontWeight.Medium,
    fontFamily = RobotoMedium500,
  ),
  val textStyleXXXLargeBold: TextStyle = TextStyle(
    fontSize = 28.sp,
    fontWeight = FontWeight.Bold,
    fontFamily = RobotoBold700,
  ),
  val textStyleXXXXLargeRegular: TextStyle = TextStyle(
    fontSize = 32.sp,
    fontWeight = FontWeight.Normal,
    fontFamily = RobotoRegular400,
  ),
  val textStyleXXXXLargeMedium: TextStyle = TextStyle(
    fontSize = 32.sp,
    fontWeight = FontWeight.Medium,
    fontFamily = RobotoMedium500,
  ),
  val textStyleXXXXLargeBold: TextStyle = TextStyle(
    fontSize = 32.sp,
    fontWeight = FontWeight.Bold,
    fontFamily = RobotoBold700,
  ),
)

// Define FontFamily objects outside of Composable
@Stable
val RobotoRegular400 = FontFamily(Font(CoreResourceR.font.roboto_regular_400))

@Stable
val RobotoMedium500 = FontFamily(Font(CoreResourceR.font.roboto_medium_500))

@Stable
val RobotoBold700 = FontFamily(Font(CoreResourceR.font.roboto_bold_700))
