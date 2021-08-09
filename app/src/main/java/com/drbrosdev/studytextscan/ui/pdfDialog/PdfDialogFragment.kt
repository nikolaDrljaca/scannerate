package com.drbrosdev.studytextscan.ui.pdfDialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.DialogFragment
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.databinding.FragmentPdfDialogBinding
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class PdfDialogFragment : DialogFragment(R.layout.fragment_pdf_dialog) {

    private val pdfDialogViewModel: PdfDialogViewModel by stateViewModel(state = { requireArguments() })

    /*
    Text fields work as exposed menus when clicked. These are the options to be displayed in the
    dropdown menu.
     */
    private val colorOptionsList = listOf("Black", "Blue", "Red", "Green")
    private val fontSizeOptionsList = listOf("12", "14", "16", "18")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentPdfDialogBinding.bind(view)

        /*
        Transparent background for the dialog so that the rounded corners can show.
         */
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.apply {
            val colorOptionsAdapter = ArrayAdapter(requireContext(), R.layout.list_item, colorOptionsList)
            val fontOptionsAdapter = ArrayAdapter(requireContext(), R.layout.list_item, fontSizeOptionsList)

            (fontSizeMenu.editText as? AutoCompleteTextView)?.setAdapter(fontOptionsAdapter)
            (colorMenu.editText as? AutoCompleteTextView)?.setAdapter(colorOptionsAdapter)

            buttonCancel.setOnClickListener { dismiss() }

            buttonExport.setOnClickListener {
                /*
                Grab current values of text fields and perform export.
                Scan comes from viewModel.
                 */
                val currentColor = colorMenu.editText?.text?.toString()
                val currentFont = fontSizeMenu.editText?.text?.toString()
            }
        }
    }
}