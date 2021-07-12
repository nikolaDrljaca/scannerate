package com.drbrosdev.studytextscan

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.core.view.WindowCompat
import com.drbrosdev.studytextscan.databinding.ActivityMainBinding
import com.drbrosdev.studytextscan.util.updateWindowInsets
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizerOptions
import kotlin.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    /*
        Here we can use a flow or stateFlow, doesn't matter
        As long as the data holder is observable
     */

    /*
    registerForActivityResult api is available inside fragments as well so that's not an issue.
    Here we update our data holder to hold the passed in URI of the image
     */
    private val selectImageRequest = registerForActivityResult(GetContent()) { uri ->
        scanText(uri) {
            binding.textView.text = it
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateWindowInsets(binding.root)

        /*
        On button click launch the image request - "image/*" here indicates that
        we only want images, not videos
         */
         */
        binding.buttonLoadImage.setOnClickListener {
            selectImageRequest.launch("image/*")
        }

    }

    private fun scanText(uri: Uri, display: (String) -> Unit) {
        val completeText = StringBuilder()
        try {
            val image = InputImage.fromFilePath(this, uri)

            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnCompleteListener { task ->
                    val scannedText = task.result
                    for (block in scannedText.textBlocks) {
                        val blockText = block.text
                        completeText.append(blockText)
                        Log.d("DEBUGn", "onCreate: block content: $blockText")
                        for (line in block.lines) {
                            val lineText = line.text
                            //Log.d("DEBUGn", "onCreate: line content: $lineText")
                            for (element in line.elements) {
                                val elementText = element.text
                                //Log.d("DEBUGn", "onCreate: element content: $elementText")
                            }
                        }
                    }
                    display(completeText.toString())
                }
                .addOnFailureListener { e -> throw e }
            
        } catch (e: Exception) {
            Log.e("DEBUGn", "onCreate: ", e)
        }
    }

    /*
    Text Recognition api from googles ml kit can use URI's to process the image and give text.
    No issues here.
     */
}