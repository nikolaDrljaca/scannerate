package com.drbrosdev.studytextscan.service.pdfExport

import android.content.Context
import com.drbrosdev.studytextscan.persistence.entity.Scan

interface PdfExportService {

    fun printDocument(
        context: Context,
        titleOfDocument: String,
        scans: List<Scan>,
        color: Int,
        fontSize: Int
    )
}