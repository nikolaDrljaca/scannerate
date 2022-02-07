package com.drbrosdev.studytextscan.ui.home

import com.drbrosdev.studytextscan.service.textFilter.TextFilterService
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ScanTextFromImageUseCase(
    private val filterService: TextFilterService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    //TODO maybe inject these to not create them everytime? - constructor injection
    private val recognizers = listOf(
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS),
        TextRecognition.getClient(DevanagariTextRecognizerOptions.Builder().build()),
        TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build()),
        TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build()),
        TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build()),
    )

    //TODO: Error checking
    suspend operator fun invoke(image: InputImage) = withContext(dispatcher) {
        val completeText = StringBuilder()
        val chips = mutableListOf<Pair<String, String>>()

        for (textRecognizer in recognizers) {
            val result = textRecognizer.process(image).await()

            if (result.text.isNotBlank()) {
                result.textBlocks.forEach { block ->
                    block.lines.forEach { line ->
                        line.elements.forEach { element ->
                            chips.addAll(filterService.filterTextForEmails(element.text))
                            chips.addAll(filterService.filterTextForPhoneNumbers(element.text))
                            chips.addAll(filterService.filterTextForLinks(element.text))
                            completeText.append(element.text + " ")
                        }
                    }
                }
                //TODO Not break, test with image that has multiple scripts
//                break
            }
        }
        completeText.toString() to chips
    }
}