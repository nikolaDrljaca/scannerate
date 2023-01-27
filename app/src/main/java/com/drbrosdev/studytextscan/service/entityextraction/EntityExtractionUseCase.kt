package com.drbrosdev.studytextscan.service.entityextraction

import com.drbrosdev.studytextscan.persistence.entity.ExtractionModelType
import com.google.mlkit.nl.entityextraction.Entity
import com.google.mlkit.nl.entityextraction.EntityExtraction
import com.google.mlkit.nl.entityextraction.EntityExtractorOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class EntityExtractionUseCase(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val entityExtractor = EntityExtraction.getClient(
        EntityExtractorOptions.Builder(EntityExtractorOptions.ENGLISH)
            .build()
    )

    suspend operator fun invoke(input: String) = withContext(dispatcher) {
        kotlin.runCatching {
            val resultModels = mutableListOf<ExtractionResultModel>()
            val downloadModelResult = entityExtractor.downloadModelIfNeeded().await()
            val annotations = entityExtractor.annotate(input).await()
            annotations.forEach {
                resultModels.addAll(it.entities.map { entity ->
                    val type = when(entity.type) {
                        Entity.TYPE_EMAIL -> ExtractionModelType.EMAIL
                        Entity.TYPE_PHONE -> ExtractionModelType.PHONE
                        Entity.TYPE_URL -> ExtractionModelType.URL
                        else -> ExtractionModelType.OTHER
                    }
                    ExtractionResultModel(
                        type = type,
                        content = it.annotatedText
                    )
                })
            }
            resultModels.toList()
        }
    }
}