package com.drbrosdev.studytextscan.service.entityextraction

import com.drbrosdev.studytextscan.persistence.entity.ExtractionModelType

data class ExtractionResultModel(
    val type: ExtractionModelType,
    val content: String
)
