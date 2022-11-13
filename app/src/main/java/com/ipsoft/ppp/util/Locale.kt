package com.ipsoft.ppp.util

import java.util.Locale

val languageCode = getLanguageName()


private fun getLanguageName(): String {
    return when(Locale.getDefault().displayLanguage){
        "english" -> "English"
        "português" -> "Portuguese"
        else -> "Any language"
    }
}
