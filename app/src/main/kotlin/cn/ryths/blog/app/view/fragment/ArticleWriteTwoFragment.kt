package cn.ryths.blog.app.view.fragment

import android.app.Fragment
import android.content.Intent
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import cn.ryths.blog.app.R
import cn.ryths.blog.app.api.Api
import cn.ryths.blog.app.api.ArticleApi
import cn.ryths.blog.app.databinding.FragmentArticleWriteTwoBinding
import cn.ryths.blog.app.entity.Code
import cn.ryths.blog.app.utils.FileUtils
import cn.ryths.blog.app.view.activity.ArticleWriteActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import java.io.File

class ArticleWriteTwoFragment : Fragment() {

    private lateinit var binding: FragmentArticleWriteTwoBinding
    private val FILE_SELECT_CODE = 0

    private val viewModel = ViewModel()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_article_write_two, container, false)

        //获取activity中articleDto数据，存入viewModel
        val article = (activity as ArticleWriteActivity).article

        viewModel.summary = article.summary
        viewModel.poster = article.poster

        binding.viewModel = viewModel

        binding.fragmentArticleWriteTwoToolbar.setNavigationOnClickListener {
            activity.finish()
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            FILE_SELECT_CODE -> {
                if (data == null) {
                    return
                }
                val uri = data.data
                viewModel.poster = FileUtils.getPath(activity, uri)
            }
        }
    }

    inner class ViewModel : BaseObservable() {

        @Bindable
        var poster: String? = null
            set(value) {
                field = value
                canNext = checkNext()
                this.notifyPropertyChanged(cn.ryths.blog.app.BR.poster)
                this.notifyPropertyChanged(cn.ryths.blog.app.BR.canNext)
            }

        @Bindable
        var summary: String? = null
            set(value) {
                field = value
                canNext = checkNext()
                this.notifyPropertyChanged(cn.ryths.blog.app.BR.summary)
                this.notifyPropertyChanged(cn.ryths.blog.app.BR.canNext)
            }

        @Bindable
        var canNext: Boolean = false

        fun checkNext() = !poster.isNullOrBlank() && !summary.isNullOrBlank()

        fun choosePoster() {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, FILE_SELECT_CODE)
        }

        fun next() {
            val article = (activity as ArticleWriteActivity).article

            article.poster = poster
            article.summary = summary

            val articleApi = Api.newApiInstance(ArticleApi::class.java)
            val posterFile = File(article.poster)
            val contentType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(poster!!.substring(poster!!.lastIndexOf(".") + 1))
            val posterBody = MultipartBody.create(MediaType.parse(contentType), posterFile)
            val part = MultipartBody.Part.createFormData("posterFile",posterFile.name,posterBody)
            articleApi.add(article.title!!, article.summary!!, article.content!!, article.categoryId!!, part)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it.code == Code.SUCCESS) {
                            this@ArticleWriteTwoFragment.activity.finish()
                        }
                    }, {})


        }

        fun back() {
            //保存当前输入状态
            val article = (activity as ArticleWriteActivity).article
            article.summary = viewModel.summary
            article.poster = viewModel.poster
            fragmentManager.popBackStack()
        }
    }
}