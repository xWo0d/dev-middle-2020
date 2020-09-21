package ru.skillbranch.skillarticles.extensions

import android.text.Layout


/**
 * Returns line height of the line in px
 */
fun Layout.getLineHeight(line: Int): Int = getLineTop(line.inc()) - getLineTop(line)

/**
 * Returns the top of the Layout after removing the extra padding applied by the layout
 */
fun Layout.getLineTopWithoutPadding(line: Int): Int {
    var lineTop = getLineTop(line)
    if (line == 0) lineTop -= topPadding
    return lineTop
}

/**
 * Returns the bottom of the Layout after removing the extra padding applied by the layout
 */
fun Layout.getLineBottomWithoutPadding(line: Int): Int {
    var lineBottom = getLineBottomWithoutSpacing(line)
    if (line == lineCount.dec()) lineBottom -= bottomPadding
    return lineBottom
}

private fun Layout.getLineBottomWithoutSpacing(line: Int): Int {
    val lineBottom = getLineBottom(line)
    val isLastLine = line == lineCount.dec()
    val hasLineSpacing = spacingAdd != 0f

    return if (!hasLineSpacing && isLastLine) {
        lineBottom + spacingAdd.toInt()
    } else {
        lineBottom - spacingAdd.toInt()
    }
}
