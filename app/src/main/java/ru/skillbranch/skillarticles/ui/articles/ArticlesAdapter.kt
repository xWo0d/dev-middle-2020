package ru.skillbranch.skillarticles.ui.articles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_article.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.models.ArticleItemData
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.format
import ru.skillbranch.skillarticles.ui.custom.CheckableImageView

class ArticlesAdapter(
    private val onItemClickListener: (ArticleItemData) -> Unit,
    private val onBookmarkClickListener: (ArticleItemData) -> Unit
): PagedListAdapter<ArticleItemData, ArticleVH>(
    ArticleDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleVH {
        val containerView = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return ArticleVH(containerView)
    }

    override fun onBindViewHolder(holder: ArticleVH, position: Int) {
        holder.bind(
            getItem(position),
            onItemClickListener,
            onBookmarkClickListener
        )
    }
}

class ArticleDiffCallback: DiffUtil.ItemCallback<ArticleItemData>() {
    override fun areItemsTheSame(oldItem: ArticleItemData, newItem: ArticleItemData): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ArticleItemData, newItem: ArticleItemData): Boolean {
        return oldItem == newItem
    }
}

class ArticleVH(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {

    // item may be null when we use placeholder
    fun bind(
        item: ArticleItemData?,
        listener: (ArticleItemData) -> Unit,
        onBookmarkClickListener: (ArticleItemData) -> Unit
    ) {
        val posterSize = containerView.context.dpToIntPx(64)
        val cornerRadius = containerView.context.dpToIntPx(8)
        val categorySize = containerView.context.dpToIntPx(40)

        Glide.with(containerView.context)
            .load(item?.poster)
            .transform(CenterCrop(), RoundedCorners(cornerRadius))
            .override(posterSize)
            .into(iv_poster)

        Glide.with(containerView.context)
            .load(item?.categoryIcon)
            .transform(CenterCrop(), RoundedCorners(cornerRadius))
            .override(categorySize)
            .into(iv_category)

        tv_date.text = item?.date?.format()
        tv_author.text = item?.author
        tv_title.text = item?.title
        tv_description.text = item?.description
        tv_likes_count.text = "${item?.likeCount}"
        tv_comments_count.text = "${item?.commentCount}"
        tv_read_duration.text = "${item?.readDuration}"

        iv_bookmark.isChecked = item?.isBookmark ?: false
        iv_bookmark.setOnClickListener {
            (it as CheckableImageView).toggle()
            onBookmarkClickListener(item!!) }

        itemView.setOnClickListener { listener(item!!) }
    }
}