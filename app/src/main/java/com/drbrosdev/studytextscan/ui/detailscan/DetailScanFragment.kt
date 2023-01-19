package com.drbrosdev.studytextscan.ui.detailscan

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.persistence.entity.ExtractionModel
import com.drbrosdev.studytextscan.persistence.entity.ExtractionModelType
import com.drbrosdev.studytextscan.ui.support.theme.ScannerateTheme
import com.drbrosdev.studytextscan.util.safeNav
import com.drbrosdev.studytextscan.util.showConfirmDialog
import com.google.android.material.transition.MaterialSharedAxis
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailScanFragment : Fragment() {
    private val viewModel: DetailScanViewModel by viewModel()
    private lateinit var textToSpeech: TextToSpeech

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

                    ScannerateDetailScreen(
                        state = state,
                        onTitleTextChanged = { viewModel.onTitleChange(it) } ,
                        onContentChanged = { viewModel.onContentChanged(it) },
                        onPinClicked = { viewModel.updateScanPinned() },
                        onChipClicked = { launchExtractedModelIntent(it) },
                        onBackClick = { findNavController().navigateUp() },
                        onPdfExport = { navigateToPdfExport(state.scan?.scanId) },
                        onDeleteClick = { onDeleteClick() }
                    )
                }
            }
        }
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
                //hideKeyboard()
                findNavController().navigateUp()
            }
        )
    }

    private fun launchExtractedModelIntent(model: ExtractionModel) {
        try {
            when(model.type) {
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
                ExtractionModelType.OTHER -> Unit
            }
        } catch (e: ActivityNotFoundException) {

        }
    }
}