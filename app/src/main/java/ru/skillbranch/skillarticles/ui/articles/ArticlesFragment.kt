package ru.skillbranch.skillarticles.ui.articles

import android.util.Log
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_articles.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.ui.base.BaseFragment
import ru.skillbranch.skillarticles.ui.base.Binding
import ru.skillbranch.skillarticles.ui.delegates.RenderProp
import ru.skillbranch.skillarticles.viewmodels.articles.ArticlesState
import ru.skillbranch.skillarticles.viewmodels.articles.ArticlesViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.NavigationCommand

class ArticlesFragment : BaseFragment<ArticlesViewModel>() {

    override val viewModel: ArticlesViewModel by viewModels()
    override val layout: Int = R.layout.fragment_articles
    override val binding: ArticlesBinding by lazy { ArticlesBinding() }

    private val articlesAdapter =
        ArticlesAdapter() { item ->
            Log.e("ArticlesFragment", "click on article: ${item.id}")
            val direction = ArticlesFragmentDirections.actionNavArticlesToPageArticle(
                item.id,
                item.author,
                item.authorAvatar,
                item.category,
                item.categoryIcon,
                item.date,
                item.poster,
                item.title
            )

            viewModel.navigate(NavigationCommand.To(direction.actionId, direction.arguments))
        }

    override fun setupViews() {
        with(rv_articles) {
            layoutManager = LinearLayoutManager(context)
            adapter = articlesAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
        }

        viewModel.observeList(viewLifecycleOwner) {
            articlesAdapter.submitList(it)
        }
    }

    inner class ArticlesBinding : Binding() {
        var isFocusedSearch: Boolean = false
        var searchQuery: String? = null
        var isSearch: Boolean = false
        var isLoading: Boolean by RenderProp(true) {
            //TODO show shimmer on rv_list
        }

        override fun bind(data: IViewModelState) {
            data as ArticlesState
            isSearch = data.isSearch
            searchQuery = data.searchQuery
            isSearch = data.isSearch
        }

        //TODO save UI
    }
}