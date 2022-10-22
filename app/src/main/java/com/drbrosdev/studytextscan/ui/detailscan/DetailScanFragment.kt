package com.drbrosdev.studytextscan.ui.detailscan

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.anim.InsetsWithKeyboardAnimationCallback
import com.drbrosdev.studytextscan.anim.InsetsWithKeyboardCallback
import com.drbrosdev.studytextscan.databinding.FragmentScanDetailBinding
import com.drbrosdev.studytextscan.service.pdfExport.PdfExportServiceImpl
import com.drbrosdev.studytextscan.util.*
import com.google.android.material.transition.MaterialSharedAxis
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import java.util.*


class DetailScanFragment : Fragment(R.layout.fragment_scan_detail) {
    private val binding: FragmentScanDetailBinding by viewBinding(FragmentScanDetailBinding::bind)
    private val viewModel: DetailScanViewModel by stateViewModel(state = { requireArguments() })
    private val pdfExportService: PdfExportServiceImpl by inject()
    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWindowInsets(binding.root)

        val insetsWithKeyboardCallback = InsetsWithKeyboardCallback(requireActivity().window)
        ViewCompat.setWindowInsetsAnimationCallback(binding.root, insetsWithKeyboardCallback)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root, insetsWithKeyboardCallback)

        val insetsWithKeyboardAnimationCallback = InsetsWithKeyboardAnimationCallback(binding.bottomBar)
        ViewCompat.setWindowInsetsAnimationCallback(binding.bottomBar, insetsWithKeyboardAnimationCallback)

        collectFlow(viewModel.state) { state ->
            state.scan?.let { scan ->
                binding.apply {
                    textViewDateCreated.text =
                        getString(R.string.text_date_created, dateAsString(scan.dateCreated))
                    textViewDateModified.text =
                        getString(R.string.text_date_modified, dateAsString(scan.dateModified))
                    editTextScanContent.setText(scan.scanText, TextView.BufferType.EDITABLE)
                    editTextScanTitle.setText(scan.scanTitle, TextView.BufferType.EDITABLE)

                    val pinColor = if (scan.isPinned) getColor(R.color.heavy_blue)
                        else getColor(R.color.light_blue)
                    imageViewPin.setColorFilter(pinColor)

                    recyclerViewChips.withModels {
                        state.filteredTextModels.let {
                            it.forEach { model ->
                                chip {
                                    id(model.filteredTextModelId)
                                    onModelClick {
                                        processFilteredModelIntent(model.type, model.content)
                                    }
                                    model(model)
                                    initCard { cardView ->
                                        when (model.type) {
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
                    showSnackbarShort(
                        getString(R.string.scan_updated),
                        anchor = binding.imageViewCopy
                    )
                }
                is DetailScanEvents.ShowUnsavedChanges -> {
                    showConfirmDialog(
                        title = getString(R.string.save_changes),
                        message = getString(R.string.unsaved_changes),
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
                    message = getString(R.string.delete_scanned_text),
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
                    message = getString(R.string.copied_clip),
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
                                getString(R.string.unsupported_language),
                                anchor = imageViewShare
                            )
                        } else {
                            showSnackbarShort(getString(R.string.loading), anchor = imageViewShare)
                            textToSpeech.speak(
                                editTextScanContent.text.toString(),
                                TextToSpeech.QUEUE_ADD,
                                null,
                                viewModel.scanId.toString()
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
                        message = getString(R.string.no_google_translate),
                        anchor = binding.imageViewShare
                    )
                }
            }

            imageViewPdf.setOnClickListener {
                val arg = bundleOf("pdf_scan_id" to viewModel.scanId)
                findNavController().navigate(
                    R.id.action_detailScanFragment_to_pdfDialogFragment,
                    arg
                )
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

    private fun processFilteredModelIntent(type: String, content: String) {
        try {
            when (type) {
                "phone" -> {
                    val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:$content")
                    }
                    if (requireActivity().packageManager != null)
                        startActivity(dialIntent)
                }
                "email" -> {
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(content))
                    }
                    if (requireActivity().packageManager != null)
                        startActivity(emailIntent)
                }
                "link" -> {
                    val urlIntent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(content)
                    }
                    if (requireActivity().packageManager != null)
                        startActivity(urlIntent)
                }
            }
        } catch (e: ActivityNotFoundException) {
            showSnackbarShort(
                message = getString(R.string.something_went_wrong),
                anchor = binding.imageViewCopy
            )
        }
    }
}