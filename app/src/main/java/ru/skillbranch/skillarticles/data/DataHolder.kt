package ru.skillbranch.skillarticles.data

import android.os.Parcel
import android.os.Parcelable
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
    fun updateAppSettings(appSettings: AppSettings) {
        settings.value = appSettings
    }

    fun updateArticlePersonalInfo(info: ArticlePersonalInfo) {
        articleInfo.value = info
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

data class SearchMode(
    val isSearchOpen: Boolean = false,
    val queryString: String? = null
): Parcelable {

    companion object CREATOR: Parcelable.Creator<SearchMode> {
        override fun createFromParcel(source: Parcel?): SearchMode {
            val isSearchOpen = source?.readBoolean() ?: false
            val queryString = source?.readString()
            return SearchMode(isSearchOpen, queryString)
        }

        override fun newArray(size: Int): Array<SearchMode> = emptyArray()
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply {
            writeBoolean(isSearchOpen)
            writeString(queryString)
        }
    }

    override fun describeContents(): Int = 0
}

val longText: String = """
    long long text content
""".trimIndent()
