package com.broco.tdc_food_chart.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.broco.tdc_food_chart.R

/**
 * 欧文（ブランド名・価格・件数・英ラベル）: Cormorant Garamond（可変フォント、300 / 400、italic）
 * 和文（本文すべて）: Zen Old Mincho（400 / 500）
 * サンセリフは使わない。
 */
@OptIn(ExperimentalTextApi::class)
val CormorantGaramond: FontFamily = FontFamily(
    Font(
        R.font.cormorant_garamond,
        weight = FontWeight.Light,
        variationSettings = FontVariation.Settings(FontVariation.weight(300)),
    ),
    Font(
        R.font.cormorant_garamond,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400)),
    ),
    Font(
        R.font.cormorant_garamond,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(FontVariation.weight(500)),
    ),
    Font(
        R.font.cormorant_garamond_italic,
        weight = FontWeight.Light,
        style = FontStyle.Italic,
        variationSettings = FontVariation.Settings(FontVariation.weight(300)),
    ),
    Font(
        R.font.cormorant_garamond_italic,
        weight = FontWeight.Normal,
        style = FontStyle.Italic,
        variationSettings = FontVariation.Settings(FontVariation.weight(400)),
    ),
)

val ZenOldMincho: FontFamily = FontFamily(
    Font(R.font.zen_old_mincho_regular, weight = FontWeight.Normal),
    Font(R.font.zen_old_mincho_medium, weight = FontWeight.Medium),
)

/** デザイン指定に対応する名前付きスタイル。px 値は 390px フレーム基準なので sp にそのまま写している。 */
object TdcType {
    /** ヘッダーのブランド名（300 / ls .2em）。 */
    val BrandTitle = TextStyle(
        fontFamily = CormorantGaramond,
        fontWeight = FontWeight.Light,
        fontSize = 17.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.2.em(17),
        color = TdcColors.Heading,
    )

    /** スプラッシュ／オンボーディングのブランド名。 */
    val BrandTitleLarge = BrandTitle.copy(fontSize = 26.sp, lineHeight = 34.sp, letterSpacing = 0.2.em(26))

    /** 「探る昼とめぐる夜」（真鍮）。 */
    val BrandSub = TextStyle(
        fontFamily = ZenOldMincho,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.18.em(10),
        color = TdcColors.Brass,
    )
    val BrandSubLarge = BrandSub.copy(fontSize = 12.sp, lineHeight = 22.sp, letterSpacing = 0.24.em(12))

    /** セクションラベル（英・italic・uppercase・真鍮）。 */
    val SectionLabel = TextStyle(
        fontFamily = CormorantGaramond,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Italic,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.18.em(11),
        color = TdcColors.Brass,
    )

    /** 一覧行のメニュー名（500）。 */
    val RowTitle = TextStyle(
        fontFamily = ZenOldMincho,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 23.sp,
        letterSpacing = 0.02.em(14),
        color = TdcColors.TextStrong,
    )

    /** 一覧行の価格（Cormorant・シャンパン）。 */
    val RowPrice = TextStyle(
        fontFamily = CormorantGaramond,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.04.em(15),
        color = TdcColors.Champagne,
    )

    /** 一覧行の店舗名。 */
    val RowShop = TextStyle(
        fontFamily = ZenOldMincho,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 19.sp,
        color = TdcColors.TextMid2,
    )

    /** 一覧行の補助行（エリア・✓ 食べた）。 */
    val RowSub = TextStyle(
        fontFamily = ZenOldMincho,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        color = TdcColors.TextWeak2,
    )

    /** バッジ・チップ（小）。 */
    val Badge = TextStyle(
        fontFamily = ZenOldMincho,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.05.em(10),
    )

    /** チップ（絞り込み・並び替え等）。 */
    val Chip = TextStyle(
        fontFamily = ZenOldMincho,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.05.em(12),
    )

    /** ボタン（pill）。 */
    val Button = TextStyle(
        fontFamily = ZenOldMincho,
        fontWeight = FontWeight.Normal,
        fontSize = 12.5.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.08.em(12.5),
    )

    /** 本文。 */
    val Body = TextStyle(
        fontFamily = ZenOldMincho,
        fontWeight = FontWeight.Normal,
        fontSize = 12.5.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.02.em(12.5),
        color = TdcColors.TextMid,
    )

    /** 本文（弱）。 */
    val BodyMuted = Body.copy(fontSize = 11.5.sp, lineHeight = 21.sp, color = TdcColors.TextWeak)

