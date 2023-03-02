package com.cookieinformation.mobileconsents.models

import android.graphics.Typeface

public data class MobileConsentCustomUI(val primaryColor: Int, val sdkTextStyle: SdkTextStyle?)

public data class SdkTextStyle(val titleStyle: TitleStyle?, val subtitleStyle: SubtitleStyle?, val bodyStyle: BodyStyle?)

public data class TitleStyle(val typeface: Typeface?)
public data class SubtitleStyle(val typeface: Typeface?)
public data class BodyStyle(val typeface: Typeface?)