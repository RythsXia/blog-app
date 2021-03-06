package cn.ryths.blog.app.view.fragment

import android.app.Fragment
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.ryths.blog.app.R
import cn.ryths.blog.app.api.Api
import cn.ryths.blog.app.api.ArticleApi
import cn.ryths.blog.app.databinding.FragmentArticleListBinding
import cn.ryths.blog.app.entity.Article
import cn.ryths.blog.app.view.activity.ArticleActivity
import cn.ryths.blog.app.view.adapter.ArticleListAdapter
import cn.ryths.blog.app.view.adapter.RollViewAdapter
import com.jude.rollviewpager.RollPagerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ArticleListFragment : Fragment() {

    private val articleApi = Api.newApiInstance(ArticleApi::class.java)

    private lateinit var binding: FragmentArticleListBinding

    private val pageSize = 10
    private var currentPage = 0


    private lateinit var articleListAdapter: ArticleListAdapter

    private lateinit var rollView: RollPagerView

    private lateinit var rollViewAdapter: RollViewAdapter

    /**
     * 存放传进来的数据，类型需要根据加载类型确定
     */
    private var data: Any? = null

    private var code = CODE_DEFAULT


    companion object {
        fun newInstance(code: Int = CODE_DEFAULT, data: Any? = null): ArticleListFragment {
            val fragment = ArticleListFragment()
            fragment.code = code
            fragment.data = data
            return fragment
        }

        /**
         * 默认加载数据的方式
         */
        val CODE_DEFAULT = 0
        /**
         * 加载指定分类下的数据
         */
        val CODE_CATEGORY = 1
        /**
         * 加载当前登录用户的文章
         */
        val CODE_SELF = 2
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_article_list, parent, false)
        binding.indexRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        articleListAdapter = ArticleListAdapter()

        if (code == CODE_DEFAULT) {
            rollView = RollPagerView(activity)
            val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, activity.windowManager.defaultDisplay.height / 4)
            rollView.layoutParams = layoutParams
            rollView.setPlayDelay(2000)
            rollViewAdapter = RollViewAdapter(rollView)
            rollViewAdapter.setListener(object : RollViewAdapter.ItemListener {
                override fun onItemClick(view: View, article: Article) {
                    gotoInfo(article.id!!)
                }

            })
            rollView.setAdapter(rollViewAdapter)
            freshRollView()

            articleListAdapter.addHeader(rollView)
        }

        freshOrUpdateList(false)

        binding.indexRefreshLayout.setOnRefreshListener {
            //重置当前页
            currentPage = 0
            if (code == CODE_DEFAULT) {
                freshRollView()
            }
            freshOrUpdateList(false)
        }

        binding.indexRefreshLayout.setOnLoadmoreListener {
            currentPage += 1
            freshOrUpdateList(true)
        }

        articleListAdapter.setListener(object : ArticleListAdapter.ItemListener {
            override fun onItemClick(view: View, article: Article, position: Int) {
                gotoInfo(article.id!!)
            }

        })

        binding.indexRecyclerView.adapter = articleListAdapter

        return binding.root
    }

    private fun gotoInfo(id: Long) {
        val intent = Intent(activity, ArticleActivity::class.java)
        intent.putExtra("articleId", id)
        startActivity(intent)
    }

    private fun freshOrUpdateList(isAddMore: Boolean) {
        val obs = when (code) {
            CODE_CATEGORY -> articleApi.findAllByCategoryId(data as Long, currentPage, pageSize, true, true)
            CODE_SELF->articleApi.findAllSelf(currentPage, pageSize, true, true)
            else -> articleApi.findAll(currentPage, pageSize, true, true)
        }

        obs.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (isAddMore) {
                        articleListAdapter.addAll(it.data!!)
                    } else {
                        articleListAdapter.setAll(it.data!!)
                    }
                    binding.indexRefreshLayout.isEnableLoadmore = currentPage + 1 < it.pagination!!.totalPage!!
                    binding.indexRefreshLayout.finishRefresh()
                    binding.indexRefreshLayout.finishLoadmore()
                }, {})
    }

    private fun freshRollView() {
        articleApi.findAllRecommendation(0, 5, false, false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    rollViewAdapter.setAll(it.data!!)
                }, {})
    }

}