package ru.skillbranch.skillarticles.viewmodels.bookmarks

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.skillbranch.skillarticles.data.models.ArticleItemData
import ru.skillbranch.skillarticles.data.repositories.ArticleDataFactory
import ru.skillbranch.skillarticles.data.repositories.ArticleStrategy
import ru.skillbranch.skillarticles.data.repositories.ArticlesRepository
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.Notify
import java.util.concurrent.Executors

class BookmarksViewModel(handle: SavedStateHandle) :
    BaseViewModel<BookmarksState>(handle, BookmarksState()) {
    private val repository = ArticlesRepository()

    private val listConfig by lazy {
        PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(10)
            .setPrefetchDistance(30)
            .setInitialLoadSizeHint(50)
            .build()
    }

    private val listData = Transformations.switchMap(state) {
        when {
            it.isSearch && !it.searchQuery.isNullOrEmpty() -> buildPagedList(
                repository.searchBookmarkArticles(it.searchQuery)
            )
            else -> buildPagedList(repository.bookmarkArticles())
        }
    }

    fun observeList(
        owner: LifecycleOwner,
        onChange: (list: PagedList<ArticleItemData>) -> Unit
    ) {
        listData.observe(owner, Observer { onChange(it) })
    }

    private fun buildPagedList(
        dataFactory: ArticleDataFactory
    ): LiveData<PagedList<ArticleItemData>> {
        val builder = LivePagedListBuilder<Int, ArticleItemData>(
            dataFactory,
            listConfig
        )

        // if all articles
        if (dataFactory.strategy is ArticleStrategy.BookmarkArticles) {
            builder.setBoundaryCallback(
                ArticlesBoundaryCallback(
                    ::zeroLoadedHandle,
                    ::itemAtEndHandle
                )
            )
        }

        return builder
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .build()
    }

    private fun itemAtEndHandle(lastLoadedArticle: ArticleItemData) {
        Log.e("BookmarksViewModel", "itemAtEndHandle:")
        viewModelScope.launch(Dispatchers.IO) {
            val items = repository.loadArticlesFromDb(
                lastLoadedArticle.id.toInt().inc(),
                listConfig.pageSize
            )

            withContext(Dispatchers.Main) {
                notify(
                    Notify.TextMessage(
                        "Load from DB articles from " +
                                "${items.firstOrNull()?.id} to ${items.lastOrNull()?.id}"
                    )
                )
            }
        }
    }

    private fun zeroLoadedHandle() {
        Log.e("ArticleViewModel", "zeroLoadedHandle:")
        notify(Notify.TextMessage("Storage is empty"))
        viewModelScope.launch(Dispatchers.IO) {
            val items =
                repository.loadArticlesFromDb(start = 0, size = listConfig.initialLoadSizeHint)
        }
    }

    fun handleSearchMode(isSearch: Boolean) {
        updateState { it.copy(isSearch = isSearch) }
    }

    fun handleSearch(query: String?) {
        query ?: return
        updateState { it.copy(searchQuery = query) }
    }

    fun handleToggleBookmark(id: String, isChecked: Boolean) {
        repository.updateBookmark(id, isChecked)
    }
}

data class BookmarksState(
    val isSearch: Boolean = false,
    val searchQuery: String? = null,
    val isLoading: Boolean = true
) : IViewModelState

class ArticlesBoundaryCallback(
    private val zeroLoadedHandle: () -> Unit,
    private val itemAtEndHandle: (ArticleItemData) -> Unit
) : PagedList.BoundaryCallback<ArticleItemData>() {
    override fun onZeroItemsLoaded() {
        // storage is empty
        zeroLoadedHandle.invoke()
    }

    override fun onItemAtEndLoaded(itemAtEnd: ArticleItemData) {
        // user scrolled down -> need load more items
        itemAtEndHandle.invoke(itemAtEnd)
    }
}