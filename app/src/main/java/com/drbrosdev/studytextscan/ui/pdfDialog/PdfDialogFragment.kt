package com.drbrosdev.studytextscan.ui.pdfDialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.persistence.entity.Scan
import com.drbrosdev.studytextscan.service.pdfExport.PdfExportServiceImpl
import com.drbrosdev.studytextscan.ui.support.theme.ScannerateTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
class PdfDialogFragment : DialogFragment() {

    private val viewModel: PdfDialogViewModel by viewModel()
    private val pdfExportService: PdfExportServiceImpl by inject()

    private val fontSizeOptionsList = listOf(
        "8",
        "9",
        "10",
        "11",
        "12",
        "13",
        "14",
        "15",
        "16",
        "17",
        "18",
        "20",
        "22",
        "24",
        "26",
        "28",
        "30",
        "32",
        "36",
        "40",
        "44"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val colorOptionsList = listOf(
            getString(R.string.black_color),
            getString(R.string.blue_color),
            getString(R.string.red_color),
            getString(R.string.green_color),
            getString(R.string.yellow_color)
        )

        return ComposeView(requireContext()).apply {
            transitionName = "pdf_dialog_frag"
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                ScannerateTheme {
                    val currentScan by viewModel.scan.collectAsStateWithLifecycle()

                    PdfDialog(
                        colorList = colorOptionsList,
                        fontSizeList = fontSizeOptionsList,
                        onCancelClick = { dismiss() },
                        onExportClick = { color, fontSize ->
                            createExport(currentScan, color, fontSize)
                            lifecycleScope.launch {
                                delay(500)
                                dismiss()
                            }
                        }
                    )
                }
            }
        }
    }

    private fun createExport(scan: Scan?, color: String, fontSize: String) = scan?.let {
        pdfExportService.printDocument(
            requireContext(),
            it.scanTitle,
            listOf(
                it
            ),
            determineColor(color),
            fontSize.toInt()
        )
    }

    private fun determineColor(colorString: String): Int {
        when (colorString) {
            getString(R.string.black_color) -> return Color.BLACK
            getString(R.string.blue_color) -> return Color.BLUE
            getString(R.string.red_color) -> return Color.GREEN
            getString(R.string.green_color) -> return Color.RED
            getString(R.string.yellow_color) -> return Color.YELLOW
        }
        return Color.BLACK
    }
}