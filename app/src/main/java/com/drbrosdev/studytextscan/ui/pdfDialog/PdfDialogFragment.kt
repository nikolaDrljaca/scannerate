package com.drbrosdev.studytextscan.ui.pdfDialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.databinding.FragmentPdfDialogBinding
import com.drbrosdev.studytextscan.service.pdfExport.PdfExportServiceImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class PdfDialogFragment : DialogFragment(R.layout.fragment_pdf_dialog) {

    private val pdfDialogViewModel: PdfDialogViewModel by stateViewModel(state = { requireArguments() })
    private val pdfExportService: PdfExportServiceImpl by inject()

    /*
    Text fields work as exposed menus when clicked. These are the options to be displayed in the
    dropdown menu.
     */
    private val colorOptionsList = listOf(
        getString(R.string.black_color),
        getString(R.string.blue_color),
        getString(R.string.red_color),
        getString(R.string.green_color),
        getString(R.string.yellow_color)
    )
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
    private val defaultColor = "Black"
    private val defaultFontSize = "12"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentPdfDialogBinding.bind(view)

        /*
        Transparent background for the dialog so that the rounded corners can show.
         */
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.apply {
            val colorOptionsAdapter =
                ArrayAdapter(requireContext(), R.layout.list_item, colorOptionsList)
            val fontOptionsAdapter =
                ArrayAdapter(requireContext(), R.layout.list_item, fontSizeOptionsList)

            colorMenu.editText?.setText(defaultColor)
            fontSizeMenu.editText?.setText(defaultFontSize)

            (fontSizeMenu.editText as? AutoCompleteTextView)?.setAdapter(fontOptionsAdapter)
            (colorMenu.editText as? AutoCompleteTextView)?.setAdapter(colorOptionsAdapter)

            buttonCancel.setOnClickListener { dismiss() }


            buttonExport.setOnClickListener {
                /*
                Grab current values of text fields and perform export.
                Scan comes from viewModel.
                 */
                val currentColor = colorMenu.editText?.text?.toString() ?: ""
                val currentFontSize = fontSizeMenu.editText?.text?.toString() ?: ""

                pdfDialogViewModel.getScan {
                    it?.let {
                        pdfExportService.printDocument(
                            requireContext(),
                            it.scanTitle,
                            listOf(
                                it
                            ),
                            determineColor(currentColor),
                            currentFontSize.toInt()
                        )
                    }
                }
                /*
                Hacky solution to dismiss the dialog after export is clicked.
                Probably not a good solution, might cause crashes since its could modify a fragment
                that has gone through onDestroy
                 */
                lifecycleScope.launch {
                    delay(500)
                    dismiss()
                }
            }
        }
    }

    private fun determineColor(colorString: String): Int {
        when (colorString) {
            "Black" -> return Color.BLACK
            "Blue" -> return Color.BLUE
            "Green" -> return Color.GREEN
            "Red" -> return Color.RED
            "Yellow" -> return Color.YELLOW
        }
        return Color.BLACK
    }
}