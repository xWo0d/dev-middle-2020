package ru.skillbranch.skillarticles.ui.custom.markdown

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.text.Spannable
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.core.graphics.withTranslation
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.attrValue

@SuppressLint("ViewConstructor")
class MarkdownTextView constructor(
    context: Context,
    fontSize: Float
): TextView(context, null, 0), IMarkdownView {

    override var fontSize: Float = fontSize
        set(value) {
            textSize = value
            field = value
        }

    override val spannableContent: Spannable
        get() = text as Spannable

    val color = context.attrValue(R.attr.colorOnBackground)

    private val searchBgHelper = SearchBgHelper(context) {
        TODO("not implemented")
    }

    init {
        setTextColor(color)
        textSize = fontSize
        movementMethod = LinkMovementMethod.getInstance()
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