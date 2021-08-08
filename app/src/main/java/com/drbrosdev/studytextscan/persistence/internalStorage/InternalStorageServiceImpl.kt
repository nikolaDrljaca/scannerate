package com.drbrosdev.studytextscan.persistence.internalStorage

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.drbrosdev.studytextscan.persistence.entity.PdfFile
import okio.IOException

class InternalStorageServiceImpl: InternalStorageService{

    override fun savePdfToInternalStorage(pdfFile: PdfFile, context: Context): Boolean {
        return try {
            context.openFileOutput(pdfFile.title, MODE_PRIVATE).use { stream ->
                true
            }
        }catch (e: IOException){
            e.printStackTrace()
            false
        }
    }
}