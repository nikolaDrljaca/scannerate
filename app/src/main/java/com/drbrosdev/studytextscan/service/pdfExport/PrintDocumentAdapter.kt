package com.drbrosdev.studytextscan.service.pdfExport

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.pdf.PrintedPdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextDirectionHeuristics
import android.text.TextPaint
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.graphics.withTranslation
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
    private var pagination: Pagination? = null

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
            pageHeight = it / 1000 * 89
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

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onWrite(
        pageRanges: Array<PageRange>,
        destination: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal,
        callback: WriteResultCallback
    ) {
        val textArray2 = mutableListOf<String>()
        text.forEachIndexed { index, it -> textArray2.add("$index) $it") }

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
                    drawPage(it, textArray2, i)
                }
                myPdfDocument?.finishPage(page)

                for (i2 in 0 until 39) { // Works, not well tested!
                    if (textArray2.isNotEmpty()) {
                        textArray2.removeAt(0)
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


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun drawPage(
        page: PdfDocument.Page,
        textArrayToBePrinted: List<String>,
        pageNumber: Int
    ) {
        val canvas = page.canvas

        val titleBaseLine = 72
        val leftMargin = 42
        val rightOffset = 70
        val widht = pageWidth - rightOffset

        val paint = Paint()
        paint.color = color
        paint.textSize = titleFontSize

        canvas.drawText(
            titleOfDocument,
            leftMargin.toFloat(),
            titleBaseLine.toFloat(),
            paint
        )

        paint.textSize = fontSize.toFloat()
        val newLine = 35 // new line start

        var textToPrint = ""
        textArrayToBePrinted.forEach {
            textToPrint += it
        }

        canvas.drawMultilineText(
            textToPrint,
            TextPaint(Paint.ANTI_ALIAS_FLAG),
            leftMargin.toFloat(),
            (titleBaseLine + newLine).toFloat(),
            widht,
            pageNumber
        )
    }

    override fun onFinish() {
        super.onFinish()
        // clean up functionality
    }

    /**
     * Computes number of pages
     * @param text: text that needs to be printed
     * @return number of pages
     */
    private fun computePageCount(printAttributes: PrintAttributes, text: List<String>): Int {
        var itemsPerPage = 4 // default item count for portrait mode

        val pageSize = printAttributes.mediaSize
        if (pageSize != null && !pageSize.isPortrait) {
            // Six items per page in landscape orientation
            itemsPerPage = 6
        }

        // Determine number of print items
        val printItemCount = 8

        return ceil((printItemCount / itemsPerPage.toDouble())).toInt()
    }

    private fun pageInRange(pageRanges: Array<PageRange>, page: Int): Boolean {
        for (i in pageRanges.indices) {
            if (page >= pageRanges[i].start && page <= pageRanges[i].end)
                return true
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun Canvas.drawMultilineText(
        text: CharSequence,
        textPaint: TextPaint,
        x: Float,
        y: Float,
        width: Int,
        page: Int
    ) {

        pagination = Pagination(
            false,
            pageWidth,
            pageHeight,
            0f,
            0f,
            text,
            textPaint,
            mutableListOf()
        )

        val staticLayout = update(page,textPaint, width)

        staticLayout.draw(this, x, y)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun update(index: Int, textPaint: TextPaint, width: Int): StaticLayout {
        val text: CharSequence? = pagination!![index]
        return StaticLayout.Builder
            .obtain(text!!, 0, text.length, textPaint, width)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setTextDirection(TextDirectionHeuristics.LTR)
            .setLineSpacing(0F, 1F)
            .setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD)
            .setBreakStrategy(LineBreaker.BREAK_STRATEGY_SIMPLE)
            .setIncludePad(true)
            .setMaxLines(38)
            .build()
    }

    private fun StaticLayout.draw(canvas: Canvas, x: Float, y: Float) {
        canvas.withTranslation(x, y) {
            draw(this)
        }
    }


    /**
     * Determines print item count which later is used for
     * determining how much pages we need for our document
     * @param size: size of the text array that needs to be printed
     * @return number of print items per page
     */
//    private fun computePrintItemCount(size: Int): Int {
//        val numberOfPrintItemsPerPage = (size.toFloat() / 38)
//        val comparingInteger = size / 38
//        val checkNewPage = numberOfPrintItemsPerPage - comparingInteger.toFloat()
//        var numberToReturn = size / 38
//        if (checkNewPage > 0.50001) {
//            numberToReturn += 1
//        }
//
//        var printItemCount = 0
//        while (numberToReturn > 0) {
//            printItemCount += 4
//            numberToReturn--
//        }
//        Log.d("print_item_count", "Print item count: $printItemCount")
//        return printItemCount
//    }

    /**
     * Compute text for printing line by line, since canvas draws every text in one line.
     * First tested version of algorithm.
     * Takes the text and and makes sure it is computed in multiple lines
     * For every new row adds a '-' if it is not a sign of some sort(buggy)
     * Algorithm needs to be extended and improved upon further testing -> @drljacan
     * @param strings: list of strings for printing
     * @return text that needs to be printed line by line
     */
//    private fun computeTextForPrinting(strings: List<String>): MutableList<String> {
//        var text = createTextForPrinting(strings)
//
//        val stringBuilder = StringBuilder()
//        val arrayListToReturn = mutableListOf<String>()
//        val textArray = text.toCharArray()
//        val textArraySize = textArray.size
//
//        var index2 = 0
//
//        textArray.forEachIndexed forEach@{ index, c ->
//            if (checkIfCharIsSpace(c) && index2 == 0) return@forEach
//            stringBuilder.append(c)
//            if (index2 == 90) {
//                if (!checkIfCharIsSign(c)) {
//                    stringBuilder.append('-')
//                }
//                val textToPrint = stringBuilder.toString()
//
//                arrayListToReturn.add(textToPrint)
//                stringBuilder.clear()
//                index2 = 0
//            } else {
//                index2++
//            }
//            if (index == textArraySize - 1) {
//                arrayListToReturn.add(stringBuilder.toString())
//            }
//        }
//        return arrayListToReturn
//    }

//    private fun createTextForPrinting(strings: List<String>): String {
//        var text = ""
//        strings.forEachIndexed { index, it ->
//            text += "$index) $it"
//        }
//        return text
//    }
//
//    private fun checkIfCharIsSpace(char: Char): Boolean {
//        if (char == ' ') return true
//        return false
//    }
//
//    private fun checkIfCharIsSign(c: Char): Boolean {
//        if (c == '!' || c == ',' || c == '.') return true
//        return false
//    }
}