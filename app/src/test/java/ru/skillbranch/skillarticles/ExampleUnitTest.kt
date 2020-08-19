package ru.skillbranch.skillarticles

import org.junit.Test

import org.junit.Assert.*
import ru.skillbranch.skillarticles.markdown.Element
import ru.skillbranch.skillarticles.markdown.MarkdownParser

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun parse_list_item() {
        val result = MarkdownParser.parse(unorderedListString)
        val actual = prepare<Element.UnorderedListItem>(result.elements)
        assertEquals(expectedUnorderedList, actual)

        printResults(actual)
        println("")
        printElements(result.elements)
    }

    @Test
    fun parse_header_item() {
        val result = MarkdownParser.parse(headerString)
        val actual = prepare<Element.Header>(result.elements)
        val actualLevels = result.elements
            .filterIsInstance(Element.Header::class.java)
            .map { it.level }
        assertEquals(expectedHeader, actual)
        assertEquals((1..6).toList(), actualLevels)

        printResults(actual)
        println("")
        printElements(result.elements)
    }

    @Test
    fun parse_quote_item() {
        val result = MarkdownParser.parse(quoteString)
        val actual = prepare<Element.Quote>(result.elements)
        assertEquals(expectedQuote, actual)

        printResults(actual)
        println("")
        printElements(result.elements)
    }

    @Test
    fun parse_italic() {
        val result = MarkdownParser.parse(italicString)
        val actual = prepare<Element.Italic>(result.elements)
        assertEquals(expectedItalic, actual)

        printResults(actual)
        println("")
        printElements(result.elements)
    }

    @Test
    fun parse_bold() {
        val result = MarkdownParser.parse(boldString)
        val actual = prepare<Element.Bold>(result.elements)
        assertEquals(expectedBold, actual)

        printResults(actual)
        println("")
        printElements(result.elements)
    }

    @Test
    fun parse_strike() {
        val result = MarkdownParser.parse(strikeString)
        val actual = prepare<Element.Strike>(result.elements)
        assertEquals(expectedStrike, actual)

        printResults(actual)
        println("")
        printElements(result.elements)
    }

    @Test
    fun parse_code_block() {
        val result = MarkdownParser.parse(codeBlockString)
        val actualStrings = prepare<Element.BlockCode>(result.elements)
        val actualTypes = result.elements
            .filterIsInstance(Element.BlockCode::class.java)
            .map { it.type.toString() }

        assertEquals(expectedCodeBlockStrings, actualStrings)
        assertEquals(expectedCodeBlockTypes, actualTypes)

        printResults(actualStrings)
        println("")
        printElements(result.elements)
    }

    @Test
    fun parse_combine() {
        val result = MarkdownParser.parse(combineEmphasisString)
        val actualItalic = prepare<Element.Italic>(result.elements)
        val actualBold = prepare<Element.Bold>(result.elements)
        val actualStrike = prepare<Element.Strike>(result.elements)
        assertEquals(expectedCombine["italic"], actualItalic)
        assertEquals(expectedCombine["bold"], actualBold)
        assertEquals(expectedCombine["strike"], actualStrike)

        printResults(actualItalic)
        printResults(actualBold)
        printResults(actualStrike)
        println("")
        printElements(result.elements)
    }

    @Test
    fun parse_rule() {
        val result = MarkdownParser.parse(ruleString)
        val actual = prepare<Element.Rule>(result.elements)
        assertEquals(3, actual.size)

        printResults(actual)
        println("")
        printElements(result.elements)
    }

    @Test
    fun parse_inline_code() {
        val result = MarkdownParser.parse(inlineString)
        val actual = prepare<Element.InlineCode>(result.elements)
        assertEquals(expectedInline, actual)

        printResults(actual)
        println("")
        printElements(result.elements)
    }

    @Test
    fun parse_link() {
        val result = MarkdownParser.parse(linkString)
        val actualTitle = prepare<Element.Link>(result.elements)
        val actualLink = result.elements.spread()
            .filterIsInstance(Element.Link::class.java)
            .map { it.link }
        assertEquals(expectedLink["titles"], actualTitle)
        assertEquals(expectedLink["links"], actualLink)

        printResults(actualTitle)
        printResults(actualLink)
        println("")
        printElements(result.elements)
    }

    @Test
    fun parse_ordered_list_item() {
        val result = MarkdownParser.parse(orderedListString)
        val actual = prepare<Element.OrderedListItem>(result.elements)
        assertEquals(expectedOrderedList, actual)

        printResults(actual)
        println("")
        printElements(result.elements)
    }

    @Test
    fun parse_all() {
        val result = MarkdownParser.parse(markdownString)
        val actualUnorderedList = prepare<Element.UnorderedListItem>(result.elements)
        val actualHeaders = prepare<Element.Header>(result.elements)
        val actualQuotes = prepare<Element.Quote>(result.elements)
        val actualItalic = prepare<Element.Italic>(result.elements)
        val actualBold = prepare<Element.Bold>(result.elements)
        val actualStrike = prepare<Element.Strike>(result.elements)
        val actualRule = prepare<Element.Rule>(result.elements)
        val actualInline = prepare<Element.InlineCode>(result.elements)
        val actualLinkTitles = prepare<Element.Link>(result.elements)
        val actualLinks = result.elements.spread()
            .filterIsInstance<Element.Link>()
            .map { it.link }

        assertEquals(expectedMarkdown["unorderedList"], actualUnorderedList)
        assertEquals(expectedMarkdown["header"], actualHeaders)
        assertEquals(expectedMarkdown["quote"], actualQuotes)
        assertEquals(expectedMarkdown["italic"], actualItalic)
        assertEquals(expectedMarkdown["bold"], actualBold)
        assertEquals(expectedMarkdown["strike"], actualStrike)
        assertEquals(3, actualRule.size)
        assertEquals(expectedMarkdown["inline"], actualInline)
        assertEquals(expectedMarkdown["linkTitles"], actualLinkTitles)
        assertEquals(expectedMarkdown["links"], actualLinks)
    }

    @Test
    fun clear_all() {
        val result = MarkdownParser.clear(markdownString)
        assertEquals(markdownClearString,  result)
    }

    //optionally (delete @Ignore fo run)
