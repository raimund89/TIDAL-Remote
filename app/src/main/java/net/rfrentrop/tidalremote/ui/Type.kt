package net.rfrentrop.tidalremote.ui

import androidx.ui.material.Typography
import androidx.ui.text.font.FontStyle
import androidx.ui.text.font.FontWeight
import androidx.ui.text.font.ResourceFont
import androidx.ui.text.font.fontFamily
import androidx.ui.unit.sp
import net.rfrentrop.tidalremote.R

private val appFontFamily = fontFamily(
        fonts = listOf(
                ResourceFont(
                        resId = R.font.work_sans_bold,
                        weight = FontWeight.Bold,
                        style = FontStyle.Normal
                ),
                ResourceFont(
                        resId = R.font.work_sans_semibold,
                        weight = FontWeight.SemiBold,
                        style = FontStyle.Normal
                ),
                ResourceFont(
                        resId = R.font.work_sans_medium,
                        weight = FontWeight.Medium,
                        style = FontStyle.Normal
                )
        )
)

private val defaultTypography = Typography()
val typography = Typography(
        body1 = defaultTypography.body1.copy(fontFamily = appFontFamily, fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
        body2 = defaultTypography.body2.copy(fontFamily = appFontFamily, fontSize = 14.sp, fontWeight = FontWeight.Medium),
        h1 = defaultTypography.h1.copy(fontFamily = appFontFamily, fontSize = 34.sp, fontWeight = FontWeight.Bold),
        h2 = defaultTypography.h2.copy(fontFamily = appFontFamily, fontSize = 28.sp, fontWeight = FontWeight.SemiBold),
        h3 = defaultTypography.h3.copy(fontFamily = appFontFamily, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
)