    /** 小さな注記。 */
    val Caption = TextStyle(
        fontFamily = ZenOldMincho,
        fontWeight = FontWeight.Normal,
        fontSize = 10.5.sp,
        lineHeight = 17.sp,
        color = TdcColors.TextWeak2,
    )

    /** 詳細のメニュー名。 */
    val DetailTitle = TextStyle(
        fontFamily = ZenOldMincho,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 31.sp,
        color = TdcColors.Heading,
    )

    /** 詳細の価格。 */
    val DetailPrice = TextStyle(
        fontFamily = CormorantGaramond,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.04.em(22),
        color = TdcColors.Champagne,
    )

    /** 定義リストのラベル（真鍮）。 */
    val DefLabel = TextStyle(
        fontFamily = ZenOldMincho,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 19.sp,
        letterSpacing = 0.1.em(11),
        color = TdcColors.Brass,
    )

    /** 定義リストの値。 */
    val DefValue = TextStyle(
        fontFamily = ZenOldMincho,
        fontWeight = FontWeight.Normal,
        fontSize = 12.5.sp,
        lineHeight = 23.sp,
        color = TdcColors.TextMid,
    )

    /** 観測記録の大きな数字。 */
    val StatNumber = TextStyle(
        fontFamily = CormorantGaramond,
        fontWeight = FontWeight.Light,
        fontSize = 46.sp,
        lineHeight = 46.sp,
        letterSpacing = 0.04.em(46),
        color = TdcColors.Champagne,
    )
    val StatDenominator = TextStyle(
        fontFamily = CormorantGaramond,
        fontWeight = FontWeight.Light,
        fontSize = 20.sp,
        lineHeight = 20.sp,
        color = TdcColors.TextWeak2,
    )

    /** 空表示のタイトル（no results 等）。 */
    val EmptyTitle = TextStyle(
        fontFamily = CormorantGaramond,
        fontWeight = FontWeight.Light,
        fontSize = 19.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.18.em(19),
        color = TdcColors.Brass,
    )

    /** Cormorant の小さな数値（日付・比率）。 */
    val SmallNumber = TextStyle(
        fontFamily = CormorantGaramond,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        color = TdcColors.Champagne,
    )

    /** 連番（italic・真鍮）。 */
    val Ordinal = TextStyle(
        fontFamily = CormorantGaramond,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Italic,
        fontSize = 13.sp,
        lineHeight = 22.sp,
        color = TdcColors.Brass85,
    )
}

/** letter-spacing を em 指定から sp へ変換する（Compose の TextUnit.em は fontSize に対する比率だが、ここでは明示的に計算しておく）。 */
private fun Double.em(fontSizeSp: Double) = (this * fontSizeSp).sp
private fun Double.em(fontSizeSp: Int) = (this * fontSizeSp).sp

/**
 * Material 3 コンポーネント（DatePicker・Dialog 等）にもブランド書体を行き渡らせるための Typography。
 */
val Typography = Typography(
    displayLarge = TextStyle(fontFamily = CormorantGaramond, fontWeight = FontWeight.Light, fontSize = 57.sp, lineHeight = 64.sp),
    displayMedium = TextStyle(fontFamily = CormorantGaramond, fontWeight = FontWeight.Light, fontSize = 45.sp, lineHeight = 52.sp),
    displaySmall = TextStyle(fontFamily = CormorantGaramond, fontWeight = FontWeight.Light, fontSize = 36.sp, lineHeight = 44.sp),
    headlineLarge = TextStyle(fontFamily = ZenOldMincho, fontWeight = FontWeight.Medium, fontSize = 30.sp, lineHeight = 40.sp),
    headlineMedium = TextStyle(fontFamily = ZenOldMincho, fontWeight = FontWeight.Medium, fontSize = 26.sp, lineHeight = 36.sp),
    headlineSmall = TextStyle(fontFamily = ZenOldMincho, fontWeight = FontWeight.Medium, fontSize = 22.sp, lineHeight = 32.sp),
    titleLarge = TextStyle(fontFamily = ZenOldMincho, fontWeight = FontWeight.Medium, fontSize = 20.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontFamily = ZenOldMincho, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 24.sp),
    titleSmall = TextStyle(fontFamily = ZenOldMincho, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontFamily = ZenOldMincho, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = ZenOldMincho, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = ZenOldMincho, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontFamily = ZenOldMincho, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = ZenOldMincho, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall = TextStyle(fontFamily = ZenOldMincho, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp),
)
