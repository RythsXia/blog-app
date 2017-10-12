package cn.ryths.blog.app.view.tab

import android.app.Fragment
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import cn.ryths.blog.app.R
import cn.ryths.blog.app.presenter.ArticlePresenter
import cn.ryths.blog.app.view.tab.category.CategoryFragment
import cn.ryths.blog.app.view.tab.index.IndexFragment
import cn.ryths.blog.app.view.tab.setting.SettingFragment


class TabActivity : AppCompatActivity() {

    val articlePresenter = ArticlePresenter()
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initEvent()
        //显示第一个视图
        tabActive(0)
    }

    private var tabs: List<Button> = ArrayList()
    private var fragments: List<Fragment> = ArrayList()

    private fun initView() {
        tabs += findViewById(R.id.tab_index) as Button
        fragments += IndexFragment()
        tabs += findViewById(R.id.tab_category) as Button
        fragments += CategoryFragment()
        tabs += findViewById(R.id.tab_setting) as Button
        fragments += SettingFragment()
    }

    private fun initEvent() {
        for ((index, tab) in tabs.withIndex()) {
            tab.setOnClickListener {
                tabActive(index)
            }
        }
    }

    private fun tabActive(index: Int) {
        val transaction = fragmentManager.beginTransaction()
        //隐藏所有
        fragments.forEach {
            if (it.isVisible) {
                transaction.hide(it)
            }
        }
        //取消tab颜色
        tabs.forEach {
            it.compoundDrawables[1].setTint(Color.BLACK)
        }
        //判断是否存在当前fragment
        val fragment = fragmentManager.findFragmentByTag("fragment${index}")
        if (fragment == null) {
            transaction.add(R.id.fragment_tab, fragments[index], "fragment${index}")
        }
        //设置tab颜色
        tabs[index].compoundDrawables[1].setTint(resources.getColor(R.color.primary))
        //显示当前
        transaction.show(fragments[index])
        transaction.commit()
    }
}
