package ru.skillbranch.skillarticles.markdown

import java.util.regex.Pattern

object MarkdownParser {

    private val LINE_SEPARATOR = System.getProperty("line.separator") ?: "\n"

    // group regex
    private const val UNORDERED_LIST_ITEM_GROUP = "(^[*+-] .+$)"
    private const val HEADER_GROUP = "(^#{1,6} .+?$)"
    private const val QUOTE_GROUP = "(^> .+?$)"
    private const val ITALIC_GROUP = "((?<!\\*)\\*[^*].*?[^*]?\\*(?!\\*)|(?<!_)_[^_].*?[^_]?_(?!_))"
    private const val BOLD_GROUP = "((?<!\\*)\\*{2}[^*].*?[^*]?\\*{2}(?!\\*)|(?<!_)_{2}[^_].*?[^_]?_{2}(?!_))"
    private const val STRIKE_GROUP = "((?<!~)~{2}[^~].*?[^~]?~{2}(?!~))"
    private const val RULE_GROUP = "(^[_*-]{3}$)"
    private const val INLINE_GROUP = "((?<!`)`[^`\\s].*?[^`]?`(?!`))"
    private const val LINK_GROUP = "(\\[[^\\[\\]]*?]\\(.*?\\)|^\\[*?]\\(.*?\\))"
    private const val CODE_BLOCK_GROUP = "(^`{3}[\\s\\S]+?`{3}$)"
    private const val ORDERED_LIST_ITEM_GROUP = "(^\\d{1,2}\\.\\s.+?$)"

    // result regex
    private val MARKDOWN_GROUPS = "$UNORDERED_LIST_ITEM_GROUP|$HEADER_GROUP|$QUOTE_GROUP" +
            "|$ITALIC_GROUP|$BOLD_GROUP|$STRIKE_GROUP|$RULE_GROUP|$INLINE_GROUP|$LINK_GROUP" +
            "|$ORDERED_LIST_ITEM_GROUP|$CODE_BLOCK_GROUP"

    private val elementPattern by lazy { Pattern.compile(MARKDOWN_GROUPS, Pattern.MULTILINE) }

    /**
     * parse markdown text to elements
     */
    fun parse(text: String): MarkdownText {
        val elements = mutableListOf<Element>()
        elements.addAll(findElements(text))
        return MarkdownText(elements)
    }

    /**
     * clear markdown text to string without markdown characters
     */
    fun clear(string: String?): String? {
        return string
            ?.let(::findElements)
            ?.fold(StringBuilder()) { sb, e ->
                sb.append(when (e) {
                    is Element.OrderedListItem -> "${e.order} ${e.text}"
                    is Element.BlockCode -> {
                        when (e.type) {
                            Element.BlockCode.Type.SINGLE -> "```${e.text}```"
                            Element.BlockCode.Type.START -> "```${e.text}\n"
                            Element.BlockCode.Type.MIDDLE -> "${e.text}\n"
                            Element.BlockCode.Type.END -> "${e.text}```"
                        }
                    }
                    else -> {
                        if (e.elements.isNotEmpty()) clear(e.text.toString()) else e.text
                    }
                })
            }
            ?.toString()
    }

