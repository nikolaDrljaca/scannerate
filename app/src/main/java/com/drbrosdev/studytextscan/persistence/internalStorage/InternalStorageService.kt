package com.drbrosdev.studytextscan.persistence.internalStorage

import android.content.Context
import com.drbrosdev.studytextscan.persistence.entity.PdfFile

interface InternalStorageService {

    fun savePdfToInternalStorage(pdfFile: PdfFile, context: Context): Boolean

}