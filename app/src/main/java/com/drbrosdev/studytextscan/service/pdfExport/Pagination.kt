package com.drbrosdev.studytextscan.service.pdfExport

import android.text.Layout
import android.text.StaticLayout

import android.text.TextPaint

class Pagination(
    private var mIncludePad: Boolean = false,
    private var mWidth: Int = 0,
    private var mHeight: Int = 0,
    private var mSpacingMultiple: Float = 0f,
    private var mSpacingAdd: Float = 0f,
    private var mText: CharSequence? = null,
    private var mPaint: TextPaint? = null,
    private var mPages: MutableList<CharSequence>? = null,
) {

    init {
        layout()
    }

    private fun layout() {
        val layout = StaticLayout(
            mText,
            mPaint,
            mWidth,
            Layout.Alignment.ALIGN_NORMAL,
            mSpacingMultiple,
            mSpacingAdd,
            mIncludePad
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
                height = layout.getLineTop(i) + mHeight
            }
            if (i == lines - 1) {
                // Put the rest of the text into the last page
                addPage(text.subSequence(startOffset, layout.getLineEnd(i)))
                return
            }
        }
    }

    private fun addPage(text: CharSequence) {
        mPages!!.add(text)
    }

    fun size(): Int {
        return mPages!!.size
    }

    operator fun get(index: Int): CharSequence? {
        return if (index >= 0 && index < mPages!!.size) mPages!![index] else null
    }
}