//    @Ignore
    @Test
    fun clear_all_with_optionally() {
        val result = MarkdownParser.clear(markdownString)
        assertEquals(markdownOptionallyClearString,  result)
    }

    //optionally (delete @Ignore fo run)
//    @Ignore
    @Test
    fun parse_all_with_optionally() {
        val result = MarkdownParser.parse(markdownString)
        val actualUnorderedList = prepare<Element.UnorderedListItem>(result.elements)
        val actualHeaders = prepare<Element.Header>(result.elements)
        val actualQuotes = prepare<Element.Quote>(result.elements)
        val actualItalic = prepare<Element.Italic>(result.elements)
        val actualBold = prepare<Element.Bold>(result.elements)
        val actualStrike = prepare<Element.Strike>(result.elements)
        val actualRule = prepare<Element.Rule>(result.elements)
        val actualInline = prepare<Element.InlineCode>(result.elements)
        val actualLinkTitles = prepare<Element.Link>(result.elements)
        val actualLinks = result.elements.spread()
            .filterIsInstance<Element.Link>()
            .map { it.link }
//        val actualBlockCode = prepare<Element.BlockCode>(result.elements) //optionally
        val actualOrderedList = prepare<Element.OrderedListItem>(result.elements) //optionally

        assertEquals(expectedMarkdown["unorderedList"], actualUnorderedList)
        assertEquals(expectedMarkdown["header"], actualHeaders)
        assertEquals(expectedMarkdown["quote"], actualQuotes)
        assertEquals(expectedMarkdown["italic"], actualItalic)
        assertEquals(expectedMarkdown["bold"], actualBold)
        assertEquals(expectedMarkdown["strike"], actualStrike)
        assertEquals(3, actualRule.size)
        assertEquals(expectedMarkdown["inline"], actualInline)
        assertEquals(expectedMarkdown["linkTitles"], actualLinkTitles)
        assertEquals(expectedMarkdown["links"], actualLinks)
//        assertEquals(expectedMarkdown["multiline"], actualBlockCode) //optionally
        assertEquals(expectedMarkdown["orderedList"], actualOrderedList) //optionally
    }

    private fun printResults(strings: List<String>) {
        val iterator = strings.iterator()
        while (iterator.hasNext()) {
            println("find >> ${iterator.next()}")
        }
    }

    private fun printElements(elements: List<Element>) {
        val iterator = elements.iterator()
        while (iterator.hasNext()) {
            println("element >> ${iterator.next()}")
        }
    }

    private fun Element.spread(): List<Element> {
        val elements = mutableListOf<Element>()
        elements.add(this)
        elements.addAll(this.elements.spread())
        return elements
    }

    private fun List<Element>.spread(): List<Element> {
        val elements = mutableListOf<Element>()

        if (this.isNotEmpty()) elements.addAll(
            this.fold(mutableListOf()) { acc, el ->
                acc.also {
                    it.addAll(el.spread())
                }
            }
        )
        return elements
    }

    private inline fun <reified T: Element> prepare(list: List<Element>): List<String> {
        return list
            .fold(mutableListOf<Element>()) { acc, el -> // spread inner elements
                acc.also { it.addAll(el.spread()) }
            }
            .filterIsInstance<T>() // filter only expected instance
            .map { it.text.toString() } // transform to element text
    }

}
