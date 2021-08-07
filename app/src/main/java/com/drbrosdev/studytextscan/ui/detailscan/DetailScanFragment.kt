package com.drbrosdev.studytextscan.ui.detailscan

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.databinding.FragmentScanDetailBinding
import com.drbrosdev.studytextscan.util.collectFlow
import com.drbrosdev.studytextscan.util.collectStateFlow
import com.drbrosdev.studytextscan.util.dateAsString
import com.drbrosdev.studytextscan.util.getColor
import com.drbrosdev.studytextscan.util.hideKeyboard
import com.drbrosdev.studytextscan.util.showConfirmDialog
import com.drbrosdev.studytextscan.util.showKeyboardOnEditText
import com.drbrosdev.studytextscan.util.showSnackbarShort
import com.drbrosdev.studytextscan.util.updateWindowInsets
import com.drbrosdev.studytextscan.util.viewBinding
import com.google.android.material.transition.MaterialSharedAxis
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import java.util.*


class DetailScanFragment : Fragment(R.layout.fragment_scan_detail) {
    private val binding: FragmentScanDetailBinding by viewBinding()
    private val viewModel: DetailScanViewModel by stateViewModel(state = { requireArguments() })
    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWindowInsets(binding.root)
        /*
        Set color of navbar to match the bottom bar color.
         */
        requireActivity().window.navigationBarColor = getColor(R.color.bottom_bar_color)


        collectStateFlow(viewModel.viewState) { state ->
            state.scan()?.let { scan ->
                binding.apply {
                    textViewDateCreated.text = "Created: ${dateAsString(scan.dateCreated)}"
                    textViewDateModified.text = "Modified: ${dateAsString(scan.dateModified)}"
                    editTextScanContent.setText(scan.scanText, TextView.BufferType.EDITABLE)
                    editTextScanTitle.setText(scan.scanTitle, TextView.BufferType.EDITABLE)

                    val pinColor = if (scan.isPinned) getColor(R.color.heavy_blue)
                        else getColor(R.color.light_blue)
                    imageViewPin.setColorFilter(pinColor)

                    recyclerViewChips.withModels {

                        state.filteredTextModels()?.let {
                            it.forEach { model ->
                                chip {
                                    id(model.filteredTextModelId)
                                    onModelClick {
                                        /*
                                        Func to handle intents based on info
                                         */
                                    }
                                    model(model)
                                    initCard { cardView ->
                                        when(model.type) {
                                            "phone" -> {
                                                cardView.setCardBackgroundColor(getColor(R.color.chip_green))
                                            }
                                            "email" -> {
                                                cardView.setCardBackgroundColor(getColor(R.color.chip_orange))
                                            }
                                            "link" -> {
                                                cardView.setCardBackgroundColor(getColor(R.color.chip_yellow))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        collectFlow(viewModel.events) {
            when (it) {
                is DetailScanEvents.ShowSoftwareKeyboardOnFirstLoad -> {
                    showKeyboardOnEditText(binding.editTextScanTitle)
                }
                is DetailScanEvents.ShowScanUpdated -> {
                    showSnackbarShort("Scan updated.", anchor = binding.imageViewCopy)
                }
                is DetailScanEvents.ShowUnsavedChanges -> {
                    showConfirmDialog(
                        title = "Save changes?",
                        message = "There seem to be unsaved changes.",
                        onPositiveClick = {
                            binding.apply {
                                viewModel.updateScan(
                                    title = editTextScanTitle.text.toString(),
                                    content = editTextScanContent.text.toString()
                                )
                            }
                            hideKeyboard()
                            findNavController().navigateUp()
                        },
                        onNegativeClick = {
                            hideKeyboard()
                            findNavController().navigateUp()
                        }
                    )
                }
                is DetailScanEvents.NavigateUp -> {
                    hideKeyboard()
                    findNavController().navigateUp()
                }
            }
        }

        /*
        Attach a callback when the back button is pressed to act the same way as the
        imageViewBack does.
         */
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            binding.apply {
                viewModel.onNavigateUp(
                    title = editTextScanTitle.text.toString(),
                    content = editTextScanContent.text.toString()
                )
            }
        }

        /*
        Click events
         */
        binding.apply {
            imageViewPin.setOnClickListener {
                viewModel.updateScanPinned()
                imageViewPin.setColorFilter(getColor(R.color.light_blue))
            }

            imageViewSave.setOnClickListener {
                viewModel.updateScan(
                    title = editTextScanTitle.text.toString(),
                    content = editTextScanContent.text.toString()
                )
                hideKeyboard()
            }

            imageViewBack.setOnClickListener {
                viewModel.onNavigateUp(
                    title = editTextScanTitle.text.toString(),
                    content = editTextScanContent.text.toString()
                )
            }

            imageViewDelete.setOnClickListener {
                showConfirmDialog(
                    message = "This will delete the scanned text.",
                    onPositiveClick = {
                        viewModel.deleteScan()
                        hideKeyboard()
                        findNavController().navigateUp()
                    }
                )
            }

            imageViewCopy.setOnClickListener {
                val clipboardManager =
                    requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("raw_data", editTextScanContent.text.toString())
                clipboardManager.setPrimaryClip(clip)
                showSnackbarShort(
                    message = "Copied to clipboard",
                    anchor = binding.imageViewTranslate
                )
            }

            imageViewShare.setOnClickListener {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, editTextScanContent.text.toString())
                    type = "text/plain"
                }
                val intent = Intent.createChooser(shareIntent, null)
                startActivity(intent)
            }

            imageViewVoice.setOnClickListener {
                textToSpeech = TextToSpeech(requireContext()) { status ->
                    if (status == TextToSpeech.SUCCESS) {
                        val hasLanguage = textToSpeech.setLanguage(Locale.US)
                        if (hasLanguage == TextToSpeech.LANG_MISSING_DATA || hasLanguage == TextToSpeech.LANG_NOT_SUPPORTED) {
                            showSnackbarShort(
                                "No supported language found.",
                                anchor = imageViewShare
                            )
                        } else {
                            showSnackbarShort("Loading...", anchor = imageViewShare)
                            textToSpeech.speak(
                                editTextScanContent.text.toString(),
                                TextToSpeech.QUEUE_ADD,
                                null,
                                viewModel.scanUtteranceId()
                            )
                        }
                    }
                }
            }

            imageViewTranslate.setOnClickListener {
                try {
                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND
                    intent.putExtra(Intent.EXTRA_TEXT, editTextScanContent.text.toString().trim())
                    intent.putExtra("key_text_input", editTextScanContent.text.toString().trim())
                    intent.putExtra("key_text_output", "")
                    intent.putExtra("key_language_from", "en")
                    intent.putExtra("key_language_to", "mal")
                    intent.putExtra("key_suggest_translation", "")
                    intent.putExtra("key_from_floating_window", false)
                    intent.component = ComponentName(
                        "com.google.android.apps.translate",
                        "com.google.android.apps.translate.TranslateActivity"
                    )
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    showSnackbarShort(
                        message = "It seems you don't have Google Translate installed.",
                        anchor = binding.imageViewShare
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        /*
        First check if TTS is initialized and if it is (this means its reading)
        stop reading once fragment is closed.
         */
        if (this::textToSpeech.isInitialized) textToSpeech.stop()
        super.onDestroyView()
    }
}