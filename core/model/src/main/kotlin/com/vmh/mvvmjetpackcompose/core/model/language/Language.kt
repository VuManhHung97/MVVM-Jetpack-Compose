package com.vmh.mvvmjetpackcompose.core.model.language

data class Language(val id: Long, val originalName: String, val languageCode: LanguageCode) {
  enum class LanguageCode(val tag: String?) {
    En(tag = "en"),
    Ja(tag = "ja"),
    Unknown(tag = null),
    ;

    companion object {
      fun fromTag(tag: String?): LanguageCode = entries.find { it.tag == tag } ?: Unknown
    }
  }
}
