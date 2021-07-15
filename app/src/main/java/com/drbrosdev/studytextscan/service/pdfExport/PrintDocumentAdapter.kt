package com.drbrosdev.studytextscan.service.pdfExport

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.pdf.PrintedPdfDocument
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.ceil

/**
 * Custom Print Adapter
 */
class MyPrintDocumentAdapter(
    private var context: Context,
    private var text: String
) : PrintDocumentAdapter() {

    private var pageHeight: Int = 0
    private var pageWidth: Int = 0
    private var myPdfDocument: PdfDocument? = null
    private var totalPages =
        4 // Only for testing -> later we will have to determine the number of pages based on text length

    override fun onStart() {
        super.onStart()
        // any operation before printing
    }

    override fun onLayout(
        oldAttributes: PrintAttributes,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal,
        callback: LayoutResultCallback,
        metadata: Bundle
    ) {
        myPdfDocument = PrintedPdfDocument(context, newAttributes)

        val height = newAttributes.mediaSize?.heightMils
        val width = newAttributes.mediaSize?.heightMils

        height?.let {
            pageHeight = it / 1000 * 72
        }

        width?.let {
            pageWidth = it / 1000 * 72
        }

        if (cancellationSignal.isCanceled) {
            callback.onLayoutCancelled()
            return
        }

        totalPages = computePageCount(newAttributes)

        if (totalPages > 0) {
            val builder =
                PrintDocumentInfo.Builder("print_output.pdf").setContentType(
                    PrintDocumentInfo.CONTENT_TYPE_DOCUMENT
                )
                    .setPageCount(totalPages)

            val info = builder.build()
            callback.onLayoutFinished(info, true)
        } else {
            callback.onLayoutFailed("Page count is zero.")
        }
    }

    override fun onWrite(
        pageRanges: Array<PageRange>,
        destination: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal,
        callback: WriteResultCallback
    ) {
        for (i in 0 until totalPages) {
            if (pageInRange(pageRanges, i)) {
                val newPage = PdfDocument.PageInfo.Builder(
                    pageWidth,
                    pageHeight, i
                ).create()

                val page = myPdfDocument?.startPage(newPage)

                if (cancellationSignal.isCanceled) {
                    callback.onWriteCancelled()
                    myPdfDocument?.close()
                    myPdfDocument = null
                    return
                }
                page?.let {
                    drawPage(it, i)
                }
                myPdfDocument?.finishPage(page)
            }
        }

        try {
            myPdfDocument?.writeTo(
                FileOutputStream(
                    destination.fileDescriptor
                )
            )
        } catch (e: IOException) {
            callback.onWriteFailed(e.toString())
            return
        } finally {
            myPdfDocument?.close()
            myPdfDocument = null
        }

        callback.onWriteFinished(pageRanges)
    }


    private fun drawPage(
        page: PdfDocument.Page,
        pageNumber: Int
    ) {
        var pagenum = pageNumber
        val canvas = page.canvas

        pagenum++ // Make sure page numbers start at 1

        val titleBaseLine = 72
        val leftMargin = 54

        val paint = Paint()
        paint.color = Color.BLACK
        paint.textSize = 40f
        canvas.drawText(
            "Test Print Document Page $pagenum",
            leftMargin.toFloat(),
            titleBaseLine.toFloat(),
            paint
        )

        paint.textSize = 16f
        var newLine = 35
        computeTextForPrinting(text).forEach {
            canvas.drawText(
                it,
                leftMargin.toFloat(),
                (titleBaseLine + newLine).toFloat(),
                paint
            )
            newLine += 20
        }
    }

    override fun onFinish() {
        super.onFinish()
        // clean up functionality
    }

    private fun computePageCount(printAttributes: PrintAttributes): Int {
        var itemsPerPage = 4 // default item count for portrait mode

        val pageSize = printAttributes.mediaSize
        if (pageSize != null && !pageSize.isPortrait) {
            // Six items per page in landscape orientation
            itemsPerPage = 6
        }

        // Determine number of print items
        val printItemCount = 1 // one text for now

        return ceil((printItemCount / itemsPerPage.toDouble())).toInt()
    }

    private fun pageInRange(pageRanges: Array<PageRange>, page: Int): Boolean {
        for (i in pageRanges.indices) {
            if (page >= pageRanges[i].start && page <= pageRanges[i].end)
                return true
        }
        return false
    }

    /**
     * Compute text for print.
     * Explanation will be made!! @nikola -> see!
     * @author Ognjen Drljaca
     */
    private fun computeTextForPrinting(text: String): List<String> {
        val stringBuilder = StringBuilder()
        val arrayList = ArrayList<String>()
        val textArray = text.toCharArray()
        val textArraySize = textArray.size
        var index2 = 0

        textArray.forEachIndexed forEach@{ index, c ->
            if (checkIfCharIsSpace(c) && index2 == 0) return@forEach
            stringBuilder.append(c)
            if (index2 == 90) {
                if (!checkIfCharIsSign(c)) {
                    stringBuilder.append('-')
                }
                val textToPrint = stringBuilder.toString()

                arrayList.add(textToPrint)
                stringBuilder.clear()
                index2 = 0
            } else {
                index2++
            }
            if (index == textArraySize - 1) {
                arrayList.add(stringBuilder.toString())
            }
        }
        return arrayList
    }

    private fun checkIfCharIsSpace(char: Char): Boolean {
        if (char == ' ') return true
        return false
    }

    private fun checkIfCharIsSign(c: Char): Boolean {
        if (c == '!' || c == ',' || c == '.') return true
        return false
    }
}