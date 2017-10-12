package cn.ryths.blog.app.view.article.article

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import cn.ryths.blog.app.R
import cn.ryths.blog.app.entity.Article
import cn.ryths.blog.app.presenter.ArticlePresenter
import cn.ryths.blog.app.presenter.PresenterCallback
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ArticleFragment : Fragment() {


    private lateinit var articlePresenter: ArticlePresenter

    private lateinit var toolbar: Toolbar

    private lateinit var authorAvatar: CircleImageView

    private lateinit var authorNickname: TextView

    private lateinit var authorSummary: TextView

    private var articleId: Long = 0

    private lateinit var content: WebView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_article_info, container, false)

        //实例化articlePresenter
        articlePresenter = ArticlePresenter()

        toolbar = view.findViewById(R.id.article_info_toolbar)
        authorAvatar = view.findViewById(R.id.article_info_author_avatar)
        authorNickname = view.findViewById(R.id.article_info_author_nickname)
        authorSummary = view.findViewById(R.id.article_info_author_summary)
        content = view.findViewById(R.id.article_info_content)
        //支持js
        content.settings.javaScriptEnabled = true

        //获取articleId
        articleId = arguments.getLong("articleId")
        getArticle()
        //初始化事件
        initEvent()

        return view
    }

    /**
     * 初始化事件
     */
    private fun initEvent() {
        //为返回键添加事件
        toolbar.setNavigationOnClickListener{
            activity.finish()
        }
    }

    /**
     * 获取文章
     */
    private fun getArticle() {
        articlePresenter.findById(articleId, object : PresenterCallback<Article?, Void?> {
            override fun success(result: Article?) {
                //result为null说明当前文章不存在，此时给出提示，同时返回前一页
                if (result == null) {
                    Toast.makeText(activity, "你访问的文章不存在", 2000).show()
                    activity.finish()
                    return
                }
                toolbar.title = result.title
                Picasso.with(activity)
                        .load(result.author!!.avatar)
                        .into(authorAvatar)
                authorNickname.text = result.author!!.nickname
                authorSummary.text = result.author!!.summary

                //设置js调用
                content.addJavascriptInterface(JsClient(result.content), "JsClient")

                //加载html
                content.loadUrl("file:///android_asset/markdown.html")

            }

            override fun fail(error: Void?) {
                Toast.makeText(activity, "获取数据失败，请重试", Toast.LENGTH_SHORT).show()
            }

        })
    }

    /**
     * js调用的api
     */
    class JsClient(private val content: String?) {
        /**
         * 获取文章内容
         */
        @JavascriptInterface
        fun getContent() = content
    }
}