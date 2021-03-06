package cn.ryths.blog.app.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.ryths.blog.app.R
import cn.ryths.blog.app.entity.Article
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat

class ArticleListAdapter : RecyclerView.Adapter<ArticleListAdapter.ArticleViewHolder>() {
    private var listener: ItemListener? = null


    /**
     * 存放文章数据
     */
    private var articles: List<Article> = ArrayList()

    /**
     * 头部
     */
    private var headers: List<View> = ArrayList()

    /**
     * 尾部
     */
    private var footers: List<View> = ArrayList()

    /**
     * 添加监听器
     */
    fun setListener(listener: ItemListener) {
        this.listener = listener
    }

    /**
     * 添加头
     */
    fun addHeader(view: View) {
        headers += view
        //更新视图
        this.notifyItemChanged(this.headers.size - 1)
    }

    /**
     * 添加尾部
     */
    fun addFooter(view: View) {
        footers += view
        //更新视图
        this.notifyItemChanged(this.headers.size + this.articles.size + this.footers.size - 1)
    }

    /**
     * 添加文章列表
     */
    fun addAll(articles: List<Article>) {
        this.articles += articles
        //更新视图
        this.notifyItemChanged(this.headers.size + this.articles.size - articles.size - 1, articles.size)
    }

    /**
     * 重新设置文章列表
     */
    fun setAll(articles: List<Article>) {
        this.articles = articles
        //更新视图
        this.notifyDataSetChanged()
    }


    /**
     * 清空文章列表
     */
    fun removeAll() {
        this.articles = ArrayList()
        //更新视图
        this.notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        //如果是头部或者尾部，不需要绑定数据
        if (position < this.headers.size || position >= this.headers.size + this.articles.size) {
            return
        }
        holder.bindArticle(articles[position - this.headers.size])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        //如果是头部
        if (viewType < this.headers.size) {
            return ArticleViewHolder(headers[viewType])
        }

        val pos1 = viewType - this.headers.size - this.articles.size
        //如果是尾部
        if (pos1 >= 0) {
            return ArticleViewHolder(footers[pos1])
        }

        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.article_list_item, parent, false)
        //如果监听器存在，则监听当前view的点击事件
        if (listener != null) {
            view.setOnClickListener {
                val pos2 = viewType - this.headers.size
                listener!!.onItemClick(it, articles[pos2],viewType)
            }
        }
        return ArticleViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.articles.size + headers.size + footers.size
    }

    override fun getItemViewType(position: Int): Int {
        //使用position作为类型
        return position
    }


    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindArticle(article: Article) {
            //获取view
            val poster = itemView.findViewById<ImageView>(R.id.list_item_poster)
//            val avatar = itemView.findViewById<CircleImageView>(R.id.list_item_author_avatar)
//            val nickname = itemView.findViewById<TextView>(R.id.list_item_author_nickname)
            val title = itemView.findViewById<TextView>(R.id.list_item_title)
            val summary = itemView.findViewById<TextView>(R.id.list_item_summary)
            val createDate = itemView.findViewById<TextView>(R.id.list_item_createDate)
            val readNum = itemView.findViewById<TextView>(R.id.list_item_readNum)
            val praiseNum = itemView.findViewById<TextView>(R.id.list_item_praiseNum)
            val categoryName = itemView.findViewById<TextView>(R.id.list_item_category_name)
            //设置数据
            //设置图片
            Picasso.with(itemView.context)
                    .load(article.poster)
                    .into(poster)
            //标题
            title.text = article.title
//            //设置昵称
//            nickname.text = article.author!!.nickname
//            //设置头像
//            Picasso.with(itemView.context)
//                    .load(article.author!!.avatar)
//                    .into(avatar)

            //概要
            summary.text = article.summary

            //发表时间
            val date = article.createDate
            val dataStr = SimpleDateFormat("yyyy/MM/dd HH:mm").format(date)
            createDate.text = dataStr
            praiseNum.text = article.praiseNum.toString()
            readNum.text = article.readNum.toString()
            categoryName.text = article.category!!.name
        }


    }

    /**
     * 监听器
     */
    interface ItemListener {
        /**
         * 每个item的点击事件，
         * [view] 被点击的view
         * [article] 对应的文章
         * [position] 该view在列表中的位置
         */
        fun onItemClick(view: View, article: Article,position:Int)
    }

}