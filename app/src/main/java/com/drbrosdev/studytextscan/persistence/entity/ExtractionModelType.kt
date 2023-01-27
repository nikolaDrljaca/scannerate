package com.drbrosdev.studytextscan.persistence.entity

enum class ExtractionModelType {
    EMAIL,
    PHONE,
    URL,
    OTHER
}

data class ExtractionModel(
    val id: Int,
    val scanId: Int,
    val type: ExtractionModelType,
    val content: String
)

fun ExtractionModel.toFilteredTextModel(): FilteredTextModel {
   return FilteredTextModel(
       filteredTextModelId = id,
       scanId = scanId,
       type = type.name.lowercase(),
       content = content
   )
}

fun FilteredTextModel.toExtractionModel(): ExtractionModel {
    val parsedType = when (type) {
        "email" -> ExtractionModelType.EMAIL
        "phone" -> ExtractionModelType.PHONE
        "link" -> ExtractionModelType.URL
        else -> ExtractionModelType.OTHER
    }
    return ExtractionModel(
        id = filteredTextModelId,
        scanId = scanId,
        type = parsedType,
        content = content
    )
}