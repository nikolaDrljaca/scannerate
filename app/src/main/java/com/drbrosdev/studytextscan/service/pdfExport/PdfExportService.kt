package com.drbrosdev.studytextscan.service.pdfExport

import android.content.Context

interface PdfExportService {
    fun printDocument(context: Context, text: String)
}