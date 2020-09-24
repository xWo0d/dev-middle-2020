package ru.skillbranch.skillarticles.extensions

import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop

fun View.setMarginOptionally(
    left: Int = marginLeft,
    top: Int = marginTop,
    right: Int = marginRight,
    bottom: Int = marginBottom
) {
    require(left >= 0 && top >= 0 && right >= 0 && bottom >= 0) { "Margins can'not be negative" }
    (layoutParams as ViewGroup.MarginLayoutParams).setMargins(left, top, right, bottom)
    requestLayout()
}

fun View.setPaddingOptionally(
    left: Int = paddingLeft,
    top: Int = paddingTop,
    right: Int = paddingRight,
    bottom: Int = paddingBottom
) {
    require(left >= 0 && top >= 0 && right >= 0 && bottom >= 0) { "Margins can'not be negative" }
    setPadding(left, top, right, bottom)
    requestLayout()
}