    /**
     * find markdown elements in markdown text
     */
    private fun findElements(string: CharSequence): List<Element> {
        val parents = mutableListOf<Element>()
        val matcher = elementPattern.matcher(string)
        var lastStartIndex = 0

        loop@ while (matcher.find(lastStartIndex)) {
            val startIndex = matcher.start()
            val endIndex = matcher.end()

            // if something is found, then everything before - TEXT
            if (lastStartIndex < startIndex) {
                parents.add(Element.Text(string.subSequence(lastStartIndex, startIndex)))
            }

            // found text
            var text: CharSequence

            // groups range for iterate by groups
            val groups = 1..11
            var group = -1
            for (gr in groups) {
                if (matcher.group(gr) != null) {
                    group = gr
                    break
                }
            }

            when (group) {
                // NOT FOUND -> BREAK
                -1 -> break@loop

                // UNORDERED LIST
                1 -> {
                    // text without "+. "
                    text = string.subSequence(startIndex.plus(2), endIndex)

                    // find inner elements
                    val subs = findElements(text)
                    val element = Element.UnorderedListItem(text, subs)
                    parents.add(element)

                    // next find start from position "endIndex" (last regex character)
                    lastStartIndex = endIndex
                }

                // HEADER
                2 -> {
                    val reg = "^#{1,6}".toRegex().find(string.subSequence(startIndex, endIndex))
                    val level = reg!!.value.length

                    // text without "{#} "
                    val text = string.subSequence(startIndex.plus(level.inc()), endIndex)

                    val element = Element.Header(level, text)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                // QUOTE
                3 -> {
                    // text without '> '
                    val text = string.subSequence(startIndex.plus(2), endIndex)
                    val subelements = findElements(text)

                    val element = Element.Quote(text, subelements)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                // ITALIC
                4 -> {
                    // text without '*{}*' (or '_{}_')
                    val text = string.subSequence(startIndex.inc(), endIndex.dec())
                    val subelements = findElements(text)

                    val element = Element.Italic(text, subelements)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                // BOLD
                5 -> {
                    // text without '**{}**' (or '__{}__')
                    val text = string.subSequence(startIndex.plus(2), endIndex.minus(2))
                    val subelements = findElements(text)

                    val element = Element.Bold(text, subelements)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                // STRIKE
                6 -> {
                    // text without '~~{}~~'
                    val text = string.subSequence(startIndex.plus(2), endIndex.minus(2))
                    val subelements = findElements(text)

                    val element = Element.Strike(text, subelements)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                // RULE
                7 -> {
                    // text without '***' (or --- or ___) insert empty character
                    val element = Element.Rule()
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                // INLINE CODE
                8 -> {
                    // text without '`{}`'
                    val text = string.subSequence(startIndex.inc(), endIndex.dec())
                    val subelements = findElements(text)

                    val element = Element.InlineCode(text, subelements)
                    parents.add(element)
                    lastStartIndex = endIndex
                }
                // LINK
                9 -> {
                    val text = string.subSequence(startIndex, endIndex)
                    val (title: String, link: String) = "\\[(.*)]\\((.*)\\)".toRegex()
                        .find(text)!!.destructured
                    val element = Element.Link(link, title)
                    parents.add(element)
                    lastStartIndex = endIndex
                }

                // ORDERED LIST
                10 -> {
                    val reg = "^(\\d{1,2}.)".toRegex().find(string.substring(startIndex, endIndex))
                    val order = reg!!.value
                    val text = string.subSequence(startIndex.plus(order.length.inc()), endIndex).toString()


                    // find inner elements
                    val subs = findElements(text)
                    val element = Element.OrderedListItem(order, text, subs)
                    parents.add(element)

                    lastStartIndex = endIndex
                }

                // CODE BLOCK
                11 -> {
                    val text = string.subSequence(startIndex.plus(3), endIndex.minus(3))

                    if (text.contains(LINE_SEPARATOR)) {
                        for ((index, line) in text.lines().withIndex()) {
                            when (index) {
                                text.lines().lastIndex -> parents.add(
                                    Element.BlockCode(
                                        Element.BlockCode.Type.END,
                                        line
                                    )
                                )
                                0 -> parents.add(
                                    Element.BlockCode(
                                        Element.BlockCode.Type.START,
                                        line + LINE_SEPARATOR
                                    )
                                )
                                else -> parents.add(
                                    Element.BlockCode(
                                        Element.BlockCode.Type.MIDDLE,
                                        line + LINE_SEPARATOR
                                    )
                                )
                            }
                        }
                    } else parents.add(Element.BlockCode(Element.BlockCode.Type.SINGLE, text))

                    lastStartIndex = endIndex
                }
            }
        }

        if (lastStartIndex < string.length) {
            val text = string.subSequence(lastStartIndex, string.length)
            parents.add(Element.Text(text))
        }

        return parents
    }
}

data class MarkdownText(val elements: List<Element>)

sealed class Element {
    abstract val text: CharSequence
    abstract val elements: List<Element>

    data class Text(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class UnorderedListItem(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Header(
        val level: Int = 1,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Quote(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Italic(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Bold(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Strike(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Rule(
        override val text: CharSequence = " ", // for insert span
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class InlineCode(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Link(
        val link: String,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class OrderedListItem(
        val order: String,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class BlockCode(
        val type: Type = Type.MIDDLE,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element() {
        enum class Type { START, END, MIDDLE, SINGLE }
    }
}