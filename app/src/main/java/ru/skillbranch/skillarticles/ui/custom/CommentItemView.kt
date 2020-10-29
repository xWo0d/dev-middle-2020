package ru.skillbranch.skillarticles.ui.custom

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.models.CommentItemData
import ru.skillbranch.skillarticles.extensions.*
import kotlin.math.min

class CommentItemView(context: Context) : ViewGroup(context, null, 0) {

    private val defaultVSpase = context.dpToIntPx(8)
    private val defaultHSpase = context.dpToIntPx(16)
    private val avatarSize = context.dpToIntPx(40)
    private val lineSize = context.dpToPx(2)
    private val iconSize = context.dpToIntPx(12)

    private val tv_date: TextView
    private val tv_author: TextView
    private val tv_body: TextView
    private val tv_answer_to: TextView
    private val iv_avatar: ImageView
    private val iv_answer_icon: ImageView

    private val grayColor = context.getColor(R.color.color_gray)
    private val primaryColor = context.attrValue(R.attr.colorPrimary)
    private val dividerColor = context.getColor(R.color.color_divider)
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = dividerColor
        strokeWidth = lineSize
        style = Paint.Style.STROKE
    }

    init {
        setPadding(defaultHSpase, defaultVSpase, defaultHSpase, defaultVSpase)
        tv_date = TextView(context).apply {
            setTextColor(grayColor)
            textSize = 12f
        }
        addView(tv_date)

        iv_avatar = ImageView(context)
        addView(iv_avatar)

        tv_author = TextView(context).apply {
            setTextColor(primaryColor)
            textSize = 14f
            setTypeface(typeface, Typeface.BOLD)
        }
        addView(tv_author)

        tv_body = TextView(context).apply {
            setTextColor(grayColor)
            textSize = 14f
        }
        addView(tv_body)

        tv_answer_to = TextView(context).apply {
            setTextColor(grayColor)
            textSize = 12f
            isVisible = false
        }
        addView(tv_answer_to)

        iv_answer_icon = ImageView(context).apply {
            setImageResource(R.drawable.ic_reply_black_24dp)
            imageTintList = ColorStateList.valueOf(grayColor)
            isVisible = false
        }
        addView(iv_answer_icon)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var usedHeight = paddingTop
        val width = View.getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)

        measureChild(tv_answer_to, widthMeasureSpec, heightMeasureSpec)
        if (tv_answer_to.isVisible) usedHeight += tv_answer_to.measuredHeight

        tv_date.minWidth = avatarSize
        measureChild(tv_date, widthMeasureSpec, heightMeasureSpec)

        tv_author.width =
            width - paddingLeft - paddingRight - avatarSize - defaultHSpase - tv_date.measuredWidth
        measureChild(tv_author, widthMeasureSpec, heightMeasureSpec)

        usedHeight += avatarSize + defaultVSpase
        tv_body.width = width - paddingLeft - paddingRight
        measureChild(tv_body, widthMeasureSpec, heightMeasureSpec)

        usedHeight += tv_body.measuredHeight + defaultVSpase
        setMeasuredDimension(width, usedHeight)

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var usedHeight = paddingTop
        val left = paddingLeft

        if (tv_answer_to.isVisible) {
            val lb = left + avatarSize / 2
            tv_answer_to.layout(
                lb,
                usedHeight,
                lb + tv_answer_to.measuredWidth,
                usedHeight + tv_answer_to.measuredHeight
            )

            val diff = (tv_answer_to.measuredHeight - iconSize) / 2
            iv_answer_icon.layout(
                tv_answer_to.right + defaultHSpase / 2,
                usedHeight + diff,
                tv_answer_to.right + defaultHSpase / 2 + iconSize,
                usedHeight + iconSize + diff
            )
            usedHeight += tv_answer_to.measuredHeight
        }

        val diffHeader = (avatarSize - tv_author.measuredHeight) / 2
        val diffDate = (avatarSize - tv_date.measuredHeight) / 2

        iv_avatar.layout(
            left,
            usedHeight,
            left + avatarSize,
            usedHeight + avatarSize
        )

        tv_author.layout(
            iv_avatar.right + defaultHSpase / 2,
            usedHeight + diffHeader,
            iv_avatar.right + defaultHSpase / 2 + tv_author.measuredWidth,
            usedHeight + tv_author.measuredHeight + diffHeader
        )

        tv_date.layout(
            tv_author.right + defaultHSpase / 2,
            usedHeight + diffDate,
            tv_author.right + defaultHSpase / 2 + tv_date.measuredWidth,
            usedHeight + tv_date.measuredHeight + diffDate
        )

        usedHeight += avatarSize + defaultVSpase

        tv_body.layout(
            left,
            usedHeight,
            left + tv_body.measuredWidth,
            usedHeight + tv_body.measuredHeight
        )
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        val level = min(paddingLeft / defaultHSpase, 5)

        if (level == 1) return
        for (i in 1 until level) {
            canvas.drawLine(
                i.toFloat() * defaultHSpase,
                0f,
                i.toFloat() * defaultHSpase,
                canvas.height.toFloat(),
                linePaint
            )
        }
    }

    fun bind(item: CommentItemData?) {
        if (item == null) {
            //TODO show shimmer
        } else {
            val level = min(item.slug.split("/").size.dec(), 5)
            setPaddingOptionally(left = level * defaultHSpase)

            Glide.with(context)
                .load(item.user.avatar)
                .apply(RequestOptions.circleCropTransform())
                .override(avatarSize)
                .into(iv_avatar)

            tv_author.text = item.user.name
            tv_date.text = item.date.format() //TODO use extension .humanizeDiff() instead format()
            tv_body.text = item.body
            tv_answer_to.text = item.answerTo
            tv_answer_to.isVisible = item.answerTo != null
            iv_answer_icon.isVisible = item.answerTo != null
        }
    }
}