package com.drbrosdev.studytextscan.service.pdfExport

import android.content.Context
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
import android.util.Log
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.ceil

/**
 * Custom Print Adapter
 */
class MyPrintDocumentAdapter(
    private var context: Context,
    private var titleOfDocument: String,
    private var text: List<String>,
    private val color: Int,
    private val fontSize: Int
) : PrintDocumentAdapter() {

    private var pageHeight: Int = 0
    private var pageWidth: Int = 0
    private var myPdfDocument: PdfDocument? = null
    private var totalPages = 4 // default
    private val titleFontSize = 40f

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
            pageHeight = it / 1000 * 79
        }

        width?.let {
            pageWidth = it / 1000 * 72
        }

        Log.d("page_dimensions", "Page has width: $pageWidth and height: $pageHeight")

        if (cancellationSignal.isCanceled) {
            callback.onLayoutCancelled()
            return
        }

        totalPages = computePageCount(newAttributes, text)
        Log.d("total_pages", "Total pages: $totalPages")

        if (totalPages > 0) {
            val builder =
                PrintDocumentInfo.Builder("print_output.pdf")
                    .setContentType(
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
        val textArray = computeTextForPrinting(text)
        for (i in 0 until totalPages) {
            if (pageInRange(pageRanges, i)) {
                val newPage = PdfDocument.PageInfo.Builder(
                    pageWidth,
                    pageHeight,
                    i
                ).create()

                val page = myPdfDocument?.startPage(newPage)

                if (cancellationSignal.isCanceled) {
                    callback.onWriteCancelled()
                    myPdfDocument?.close()
                    myPdfDocument = null
                    return
                }
                page?.let {
                    drawPage(it, i, textArray)
                }
                myPdfDocument?.finishPage(page)

                for (i2 in 0 until 35) { // remove texts that were printed TODO still not optimized
                    if (textArray.isNotEmpty()) {
                        textArray.removeAt(0)
                    }
                }
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
        pageNumber: Int,
        textArrayToBePrinted: List<String>
    ) {
        var pageNum = pageNumber
        val canvas = page.canvas

        pageNum++ // Make sure page numbers start at 1

        val titleBaseLine = 72
        val leftMargin = 54

        val paint = Paint()
        paint.color = color
        paint.textSize = titleFontSize // we go with default title font size
        canvas.drawText(
            titleOfDocument,
            leftMargin.toFloat(),
            titleBaseLine.toFloat(),
            paint
        )

        paint.textSize = fontSize.toFloat()
        var newLine = 35

        textArrayToBePrinted.forEach {
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

    private fun computePageCount(printAttributes: PrintAttributes, text: List<String>): Int {
        var itemsPerPage = 4 // default item count for portrait mode

        val pageSize = printAttributes.mediaSize
        if (pageSize != null && !pageSize.isPortrait) {
            // Six items per page in landscape orientation
            itemsPerPage = 6
        }

        // Determine number of print items
        val printItemCount =
            computePrintItemCount(computeTextForPrinting(text).size) // one text for now

        return ceil((printItemCount / itemsPerPage.toDouble())).toInt()
    }

    private fun computePrintItemCount(size: Int): Int {
        val numberOfPrintItemsPerPage = (size.toFloat() / 38)
        val comparingInteger = size / 38
        val checkNewPage = numberOfPrintItemsPerPage - comparingInteger.toFloat()
        var numberToReturn = size / 38
        if (checkNewPage > 0.50001) {
            numberToReturn += 1
        }

        var printItemCount = 0
        while (numberToReturn > 0) {
            printItemCount += 4
            numberToReturn--
        }
        Log.d("print_item_count", "Print item count: $printItemCount")
        return printItemCount
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
     * First tested version of algorithm.
     * Takes the text and and makes sure it is computed in multiple lines
     * For every new row adds a '-' if it is not a sign of some sort(buggy)
     * Algorithm needs to be extended and improved upon further testing -> @drljacan
     */
    private fun computeTextForPrinting(strings: List<String>): MutableList<String> {
        var text = createTextForPrinting(strings)

        val stringBuilder = StringBuilder()
        val arrayListToReturn = mutableListOf<String>()
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

                arrayListToReturn.add(textToPrint)
                stringBuilder.clear()
                index2 = 0
            } else {
                index2++
            }
            if (index == textArraySize - 1) {
                arrayListToReturn.add(stringBuilder.toString())
            }
        }
        return arrayListToReturn
    }

    private fun createTextForPrinting(strings: List<String>): String {
        var text = ""
        strings.forEachIndexed { index, it ->
            text += "$index) $it"
        }
        return text
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