package com.drbrosdev.studytextscan.service.pdfExport

import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.Log

class Pagination(
    private var mIncludePad: Boolean = false,
    private var mWidth: Int = 0,
    private var mHeight: Int = 0,
    private var mSpacingMultiple: Float = 1f,
    private var mSpacingAdd: Float = 0F,
    private var mText: CharSequence? = null,
    private var mPaint: TextPaint? = null,
    private var mPages: MutableList<CharSequence>? = null,
    private var fontSize: Float
) {

    init {
        layout()
    }

    private fun layout() {

        mPaint!!.textSize = fontSize
        val layout = StaticLayout(
            mText!!,
            0,
            mText!!.length,
            mPaint!!,
            mWidth,
            Layout.Alignment.ALIGN_NORMAL,
            mSpacingMultiple,
            mSpacingAdd,
            mIncludePad,
            TextUtils.TruncateAt.START,
            0
        )

        val lines = layout.lineCount
        val text = layout.text
        var startOffset = 0
        var height = mHeight
        for (i in 0 until lines) {
            if (height < layout.getLineBottom(i)) {
                // When the layout height has been exceeded
                addPage(text.subSequence(startOffset, layout.getLineStart(i)))
                startOffset = layout.getLineStart(i)
                height =
                    layout.getLineTop(i) + mHeight - 35 // remove from height the new line where title fits
            }
            if (i == lines - 1) {
                // Put the rest of the text into the last page
                addPage(text.subSequence(startOffset, layout.getLineEnd(i)))
                Log.d("pagination_finished", "Pagination finished! Number of pages: ${getNumberOfPages()}")
                return
            }
        }
    }

    private fun addPage(text: CharSequence) {
        mPages!!.add(text)
    }

    fun getNumberOfPages(): Int {
        return mPages!!.size
    }

    operator fun get(index: Int): CharSequence? {
        return if (index >= 0 && index < mPages!!.size) mPages!![index] else null
    }
}