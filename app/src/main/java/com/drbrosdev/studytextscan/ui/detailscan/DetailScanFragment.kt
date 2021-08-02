package com.drbrosdev.studytextscan.ui.detailscan

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
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
import com.drbrosdev.studytextscan.util.showShortToast
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
        Click events
         */
        binding.apply {
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

//    private fun deleteAlertDialog() {
//        val alertDialogBuilder = AlertDialog.Builder(requireContext())
//        alertDialogBuilder.setTitle("Title")
//        alertDialogBuilder.setMessage("Message")
//        alertDialogBuilder.setPositiveButton("Yes") { dialog, which ->
//            showShortToast("Message Yes")
//        }
//        alertDialogBuilder.setNegativeButton("No") { dialog, which ->
//            showShortToast("Message No")
//        }
//        val built = alertDialogBuilder.create()
//        built.show()
//        val buttonYes = built.getButton(DialogInterface.BUTTON_POSITIVE)
//        with(buttonYes) {
//            setBackgroundColor(Color.BLACK)
//            setTextColor(Color.WHITE)
//        }
//        val buttonNo = built.getButton(DialogInterface.BUTTON_NEGATIVE)
//        with(buttonNo) {
//            setBackgroundColor(Color.BLACK)
//            setTextColor(Color.WHITE)
//            setPadding(20, 0, 20, 0)
//        }
//    }
}