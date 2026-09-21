package com.broco.tdc_food_chart.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * TDC FOOD CHART のブランドトークン（WEB v1.0 ／ Android 2a と共通）。
 * 真鍮・シャンパンは細線と小さなアクセントのみ。面塗りは Brass16 の選択状態まで。発光・グラデーションは使わない。
 */
object TdcColors {
    // 面
    val Navy = Color(0xFF0E1724)          // 背景・カード
    val NavyDeep = Color(0xFF080E17)      // 端末フレーム外（未使用に近い）
    val NavyPressed = Color(0xFF131E2E)   // 行の押下
    val Sheet = Color(0xFF101C2B)         // ボトムシート・ボトムナビ
    val Overlay = Color(0xB8060B12)           // rgba(6,11,18,.72)

    // 文字
    val TextStrong = Color(0xFFEEF1F5)
    val Heading = Color(0xFFF2EFE8)
    val TextMid = Color(0xFFDCE4EC)
    val TextMid2 = Color(0xFFC6D2DE)
    val TextMid3 = Color(0xFFD6DEE8)
    val TextWeak = Color(0xFF9AA7B8)
    val TextWeak2 = Color(0xFF8896A8)
    val TextWeak3 = Color(0xFF93A1B1)
    val ChipOff = Color(0xFFAEBBC9)
    val Placeholder = Color(0xFF7C8A9B)
    val IconMuted = Color(0xFF8E9BAA)

    // 真鍮・シャンパン
    val Brass = Color(0xFFA88B5C)
    val Champagne = Color(0xFFD8C69E)
    val ChampagnePale = Color(0xFFC9B285)
    val ChampagneMuted = Color(0xFFBFAE8F)
    val ChampagneStrong = Color(0xFFE7DCC6)

    // 閉店（注意色）
    val ClosedText = Color(0xFFD3A5A5)
    val ClosedNote = Color(0xFFC4A9A9)
    val ClosedBorder = Color(0x80C69696)
    val ClosedNoteBorder = Color(0x47C69696)

    // 罫線・微細な面（真鍮の alpha）
    val Brass16 = Brass.copy(alpha = 0.16f)
    val Brass18 = Brass.copy(alpha = 0.18f)
    val Brass22 = Brass.copy(alpha = 0.22f)
    val Brass24 = Brass.copy(alpha = 0.24f)
    val Brass26 = Brass.copy(alpha = 0.26f)
    val Brass28 = Brass.copy(alpha = 0.28f)
    val Brass30 = Brass.copy(alpha = 0.30f)
    val Brass34 = Brass.copy(alpha = 0.34f)
    val Brass40 = Brass.copy(alpha = 0.40f)
    val Brass50 = Brass.copy(alpha = 0.50f)
    val Brass60 = Brass.copy(alpha = 0.60f)
    val Brass75 = Brass.copy(alpha = 0.75f)
    val Brass85 = Brass.copy(alpha = 0.85f)

    val HairlineWhite = Color.White.copy(alpha = 0.07f)   // グリッド区切り線
    val TrackWhite = Color.White.copy(alpha = 0.08f)      // プログレスの下地
    val SurfaceFaint = Color.White.copy(alpha = 0.018f)
    val SurfaceFaint2 = Color.White.copy(alpha = 0.03f)
}
