package ru.skillbranch.skillarticles.ui.custom.markdown

import android.text.Spannable

interface IMarkdownView {
    var fontSize: Float
    val spannableContent: Spannable
}