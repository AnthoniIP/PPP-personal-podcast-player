package com.ipsoft.ppp.util

import androidx.compose.ui.text.intl.Locale
import java.util.Locale as JavaLocale

val regionCode = Locale.current.region.lowercase(JavaLocale.getDefault())

val languageCode = Locale.current.language
