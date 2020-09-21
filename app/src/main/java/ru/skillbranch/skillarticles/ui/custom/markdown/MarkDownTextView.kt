package ru.skillbranch.skillarticles.ui.custom.markdown

import android.content.Context
import android.graphics.Canvas
import android.text.Spanned
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.graphics.withTranslation

class MarkdownTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): TextView(context, attrs, defStyleAttr) {

    private val searchBgHelper = SearchBgHelper(context) {
        TODO("not implemented")
    }

    override fun onDraw(canvas: Canvas?) {
        if (text is Spanned) {
            canvas?.withTranslation(totalPaddingLeft.toFloat(), totalPaddingRight.toFloat()) {
                searchBgHelper.draw(canvas, text as Spanned, layout)
            }
        }
        super.onDraw(canvas)
    }
}