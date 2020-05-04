package ru.skillbranch.skillarticles.data

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import ru.skillbranch.skillarticles.R
import java.util.*

object LocalDataHolder {
    private var isDelay = true
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val articleData = MutableLiveData<ArticleData?>(null)
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val articleInfo = MutableLiveData<ArticlePersonalInfo?>(null)
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val settings = MutableLiveData(AppSettings())

    private val searchSettings = MutableLiveData<SearchData>(SearchData())

    fun findArticle(articleId: String): LiveData<ArticleData?> {
        GlobalScope.launch {
            if (isDelay) delay(1000)
            withContext(Dispatchers.Main){
                articleData.value = ArticleData(
                        title = "CoordinatorLayout Basic",
                        category = "Android",
                        categoryIcon = R.drawable.logo,
                        date = Date(),
                        author = "Skill-Branch"
                    )
            }

        }
        return articleData

    }

    fun findArticlePersonalInfo(articleId: String): LiveData<ArticlePersonalInfo?> {
        GlobalScope.launch {
            if (isDelay) delay(500)
            withContext(Dispatchers.Main){
                articleInfo.value = ArticlePersonalInfo(isBookmark = true)
            }
        }
        return articleInfo
    }

    fun getAppSettings() = settings

    fun getSearchData() = searchSettings

    fun updateAppSettings(appSettings: AppSettings) {
        settings.value = appSettings
    }

    fun updateArticlePersonalInfo(info: ArticlePersonalInfo) {
        articleInfo.value = info
    }

    fun updateSearchData(searchData: SearchData) {
       searchSettings.value = searchData
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearData(){
        articleInfo.postValue(null)
        articleData.postValue(null)
        settings.postValue(AppSettings())
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun disableDelay(value:Boolean = false) {
        isDelay = !value
    }
}

object NetworkDataHolder {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val content = MutableLiveData<List<Any>?>(null)
    private var isDelay = true

    fun loadArticleContent(articleId: String): LiveData<List<Any>?> {
        GlobalScope.launch {
            if (isDelay) delay(1500)
            withContext(Dispatchers.Main){
                content.value = listOf(longText)
            }

        }
        return content
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun disableDelay(value:Boolean = false) {
        isDelay = !value
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearData(){
        content.postValue(null)
    }
}

data class ArticleData(
    val shareLink: String? = null,
    val title: String? = null,
    val category: String? = null,
    val categoryIcon: Any? = null,
    val date: Date,
    val author: Any? = null,
    val poster: String? = null,
    val content: List<Any> = emptyList()
)

data class ArticlePersonalInfo(
    val isLike: Boolean = false,
    val isBookmark: Boolean = false
)

data class AppSettings(
    val isDarkMode: Boolean = false,
    val isBigText: Boolean = false
)

data class SearchData(
    val isSearch: Boolean = false,
    val queryString: String? = null
)

val longText: String = """
    long long text content
""".trimIndent()
