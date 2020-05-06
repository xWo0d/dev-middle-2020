package ru.skillbranch.skillarticles.ui

interface IArticleView {

    /**
     * отрисовать все вхождения поискового запроса в контенте (spannable)
     */
    fun renderSearchResult(searchResult: List<Pair<Int, Int>>)

    /**
     * отрисовать текущее положение поиска и перевести вокус на него (spannable)
     */
    fun renderSearchPosition(searchPosition: Int)

    /**
     * очистить результаты поиска (удалить все spannable)
     */
    fun clearSearchResult()

    /**
     * показать search bar
     */
    fun showSearchBar()

    /**
     * скрыть search bar
     */
    fun hideSearchBar()

}