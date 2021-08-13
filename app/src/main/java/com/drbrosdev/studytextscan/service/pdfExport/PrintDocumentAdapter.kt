package com.drbrosdev.studytextscan.service.pdfExport

import android.content.Context
import android.graphics.Canvas
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
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.Log
import androidx.core.graphics.withTranslation
import java.io.FileOutputStream
import java.io.IOException

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

    private lateinit var myPdfDocument: PdfDocument
    private lateinit var pagination: Pagination

    private var pageHeight: Int = 0
    private var pageWidth: Int = 0
    private var totalPages = 0

    private val titleFontSize = 40f
    private val rightOffset = 70
    private val titleBaseLine = 72
    private val leftMargin = 42
    private val newLine = 35
    private val spacingAdd = 0F
    private val spacingMulti = 1F

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

        val mutableTextArray = mutableListOf<String>()
        text.forEach { mutableTextArray.add("$it") }
        var textToPrint = ""
        mutableTextArray.forEach {
            textToPrint += it
        }

        pagination = Pagination(
            false,
            pageWidth - rightOffset,
            pageHeight - titleBaseLine - newLine,
            1F,
            0F,
            textToPrint,
            TextPaint(Paint.ANTI_ALIAS_FLAG),
            mutableListOf(),
            fontSize.toFloat()
        )
        Log.d("pagination", "Pagination created!")

        totalPages = pagination.getNumberOfPages()
        Log.d("total_pages", "Total pages: $totalPages")

        if (totalPages > 0) {
            val builder =
                PrintDocumentInfo.Builder("${titleOfDocument.replace(" ", "_").lowercase()}.pdf")
                    .setContentType(
                        PrintDocumentInfo.CONTENT_TYPE_DOCUMENT
                    )
                    .setPageCount(totalPages)

            val info = builder.build()
            callback.onLayoutFinished(info, true)
        } else {
            Log.e("on_layout", "Page count is zero.")
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
                    pageHeight,
                    i
                ).create()

                val page = myPdfDocument.startPage(newPage)

                if (cancellationSignal.isCanceled) {
                    callback.onWriteCancelled()
                    myPdfDocument.close()
                    return
                }
                page?.let {
                    drawPage(it, i)
                }
                myPdfDocument.finishPage(page)

            }
        }

        try {
            myPdfDocument.writeTo(
                FileOutputStream(
                    destination.fileDescriptor
                )
            )
        } catch (e: IOException) {
            callback.onWriteFailed(e.toString())
            return
        } finally {
            myPdfDocument.close()
        }

        callback.onWriteFinished(pageRanges)
    }

    private fun drawPage(
        page: PdfDocument.Page,
        pageNumber: Int
    ) {
        Log.d("draw_page", "Writing page number: $pageNumber")

        val canvas = page.canvas

        val width = pageWidth - rightOffset

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

        canvas.drawMultilineText(
            TextPaint(paint),
            leftMargin.toFloat(),
            (titleBaseLine + newLine).toFloat(),
            width,
            pageNumber
        )
    }

    private fun pageInRange(pageRanges: Array<PageRange>, page: Int): Boolean {
        for (i in pageRanges.indices) {
            if (page >= pageRanges[i].start && page <= pageRanges[i].end)
                return true
        }
        return false
    }

    private fun Canvas.drawMultilineText(
        textPaint: TextPaint,
        x: Float,
        y: Float,
        width: Int,
        page: Int
    ) {

        Log.d("draw_multiline_text", "Printing page: $page!")
        val staticLayout = update(
            page,
            textPaint,
            width
        )

        staticLayout.draw(this, x, y)
    }

    private fun update(index: Int, textPaint: TextPaint, width: Int): StaticLayout {
        val text: CharSequence? = pagination[index]
        Log.d("update", "Text for printing: $text")
        return StaticLayout(
            text!!,
            0,
            text.length,
            textPaint,
            width,
            Layout.Alignment.ALIGN_NORMAL,
            spacingMulti,
            spacingAdd,
            false,
            TextUtils.TruncateAt.START,
            0
        )
    }

    private fun StaticLayout.draw(canvas: Canvas, x: Float, y: Float) {
        canvas.withTranslation(x, y) {
            draw(this)
        }
    }
}