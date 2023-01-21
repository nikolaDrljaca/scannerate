package com.drbrosdev.studytextscan.ui.detailscan

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.persistence.entity.ExtractionModel
import com.drbrosdev.studytextscan.persistence.entity.ExtractionModelType
import com.drbrosdev.studytextscan.ui.support.theme.HeavyBlue
import com.drbrosdev.studytextscan.ui.support.theme.LightBlue
import com.drbrosdev.studytextscan.ui.support.theme.ScannerateTheme
import com.drbrosdev.studytextscan.util.safeNav
import com.drbrosdev.studytextscan.util.showConfirmDialog
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class DetailScanFragment : Fragment() {
    private val viewModel: DetailScanViewModel by viewModel()
    private lateinit var textToSpeech: TextToSpeech
    private val snackbarHostState = SnackbarHostState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onDestroyView() {
        if (this::textToSpeech.isInitialized) textToSpeech.stop()
        super.onDestroyView()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            transitionName = "detail_frag_2"
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                ScannerateTheme {
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    Box(modifier = Modifier.fillMaxSize()) {
                        ScannerateDetailScreen(
                            state = state,
                            onTitleTextChanged = { viewModel.onTitleChange(it) },
                            onContentChanged = { viewModel.onContentChanged(it) },
                            onPinClicked = { viewModel.updateScanPinned() },
                            onChipClicked = { launchExtractedModelIntent(it) },
                            onBackClick = { findNavController().navigateUp() },
                            onPdfExport = { navigateToPdfExport(state.scan?.scanId) },
                            onDeleteClick = { onDeleteClick() },
                            onCopyClick = { copyToClipboard(state.scan?.scanText) },
                            onTranslateClick = { openInTranslate(state.scan?.scanText) },
                            onShareClick = { share(state.scan?.scanText) },
                            onTtsClick = { tts(state.scan?.scanText) }
                        )

                        SnackbarHost(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 72.dp)
                                .navigationBarsPadding(),
                            hostState = snackbarHostState,
                            snackbar = {
                                Snackbar(
                                    snackbarData = it,
                                    backgroundColor = LightBlue,
                                    contentColor = HeavyBlue
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    private fun openInTranslate(input: String?) = input?.let { text ->
        try {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, text)
            intent.putExtra("key_text_input", text)
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
            //snackbar - it seems you don't have translate installed.
        }
    }

    private fun tts(input: String?) = input?.let { text ->
        textToSpeech = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val hasLanguage = textToSpeech.setLanguage(Locale.US)
                if (hasLanguage == TextToSpeech.LANG_MISSING_DATA || hasLanguage == TextToSpeech.LANG_NOT_SUPPORTED) {
                    lifecycleScope.launch {
                        snackbarHostState.showSnackbar(message = getString(R.string.unsupported_language))
                    }
                } else {
                    lifecycleScope.launch {
                        snackbarHostState.showSnackbar(message = getString(R.string.loading))
                    }
                    textToSpeech.speak(
                        text,
                        TextToSpeech.QUEUE_ADD,
                        null,
                        viewModel.state.value.scan?.scanId.toString()
                    )
                }
            }
        }
    }

    private fun copyToClipboard(input: String?) = input?.let { text ->
        val clipboardManager =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("raw_data", text)
        clipboardManager.setPrimaryClip(clip)
        //fire off snackbar
        lifecycleScope.launch { snackbarHostState.showSnackbar(message = getString(R.string.copied_clip)) }
    }

    private fun share(input: String?) = input?.let { text ->
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val intent = Intent.createChooser(shareIntent, null)
        startActivity(intent)
    }

    private fun navigateToPdfExport(scanId: Long?) = scanId?.let {
        val action = DetailScanFragmentDirections.toPdfDialogFragment(it.toInt())
        findNavController().safeNav(action)
    }

    private fun onDeleteClick() {
        showConfirmDialog(
            message = getString(R.string.delete_scanned_text),
            onPositiveClick = {
                viewModel.deleteScan()
                findNavController().navigateUp()
            }
        )
    }

    private fun launchExtractedModelIntent(model: ExtractionModel) {
        try {
            when (model.type) {
                ExtractionModelType.EMAIL -> {
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(model.content))
                    }
                    if (requireActivity().packageManager != null)
                        startActivity(emailIntent)
                }
                ExtractionModelType.PHONE -> {
                    val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${model.content}")
                    }
                    if (requireActivity().packageManager != null)
                        startActivity(dialIntent)
                }
                ExtractionModelType.URL -> {
                    val urlIntent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(model.content)
                    }
                    if (requireActivity().packageManager != null)
                        startActivity(urlIntent)
                }
                ExtractionModelType.OTHER -> copyToClipboard(model.content)
            }
        } catch (e: ActivityNotFoundException) {
            lifecycleScope.launch { snackbarHostState.showSnackbar(message = getString(R.string.something_went_wrong)) }
        }
    }
}