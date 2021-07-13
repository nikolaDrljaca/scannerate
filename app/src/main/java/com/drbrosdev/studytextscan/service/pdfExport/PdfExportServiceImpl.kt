package com.drbrosdev.studytextscan.service.pdfExport

import android.content.Context
import android.print.PrintManager
import com.drbrosdev.studytextscan.R

class PdfExportServiceImpl : PdfExportService {

    override fun printDocument(
        context: Context,
        text: String
    ) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager

        val jobName = context.getString(R.string.app_name) + " Document"

        printManager.print(jobName, MyPrintDocumentAdapter(context, text), null)
    }
}