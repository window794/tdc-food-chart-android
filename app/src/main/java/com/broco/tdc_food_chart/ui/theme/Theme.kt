package com.broco.tdc_food_chart.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

/**
 * TDC FOOD CHART のテーマ。常にダーク（深いネイビー）。
 * Material 3 は操作体系（BottomSheet・DatePicker・Dialog・タップ領域）の基礎として使い、
 * dynamic color と tonal surface は使わない。surface 系をすべてブランドの面色に固定している。
 */
private val TdcColorScheme = darkColorScheme(
    primary = TdcColors.Brass,
    onPrimary = TdcColors.Navy,
    primaryContainer = TdcColors.Brass16,
    onPrimaryContainer = TdcColors.ChampagneStrong,
    secondary = TdcColors.Champagne,
    onSecondary = TdcColors.Navy,
    secondaryContainer = TdcColors.Brass18,
    onSecondaryContainer = TdcColors.ChampagneStrong,
    tertiary = TdcColors.ChampagnePale,
    onTertiary = TdcColors.Navy,
    background = TdcColors.Navy,
    onBackground = TdcColors.TextStrong,
    surface = TdcColors.Navy,
    onSurface = TdcColors.TextStrong,
    surfaceVariant = TdcColors.Sheet,
    onSurfaceVariant = TdcColors.TextMid2,
    surfaceContainerLowest = TdcColors.Navy,
    surfaceContainerLow = TdcColors.Sheet,
    surfaceContainer = TdcColors.Sheet,
    surfaceContainerHigh = TdcColors.Sheet,
    surfaceContainerHighest = TdcColors.NavyPressed,
    surfaceTint = TdcColors.Navy,
    inverseSurface = TdcColors.TextStrong,
    inverseOnSurface = TdcColors.Navy,
    outline = TdcColors.Brass50,
    outlineVariant = TdcColors.Brass22,
    error = TdcColors.ClosedText,
    onError = TdcColors.Navy,
    errorContainer = TdcColors.ClosedNoteBorder,
    onErrorContainer = TdcColors.ClosedNote,
    scrim = TdcColors.Overlay,
)

/** 角丸は面 4〜5dp・チップのみ pill・シート上端のみ 12dp。 */
private val TdcShapes = Shapes(
    extraSmall = RoundedCornerShape(3.dp),
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(5.dp),
    large = RoundedCornerShape(6.dp),
    extraLarge = RoundedCornerShape(12.dp),
)

@Composable
fun TdcFoodChartTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TdcColorScheme,
        typography = Typography,
        shapes = TdcShapes,
        content = content,
    )
}
