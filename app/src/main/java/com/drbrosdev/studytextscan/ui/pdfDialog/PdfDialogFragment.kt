package com.drbrosdev.studytextscan.ui.pdfDialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.databinding.FragmentPdfDialogBinding
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class PdfDialogFragment : DialogFragment(R.layout.fragment_pdf_dialog) {

    private val pdfDialogViewModel: PdfDialogViewModel by stateViewModel(state = { requireArguments() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentPdfDialogBinding.bind(view)

        binding.apply {
            tvXd.text = "Alo bato demonizovan"
        }

    }
}