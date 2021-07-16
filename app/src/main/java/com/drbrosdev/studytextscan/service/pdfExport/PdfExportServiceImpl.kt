package com.drbrosdev.studytextscan.service.pdfExport

import android.content.Context
import android.print.PrintManager
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.persistence.entity.Scan

class PdfExportServiceImpl : PdfExportService {

    override fun printDocument(context: Context, scans: List<Scan>) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager

        val jobName = context.getString(R.string.app_name) + " Document"

        val text = mutableListOf<String>()
        scans.forEach {
            text.add(it.scanText)
        }

        printManager.print(jobName, MyPrintDocumentAdapter(context, text), null)
    }
}