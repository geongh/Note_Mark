package com.solaisc.notemark.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.solaisc.notemark.R

// Set of Material typography styles to start with

val spacegFontBold = FontFamily(Font(R.font.spacegrotesk_bold))
val spacegFontMedium = FontFamily(Font(R.font.spacegrotesk_medium))
val interFontMedium = FontFamily(Font(R.font.inter_medium))
val interFontRegular = FontFamily(Font(R.font.inter_regular))

val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = spacegFontBold,
        fontSize = 36.sp,
        lineHeight = 40.sp
    ),
    titleMedium = TextStyle(
        fontFamily = spacegFontBold,
        fontSize = 32.sp,
        lineHeight = 36.sp
    ),
    titleSmall = TextStyle(
        fontFamily = spacegFontMedium,
        fontSize = 17.sp,
        lineHeight = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = interFontRegular,
        fontSize = 17.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = interFontMedium,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = interFontRegular,
        fontSize = 15.sp,
        lineHeight = 20.sp
    )

    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)